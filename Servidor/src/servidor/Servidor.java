/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */
package servidor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
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
import java.util.stream.Collectors;
import org.json.JSONObject;


//Esta es la clase que define al servidor y crea todos los elementos que va a controlar. El codigo se ejecutara en el
//centro del sistema, donde llegara toda la informacion y se almacenara en un archivo para su redireccion hacia cualquier
//parte de la arquitectura.
public class Servidor {
    //Listas para guardar los clientes conectados al servidor
    static List<Usuario> usuarios;  
    static List<ServidorHilo> sensores; 
    static List<String> topicos;
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
    
    //Clase: Servidor, crea todas las instancias de los parametros que va a utilizar el servidor y, posteriormente,
    //inicia el servidor en su propio socket para que los clientes puedan empezar a acceder a este.
    public static void main(String[] args) {
        
        //Se inicializa listas de clientes
        usuarios = new ArrayList<>();
        sensores = new ArrayList<>();
        topicos = new ArrayList<>();
        if(args.length > 0) IP = args[0];
        //InitializeAgents();
        
        ServerSocket SERVER_DATA_RECEIVER;

        try {
            //Se valida el valor obtenido, debera ser una direccion IP valida con el formato de IP
            if(IP != null){
                //Se valida que el valor sea una direccion IP valida
                boolean isValidIP =  Helper.validaIP(IP);
                
                if(isValidIP){
                    System.out.print("Inicializando SERVIDORES... \n\n");
                    //Se crea una nueva instancia de ServerSocket para recibir sensores
                    InetAddress addr = InetAddress.getByName(IP);
                    //Servidor de Datos
                    SERVER_DATA_RECEIVER = new ServerSocket(10578, 0, addr);
                    //Servidor Recibidor de Metodos POST
                    HttpServer SERVER_POST_RECEIVER = HttpServer.create(new InetSocketAddress(addr,8000), 0);
                    SERVER_POST_RECEIVER.createContext("/logmein", new ServidorHttp.PostHandler());
                    SERVER_POST_RECEIVER.setExecutor(null); // creates a default executor
                    SERVER_POST_RECEIVER.start();
                    //Servidor Interfaz HTTP
                    ServerSocket SERVER_HTTP = new ServerSocket(8080,0,addr);
                    
                    System.out.print("Servidor de Datos en el puerto " + 10578);
                    System.out.println("\t[OK]");
                    System.out.print("Servidor Login en el puerto " + 8000);
                    System.out.println("\t[OK]");

                    int idUsuario = 0;
                    int idSensor = 0;

                    InitialDate = new Date();
                    UpdateSensorThread.start();
                    //Se requiere que el servidor HTTP se ejecute en un hilo diferente para que los dos 
                    //servidores (Sensor y HTTP) se ejecuten de forma paralela
                    //El servidor HTTP es la interfaz IP:8080
                    new Thread(){
                        public void run(){
                            try {
                                System.out.print("Servidor HTTP en el puerto " + 8080);
                                System.out.println("\t[OK]");
                                while(!Thread.interrupted()){    
                                    ServidorHttp servidorWeb = new ServidorHttp(SERVER_HTTP.accept());
                                    servidorWeb.start();
                                }
                            } catch (IOException ex) {
                                System.out.println("Ha ocurrido un error en el servidor HTTP\n" + ex.getMessage());
                            }
                        }
                    }.start();

                    /*Siempre espera nuevas conexiones, cuando identifica una nueva,
                    crea una instancia de Socket y lo agrega a la lista de clientes*/
                    //Bucle infinito para recibir sensores
                    while (true) {
                        Socket socketSensor;
                        socketSensor = SERVER_DATA_RECEIVER.accept();
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
    
    //Esta funcion se encarga de tomar la informacion entrante de cada uno de los sensores y escribirla en un archivo JSON
    //para poder enviarla a los clientes cuando sea requerida y visualizarla.
    public static void UpdateSensorInformation(){
        Date currentDate = new Date();
        
        long difMilisegundos = currentDate.getTime() - InitialDate.getTime(); //Diferencia en milisegundos
        long segundos = difMilisegundos / 1000;
        
        //Han pasaado 30 segundos o mas tiempo, se debera actualizar el json con la informacion  de cada uno de los sensores
        //Que estan conectados al servidor
        if(segundos >= 10){
            
            //Los topicos se actualizan dependiendo de los sensores y sus topicos
            topicos = sensores.stream().map(s -> s.getTopico()).distinct().collect(Collectors.toList());
            System.out.println("NUEVA ACTUALIZACION: " + sensores.size() + " Sensores, " + usuarios.size() + " Usuarios, " + topicos.size() + " Topicos");
            JSONObject[] jsonArraySensores = new JSONObject[sensores.size()];
            JSONObject[] jsonArrayUsuarios = new JSONObject[usuarios.size()];
            JSONObject[] jsonArrayTopicos = new JSONObject[topicos.size()];
            
            for(int i = 0; i < sensores.size(); i++){
                ServidorHilo sensor = sensores.get(i);
                JSONObject jsonSensor = new JSONObject();
                jsonSensor.put("id",sensor.getIdCliente());
                jsonSensor.put("nombre",sensor.getNombre());
                jsonSensor.put("valor",sensor.getValue());
                jsonSensor.put("ip",sensor.getIP());
                jsonSensor.put("topico",sensor.getTopico());
                jsonArraySensores[i] = jsonSensor;
            }
            for(int i = 0; i < usuarios.size(); i++){
                Usuario usuario = usuarios.get(i);
                JSONObject jsonUsuario = new JSONObject();
                jsonUsuario.put("nombre",usuario.getNombre());
                jsonUsuario.put("ip",usuario.getIP());
                jsonArrayUsuarios[i] = jsonUsuario;
            }
            for(int i = 0; i < topicos.size(); i++){
                String topico = topicos.get(i);
                JSONObject jsonTopico = new JSONObject();
                jsonTopico.put("nombre",topico);
                jsonArrayTopicos[i] = jsonTopico;
            }
            Helper.UpdateJsonData(jsonArraySensores,"INFO");
            Helper.UpdateJsonData(jsonArrayUsuarios, "USERS");
            Helper.UpdateJsonData(jsonArrayTopicos, "TOPICS");
            InitialDate = new Date();
        }
    }
    
    //Funcion para remover un sensor de la arquitectura cuando este se vaya a desconectar.
    public static void RemoveSensor(String idclientee){
        
        for(int i = 0; i<= sensores.size(); i++){
            ServidorHilo nsensor = sensores.get(i);
            if(nsensor.getIdCliente().equals(idclientee)){
                sensores.remove(i);
                break;
            }
        }
    }

    public static void AddUser(String name,String ip){
        //Solo agrega a nuevos usuarios si estos tienen una IP diferente a los ya registrados
        List<Usuario> nusuarios = usuarios.stream().filter(u -> u.getIP().equals(ip)).collect(Collectors.toList());
        if(nusuarios.size() == 0){
            usuarios.add(new Usuario(name,ip));
        }
    }
    
    //Esta funcion se encarga de activar el agente inteligente que se encargara de administrar el sistema utilizando
    //fundamentos en inteligencia artificial.
    /*public static void InitializeAgents()
    {
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        Profile profile = new ProfileImpl();
        
        profile.setParameter(Profile.CONTAINER_NAME, "TestContainer");
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        ContainerController container = runtime.createAgentContainer(profile);
        
        try {
            AgentController ag = container.createNewAgent(
                    "a1",
                    "servidor.AdminAgent",
                    new Object[]{});//arguments
            ag.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }*/
    
    
}
