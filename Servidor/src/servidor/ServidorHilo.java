/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

package servidor;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.*;
import org.json.JSONObject;

//Por cada conexion al servidor se crea un hilo que administra la conexion con los clientes sensores, esta clase
//representa ese hilo y sus metodos para obtener la informacion a traves de sockets y enviarla a la clase principal
public class ServidorHilo extends Thread{
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String idCliente;
    private String nombre;
    private String value;
    private String estado;
    private String Password;
    private String Topico;
    private int countTries;
    private Servidor servidor;
    
    
    private BufferedReader entrada;
    
    public ServidorHilo(Socket socket, int id) {
        this.socket = socket;
        this.idCliente = "Sensor" + id;
        this.nombre = "SENSOR";
        this.estado = "ACTIVO";
        countTries = 0;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } 
        catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Esta funcion detecta que se le envia un mensaje CONNECT al servidor y establece la conexion con el cliente sensor.
    public boolean EstableceComunicacion(){
        boolean response = false;
        
        try
        {
            //EL SERVIDOR RECIBE UN MENSAJE CONNECT
            Mensaje CONNECT = Helper.Receive(dis,"");
            
            if(CONNECT != null && (new String(CONNECT.getCabecera(),StandardCharsets.UTF_8).equals("1A"))){
                //El SERVIDOR ENVIA LA CONTRASEÑA PARA ENCRIPTAR LOS MENSAJES POSTERIORES, DEL CLIENTE
                Password = Helper.getRandomAlphaNumString();
                Helper.Send("1B", Password, dos,"");
                //EL SERVIDOR RECIBE RESPUESTA DEL CLIENTE, UN ACKNOWLEDGE
                Mensaje ACKCONN = Helper.Receive(dis,"");
                //EL SERVIDOR HA RECIBIDO RESPUESTA DEL CLIENTE, POR LO TANTO CONTINUA
                if(ACKCONN != null && (new String(ACKCONN.getCabecera(),StandardCharsets.UTF_8).equals("1C"))){
                    String passwordReceived = new String(ACKCONN.getDatos(),StandardCharsets.UTF_8);
                    if(passwordReceived.equals(Password)){
                        response = true;
                        Password = passwordReceived;
                        System.out.println("Comunicacion establecida con SENSOR :" + idCliente);
                    }
                } 
            }
        }
        catch(Exception error)
        {
        }
        return response;
    }
    
    //Obtiene el mensaje de suscripcion del cliente para saber el topico en el que se desea inscribir al sensor.
    public boolean SuscribeTopico(){
        boolean response = false;
        
        try
        {
            //EL SERVIDOR RECIBE UN MENSAJE SUBS
            Mensaje SUBS = Helper.Receive(dis,Password);
            
            if(SUBS != null && (new String(SUBS.getCabecera(),StandardCharsets.UTF_8).equals("2A"))){
                //El SERVIDOR ENVIA RESPUESTA
                Helper.Send("2B", "", dos,Password);
                //ENVIA EL ACKNOWLEDGE PARA EL SERVIDOR
                Mensaje ACKSUBS = Helper.Receive(dis,Password);
                if(ACKSUBS != null && (new String(ACKSUBS.getCabecera(),StandardCharsets.UTF_8).equals("2C"))){
                    response = true;
                    Topico = new String(ACKSUBS.getDatos(),StandardCharsets.UTF_8);
                    System.out.println("Suscripcion establecida con SENSOR :" + idCliente + " con Topico: " + Topico);
                }
            }
             
        }
        catch(Exception error)
        {
        }
        
        return response;
    }
   
    
    /*Recibe mensaje del cliente*/
    //El hilo se encarga de tener una comunicacion constante con el sensor, obteniendo la informacion de este y mandando
    //un mensaje PING para asegurar que la conexion siga integra.
    @Override
    public void run() {
        String accion = "";

        try {
           //El Cliente Sensor establece comunicación con el servidor mediante mensajes de saludo
            boolean esComunicacionEstablecida = EstableceComunicacion();
            
            if(esComunicacionEstablecida){
                //El Cliente Sensor se suscribe a un Topico 
                boolean esSuscritoATopico = SuscribeTopico();
                
                if(esSuscritoATopico){
                    
                    while(!Thread.interrupted()){
                        //Obtiene los mensajes que es la informacion recopilada por el sensor
                        Mensaje MESSAGE = Helper.Receive(dis,Password);
                        if(MESSAGE != null && (new String(MESSAGE.getCabecera(),StandardCharsets.UTF_8).equals("2E"))){
                            byte[] data = MESSAGE.getDatos();
                            String strData = new String(data,StandardCharsets.UTF_8);
                            value = strData;
                            System.out.println("Sensor: " + idCliente + "\tValor: " + value);
                        }
                        else if(MESSAGE != null && (new String(MESSAGE.getCabecera(),StandardCharsets.UTF_8).equals("4A"))){
                            Helper.Send("4B", "PONG...", dos,"");
                        }
                    }
                }
            }
        } 
        catch (Exception ex) {
            System.out.println("Error al recibir o enviar paquete\n" + ex.getMessage());
            desconectar();
        }
    }
    
     public String getIP()
    {
      String ip;
      ip=new String(this.socket.getInetAddress()+"");
      return ip;
    }
     
     public String getTopico(){
         return Topico;
     }
    
    public void setEstado(String nEstado){
      this.estado=nEstado;
    }
    
    public String getValue(){
        return value;
    }
    public String getNombre(){
        return nombre;
    }
    
    public String getEstado(){
      return this.estado;
    }
    
    public void setIdCliente(String nNombre){
      this.idCliente=nNombre;
    }
    
    public String getIdCliente(){
      return idCliente;
    }
    
    //Esta funcion se deshace de la conexion con el sensor cuando este mande el mensaje de DISCONNECT.
    public void desconectar() {
        try {
            if(socket != null){
                socket.close();
                Servidor.RemoveSensor(idCliente);
            }
                
                
        } catch (IOException ex) {
            System.out.println("Error al desconectar socket\n" + ex.getMessage());
        }
    }
    
    public void doBeforeKill(){
        if(Thread.currentThread().isInterrupted())
            Servidor.RemoveSensor(idCliente);
    }
}
