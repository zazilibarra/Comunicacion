/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */
package comunicacion;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import jade.core.*;
import jade.wrapper.*;

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
        System.out.print("Inicializando servidor... ");
        
        try {
            //Se crea una nueva instancia de ServerSocket para recibir comunicaciones
            InetAddress addr = InetAddress.getByName("127.0.0.4");
            ss = new ServerSocket(10578, 0, addr);
            System.out.println("\t[OK]");
            
            int idUsuario = 0;
            int idSensor = 0;
            
            /*Siempre espera nuevas conexiones, cuando identifica una nueva,
            crea una instancia de Socket y lo agrega a la lista de clientes*/
            while (true) {
                Socket socket;
                System.out.println("Esperando...");
                socket = ss.accept();
                System.out.println("Nueva conexión entrante: " + socket);
                
                ServidorHilo nCliente = new ServidorHilo(socket, idSensor);
                sensores.add(nCliente);
                nCliente.start();
                idSensor++;
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
