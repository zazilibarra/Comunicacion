/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

package cliente;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.*;

//El cliente se crea en cada una de las tarjetas controladoras asociadas a los sensores, el cual procesa 
//la informacion y empaqueta para posteriormente enviarla al servidor.
public class Cliente extends Thread {
    protected Socket socket; //Socket para comunicacion con Servidor
    protected DataOutputStream dos;
    protected DataInputStream dis;
    private String id;
    private String Password;
    private CheckerThread checker; //Hilo para validar conexion
    private DataThread data; //Hilo para enviar datos al Servidor
    
    public Cliente(String name) throws IOException {
        try {
            id = name;
            //El Cliente Sensor se conecta con el Servidor
            socket = new Socket("192.168.1.6", 10578);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } 
        catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(Exception ex){
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    //Este hilo se encarga de obtener la informacion del cliente y enviarla al servidor para establecer la conexion
    @Override
    public void run(){
        try {
            //El Cliente Sensor establece comunicación con el servidor mediante mensajes de saludo y
            //recibe la contraseña para la encriptacion de mensajes
            boolean esComunicacionEstablecida = EstableceComunicacion();
            //Si la comunicación fue exitosa y el Cliente Sensor recibió la contraseña, entonces
            //se procede a iniciar el flujo de datos
            if(esComunicacionEstablecida){
                //El Cliente Sensor se suscribe a un Topico 
                boolean esSuscritoATopico = SuscribeTopico("RECAMARA");
                //Si la suscripción fue exitosa entonces se procede a enviar los datos recibidos por el sensor
                if(esSuscritoATopico){
                    //Se crean las instancias de dos hilos:
                    //El hilo Checker envía mensajes tipo Ping para determinar si aun existe conexión o no con el servidor
                    //El hilo Data envía la información obtenida por el sensor
                    checker = new CheckerThread(socket,Password);
                    data = new DataThread(socket,Password);
                    checker.start();
                    data.start();
                }
            }
            
        } 
        catch(Exception e){
            System.out.println("Error en cliente\n" + e.getMessage());
            terminate();
        }
    }
    
    
    public void terminate(){
        CerrarConexion();
        checker.interrupt();
        data.interrupt();
        interrupt();
    }
    
    //Esta funcion se encarga de estructurar el mensaje para lograr la conexion del servidor y crear su respectivo hilo.
    public boolean EstableceComunicacion(){
        boolean response = false;
        
        try
        {
            //El Cliente Sensor envía mensaje de Conexión y espera respuesta
            Helper.Send("1A", "CONEXION", dos,"");
            //El Cliente Sensor recibe la respuesta del Servidor, indicando su exitosa conexión
            Mensaje CONNBACK = Helper.Receive(dis,"");
            if(CONNBACK != null && (new String(CONNBACK.getCabecera(),StandardCharsets.UTF_8).equals("1B"))){
                byte[] data = CONNBACK.getDatos();
                //El cuerpo del mensaje CONNBACK contiene la contraseña que sera utilizada para encriptar los mensajes
                String strData = new String(data,StandardCharsets.UTF_8);
                Password = strData;
                Helper.Send("1C", Password, dos, "");
                response = true;
            }
        }
        catch(Exception error)
        {
            System.out.println("Ha ocurrido un error al establecer comunicación con el Servidor.\n" + error.getMessage());
        }
        
        return response;
    }

    //Funcion que se encarga de enviar al servidor un mensaje de suscripcion para que el sensor sea considerado en un
    //topico.
    public boolean SuscribeTopico(String topico){
        boolean response = false;
        
        try
        {
            //EL CLIENTE ENVIA UN MENSAJE SUBS
            Helper.Send("2A", topico, dos,Password);
            
            //El CLIENTE RECIBE LA RESPUESTA DEL SERVIDOR
            Mensaje SUBSBACK = Helper.Receive(dis,Password);
            //EL CLIENTE HA RECIBIDO RESPUESTA DEL SERVIDOR, POR LO TANTO CONTINUA
            if(SUBSBACK != null && (new String(SUBSBACK.getCabecera(),StandardCharsets.UTF_8).equals("2B"))){
                //ENVIA EL ACKNOWLEDGE PARA EL SERVIDOR
                Mensaje ACKSUBS = Helper.Send("2C",topico, dos,Password);
                response = true;
            } 
        }
        catch(Exception error)
        {
            System.out.println("Ha ocurrido un error al establecer topico con el Servidor.\n" + error.getMessage());
            terminate();
        }
        
        return response;
    }
  
    //Esta funcion apaga el cliente, mandando un mensaje de DISCONNECT al servidor.
    public void CerrarConexion(){
        try {
            if(socket != null)
                socket.close();
        } catch (IOException ex) {
            System.out.println("Error al cerrar el socket\n" + ex.getMessage());
        }
    }
}

class Main  {
    public static void main(String[] args) throws IOException, InterruptedException {
        try{
            Thread client = new Cliente("Cliente Sensor");
            client.start();
        }
        catch(Exception err){
            
        }
    }
}
