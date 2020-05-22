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

public class ServidorHilo extends Thread{
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String idCliente;
    private String estado;
    private String password;
    
    private BufferedReader entrada;
    
    public ServidorHilo(Socket socket, int id) {
        this.socket = socket;
        this.idCliente = "Sensor" + id;
        this.estado = "ACTIVO";
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            //dos.writeUTF("Petición recibida y aceptada");
            
            dis = new DataInputStream(socket.getInputStream());
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
            Mensaje connect = Helper.Receive(dis);
            //El SERVIDOR ENVIA LA CONTRASEÑA PARA ENCRIPTAR LOS MENSAJES POSTERIORES, DEL CLIENTE
            password = Helper.getRandomAlphaNumString();
            Mensaje connback = Helper.Send("1B", password, dos);
            //EL SERVIDOR RECIBE RESPUESTA DEL CLIENTE, UN ACKNOWLEDGE
            Mensaje ackconn = Helper.Receive(dis);
            //EL SERVIDOR HA RECIBIDO RESPUESTA DEL CLIENTE, POR LO TANTO CONTINUA
            if(ackconn != null){
                String passwordReceived = new String(ackconn.getDatos(),StandardCharsets.UTF_8);
                if(passwordReceived.equals(password)) response = true;
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
            Mensaje subs = Helper.Receive(dis);
            
            if(subs != null){
                //El SERVIDOR ENVIA RESPUESTA
                Mensaje subsback = Helper.Send("2B", "CANAL?", dos);
                //ENVIA EL ACKNOWLEDGE PARA EL SERVIDOR
                Mensaje acksubs = Helper.Receive(dis);
                if(acksubs != null) response = true;
            }
             
        }
        catch(Exception error)
        {
        }
        
        return response;
    }
    
    public boolean offerAdmin(){
        boolean response = false;
        
        try
        {
            //EL SERVIDOR RECIBE UN MENSAJE OFFERADM
            Mensaje offeradm = Helper.Receive(dis);
            if(offeradm != null){
                
                //El SERVIDOR ENVIA LA RESPUESTA AL CLIENTE
                Mensaje accept_or_decline = Helper.Send("3B", "ACCEPT", dos);
                //ENVIA EL ACKNOWLEDGE PARA EL SERVIDOR
                String res = new String(accept_or_decline.getDatos(),StandardCharsets.UTF_8);
                Mensaje ackadm = Helper.Send("3D", res, dos);
                if(res.equals("ACCEPT")){
                    response = true;
                }  
            } 
            
            //EL CLIENTE HA RECIBIDO RESPUESTA DEL SERVIDOR, POR LO TANTO CONTINUA
            
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
            Mensaje ping = Helper.Receive(dis);
            //EL SERVIDOR HA RECIBIDO RESPUESTA DEL CLIENTE, POR LO TANTO CONTINUA
            if(ping != null){
                //EL SERVIDOR ENVIA UN MENSAJE PONG
                Mensaje pong = Helper.Send("4B", "", dos);
                response = true;
            } 
            
            
        }
        catch(Exception error)
        {
            
        }
        
        return response;
    }
    
    /*Recibe mensaje del cliente*/
    @Override
    public void run() {
        String accion = "";

        try {
//            String mensajeServidor;
//            while((mensajeServidor = entrada.readLine()) != null) //Mientras haya mensajes desde el cliente
//            {
//                //Se muestra por pantalla el mensaje recibido
//                System.out.println("Desde Servidor: " + mensajeServidor + "\n");
//            }

            while(true){
                Mensaje mensaje = Helper.Receive(dis);
                if(mensaje != null){
                    byte[] data = mensaje.getDatos();
                    String strData = new String(data,StandardCharsets.UTF_8);
                    System.out.println(strData);
                }
            }
            
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
