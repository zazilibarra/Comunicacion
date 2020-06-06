/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import jade.core.Agent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import jade.core.behaviours.TickerBehaviour;
import java.io.File;
//import org.json.JSONObject;
import java.io.FileReader;
import java.util.Iterator;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.FileWriter;


/**
 *
 * @author Daniel
 */

//Este es el agente inteligente que tiene el servidor. Este agente se controla por medio de comportamientos que se activan cuando
//cierto evento es detectado, funcionando como si fuera inteligencia artificial.
public class AdminAgent extends Agent {
    
    private List<Socket> blackList = new ArrayList<Socket>();
    private Socket admin = null;
    private DataOutputStream dos;
    private BufferedReader entrada;
    JsonParser parser = new JsonParser();
    Iterator<JsonElement> iterator;
    JsonObject elemento, newElement;
    String nameActual, valActual;
    Float value;
    
    //Esta funcion se encarga de obtener la informacion de los sensores y detectar si algo no concuerda con los datos que deberian
    //de obtenerse
    protected void setup() {
        System.out.println("\n --> Hola, soy el agente: " + getAID().getName());
        
        addBehaviour(new TickerBehaviour(this, 1000) {
            
            protected void onTick(){
                //CHECAMOS LAS CONEXIONES EXISTENTES
                 File file = new File(".","/getinfo.json");
                 int fileLength = (int)file.length();
                 try {
                    Object obj = parser.parse(new FileReader("./getinfo.json"));
                    JsonElement jsonObject = (JsonElement) obj;
                    JsonArray jsonList = (JsonArray) jsonObject.getAsJsonArray();
                    iterator = jsonList.iterator();
                    while (iterator.hasNext()) {
                        elemento = iterator.next().getAsJsonObject();
                        nameActual = elemento.get("nombre").toString();
                        nameActual = nameActual.substring(1, nameActual.length() - 1);
                        if(nameActual.equals("KY-001")) {
                            valActual = elemento.get("value").toString();
                            valActual = valActual.substring(1, valActual.length() - 1);
                            value = Float.parseFloat(valActual);
                            if(value > 45.00)
                            {
                                System.out.println("La temperatura es demasiado alta, prende el aire acondicionado");
                                /*newElement = new JsonObject();
                                newElement.addProperty("id", "ERROR");
                                newElement.addProperty("nombre", "ERROR");
                                newElement.addProperty("value", "La temperatura es muy alta");
                                jsonList.add(newElement);*/
                            }     
                        }
		    }
                    
                    /*String data = jsonList.getAsString();
                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(fw);
            
                    bw.write(data);
                    bw.flush();
                    bw.close();  */ 
                    
                 } catch (Exception e) {
			e.printStackTrace();
		}
            }
        });
    }
    
    //Esta funcion obtiene los nuevos clientes conectados al sistema y les intenta asignar la administracion.
    public void AsignarAdmin(Socket newClient)
    {
        try {
            dos = new DataOutputStream(newClient.getOutputStream());
            //entrada = new BufferedReader(new InputStreamReader(newClient.getInputStream()));
        }
        catch(IOException e) {
            
        }
        if(admin != null && !blackList.contains(newClient))
        {
            //CODIGO PARA ENVIARLE AL CLIENTE LA PETICION DE ADMINISTRACION
        }
    }
    
    public void ObtenerRespuesta(Socket newClient, boolean answer)
    {
        if(answer && admin == null)
        {
            admin = newClient;
        }
        else
        {
            blackList.add(newClient);
        }
    }
}
