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

public class Servidor {
    //Listas para guardar los clientes conectados al servidor
    static List<ServidorHilo> usuarios;  
    static List<ServidorHilo> sensores;  
    static AgentController AdminAgent;
    
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
            InetAddress addr = InetAddress.getByName("192.168.1.71");
            ss = new ServerSocket(10578, 0, addr);
            System.out.print("Servidor SENSORES en el puerto " + 10578);
            System.out.println("\t[OK]");
            
            int idUsuario = 0;
            int idSensor = 0;
            
            /*Siempre espera nuevas conexiones, cuando identifica una nueva,
            crea una instancia de Socket y lo agrega a la lista de clientes*/
            System.out.println("Esperando...");
            while (true) {
                Socket socketSensor;
                socketSensor = ss.accept();
                int PuertoLocal = socketSensor.getLocalPort();
                if(PuertoLocal!= 8080){
                    System.out.println("Nueva conexión entrante (SENSOR): " + socketSensor);  
                    ServidorHilo nCliente = new ServidorHilo(socketSensor, idSensor);
                    sensores.add(nCliente);
                    nCliente.start();
                    idSensor++;
                }else{
                    System.out.println("Nueva conexión entrante (CLIENTE): " + socketSensor);
                }
            }
        } 
        catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
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
