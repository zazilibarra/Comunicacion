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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    static String IP;
    
    public static void main(String[] args) {
        
        //Se inicializa listas de clientes
        usuarios = new ArrayList<>();
        sensores = new ArrayList<>();
        if(args.length > 0) IP = args[0];
        //InitializeAgents();
        
        ServerSocket ss;
        ServerSocket sshtml;
        try {
            //Se valida el valor obtenido, debera ser una direccion IP valida con el formato de IP
            if(IP != null){
                final String IP_ADDRESS_PATTERN =
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
                
                Pattern pattern = Pattern.compile(IP_ADDRESS_PATTERN);
                Matcher matcher = pattern.matcher(IP);
                boolean isValidIP = matcher.matches();
                
                if(isValidIP){
                    System.out.print("Inicializando SERVIDOR... \n");
                    //Se crea una nueva instancia de ServerSocket para recibir sensores
                    InetAddress addr = InetAddress.getByName(IP);
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
                }else{
                    System.out.println("Error: Debe ingresar una direccion IP valida");
                }

            }else{
                System.out.println("Error: Debe ingresar una direccion IP");
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
//        jade.core.Runtime runtime = jade.core.Runtime.instance();
//        Profile profile = new ProfileImpl();
//        
//        profile.setParameter(Profile.CONTAINER_NAME, "TestContainer");
//        profile.setParameter(Profile.MAIN_HOST, "localhost");
//        ContainerController container = runtime.createAgentContainer(profile);
//        
//        try {
//            AgentController ag = container.createNewAgent(
//                    "a1",
//                    "comunicacion.AdminAgent",
//                    new Object[]{});//arguments
//            ag.start();
//            AdminAgent = ag;
//        } catch (StaleProxyException e) {
//            e.printStackTrace();
//        }
    }
}
