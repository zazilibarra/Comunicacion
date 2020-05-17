/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

package comunicacion;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.*;

public class ServidorHilo extends Thread {
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String idCliente;
    private String estado;
    private String Password;
    
    public ServidorHilo(Socket socket, int id) {
        this.socket = socket; //hola mundo
        this.idCliente = "Sensor" + id;
        this.estado = "ACTIVO";
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } 
        catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean tryConnection(){
        boolean response = false;
        
        try
        {
            //EL SERVIDOR RECIBE UN MENSAJE CONNECT
            Mensaje connect = Receive();
            //El SERVIDOR ENVIA LA CONTRASEÑA PARA ENCRIPTAR LOS MENSAJES POSTERIORES, DEL CLIENTE
            Password = Helper.getRandomAlphaNumString();
            Mensaje connback = Send("",Password);
            //EL SERVIDOR RECIBE RESPUESTA DEL CLIENTE, UN ACKNOWLEDGE
            Mensaje ackconn = Receive();
            //EL SERVIDOR HA RECIBIDO RESPUESTA DEL CLIENTE, POR LO TANTO CONTINUA
            if(ackconn != null){
                String passwordReceived = new String(ackconn.getDatos(),StandardCharsets.UTF_8);
                if(passwordReceived.equals(Password)) response = true;
            } 
        }
        catch(Exception error)
        {
        }
        return response;
    }
    
    public boolean subsConnection(){
        boolean response = false;
        
        try
        {
            //EL SERVIDOR RECIBE UN MENSAJE SUBS
            Mensaje subs = Receive();
            
            if(subs != null){
                //El SERVIDOR ENVIA RESPUESTA
                Mensaje subsback = Send("2B","CANAL?");
                //ENVIA EL ACKNOWLEDGE PARA EL SERVIDOR
                Mensaje acksubs = Receive();
                if(acksubs != null) response = true;
            }
             
        }
        catch(Exception error)
        {
        }
        return response;
    }
    
    public boolean testConnection(){
        boolean response = false;
        
        try
        {
            //EL SERVIDOR RECIBE UN MENSAJE PING
            Mensaje ping = Receive();
            //EL SERVIDOR HA RECIBIDO RESPUESTA DEL CLIENTE, POR LO TANTO CONTINUA
            if(ping != null){
                response = true;
            } 
            //EL SERVIDOR ENVIA UN MENSAJE PONG
            Mensaje pong = Send("4B","");
            
        }
        catch(Exception error)
        {
            
        }
        
        return response;
    }
    
    public Mensaje Send(String cabecera,String datos) throws Exception{
            //CREA UN MENSAJE CONNECT
            Mensaje mensaje = new Mensaje(cabecera, datos); 
            byte[] paquete = mensaje.getPaquete();
            //SE ENVIA EL TAMAÑO DEL PAQUETE
            dos.writeInt(paquete.length);
            //SE ENVIA EL PAQUETE
            dos.write(paquete);
            return mensaje;
    }
    
    public Mensaje Receive() throws Exception{
            Mensaje mensaje = null;
            //LEE EL TAMAÑO DEL PAQUETE
            int length = dis.readInt();
            if(length > 0){
                //SE LEEN LOS BYTES DEL PAQUETE
                byte[] paquete = new byte[length];
                dis.readFully(paquete, 0, length);
                mensaje = new Mensaje(paquete);
                
            }
            return mensaje;
    }
    /*Recibe mensaje del cliente*/
    @Override
    public void run() {
        String accion = "";
        
        try {
            Mensaje received = Receive();
            if(received != null) received.print();
            
        } 
        catch (Exception ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     public String getIP()
    {
      String ip;
      ip=new String(this.socket.getInetAddress()+"");
      return ip;
    }
    
    public void setEstado(String nEstado)
    {
      this.estado=nEstado;
    }
    
    public String getEstado()
    {
      return this.estado;
    }
    
    public void setidCliente(String nNombre)
    {
      this.idCliente=nNombre;
    }
    
    public String getidCliente()
    {
      return idCliente;
    }
    
    public void desconnectar() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
