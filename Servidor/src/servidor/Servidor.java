/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */
package servidor;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import jade.core.*;
import jade.wrapper.*;
import java.util.Date;
import org.json.JSONObject;

public class Servidor {
    //Listas para guardar los clientes conectados al servidor
    static List<ServidorHilo> usuarios;  
    static List<ServidorHilo> sensores;  
    static AgentController AdminAgent;
    static Date InitialDate;
    static Thread UpdateSensorThread = new Thread(new Runnable() {
        @Override
        public  void run() {
            while(true){
                UpdateSensorInformation();
            }
            
        }
    });  
    
    public static void main(String[] args) {
        //Se inicializa listas de clientes
        usuarios = new ArrayList<ServidorHilo>();
        sensores = new ArrayList<ServidorHilo>();

        //InitializeAgents();
        
        ServerSocket ss;
        ServerSocket sshtml;
        System.out.print("Inicializando SERVIDOR... \n");
        
        try {
            //Se crea una nueva instancia de ServerSocket para recibir sensores
            InetAddress addr = InetAddress.getByName("192.168.1.6");
            ss = new ServerSocket(10578, 0, addr);
            System.out.print("Servidor SENSORES en el puerto " + 10578);
            System.out.println("\t[OK]");
            
            int idUsuario = 0;
            int idSensor = 0;
            
            /*Siempre espera nuevas conexiones, cuando identifica una nueva,
            crea una instancia de Socket y lo agrega a la lista de clientes*/
            System.out.println("Esperando...");
            InitialDate = new Date();
            UpdateSensorThread.start();
            
            while (true) {
                Socket socketSensor;
                socketSensor = ss.accept();
                int PuertoLocal = socketSensor.getLocalPort();
                System.out.println("Nueva conexión entrante (SENSOR): " + socketSensor + "\n");  
                ServidorHilo nCliente = new ServidorHilo(socketSensor, idSensor);
                sensores.add(nCliente);
                nCliente.start();
                idSensor++;
            }
        } 
        catch (IOException ex) {
            System.out.println("Error en el servidor\n" + ex.getMessage());
        }
    }
    
    public static void UpdateSensorInformation(){
        Date currentDate = new Date();
        
        long difMilisegundos = currentDate.getTime() - InitialDate.getTime(); //Diferencia en milisegundos
        long segundos = difMilisegundos / 1000;
        
        //Han pasaado 30 segundos o mas tiempo, se debera actualizar el json con la informacion  de cada uno de los sensores
        //Que estan conectados al servidor
        if(segundos >= 5){
            JSONObject[] jsonArray = new JSONObject[sensores.size()];
            for(int i = 0; i < sensores.size(); i++){
                ServidorHilo sensor = sensores.get(i);
                JSONObject jsonSensor = new JSONObject();
                jsonSensor.put("id",sensor.getIdCliente());
                jsonSensor.put("nombre",sensor.getNombre());
                jsonSensor.put("valor",sensor.getValue());
                jsonSensor.put("ip",sensor.getIP());
                jsonArray[i] = jsonSensor;
            }
            Helper.UpdateJsonData(jsonArray);
            InitialDate = new Date();
        }
    }
    
    public static void RemoveSensor(String idclientee){
        
        for(int i = 0; i<= sensores.size(); i++){
            ServidorHilo nsensor = sensores.get(i);
            if(nsensor.getIdCliente().equals(idclientee)){
                sensores.remove(i);
                break;
            }
        }
    }
    
    public static void InitializeAgents()
    {
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        Profile profile = new ProfileImpl();
        
        profile.setParameter(Profile.CONTAINER_NAME, "TestContainer");
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        ContainerController container = runtime.createAgentContainer(profile);
        
        try {
            AgentController ag = container.createNewAgent(
                    "a1",
                    "comunicacion.AdminAgent",
                    new Object[]{});//arguments
            ag.start();
            AdminAgent = ag;
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
