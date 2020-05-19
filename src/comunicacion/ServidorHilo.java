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
    private String password;
    
    private BufferedReader entrada;
    
    public ServidorHilo(Socket socket, int id) {
        this.socket = socket;
        this.idCliente = "Sensor" + id;
        this.estado = "ACTIVO";
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            //dos.writeUTF("Petición recibida y aceptada");
            
            //dis = new DataInputStream(socket.getInputStream());
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            String mensajeServidor;
            while((mensajeServidor = entrada.readLine()) != null) //Mientras haya mensajes desde el cliente
            {
                //Se muestra por pantalla el mensaje recibido
                System.out.println("Desde Servidor: " + mensajeServidor + "\n");
            }
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
            password = Helper.getRandomAlphaNumString();
            Mensaje connback = Send("", password);
            //EL SERVIDOR RECIBE RESPUESTA DEL CLIENTE, UN ACKNOWLEDGE
            Mensaje ackconn = Receive();
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
    
    public boolean offerAdmin(){
        boolean response = false;
        
        try
        {
            //EL SERVIDOR RECIBE UN MENSAJE OFFERADM
            Mensaje offeradm = Receive();
            if(offeradm != null){
                
                //El SERVIDOR ENVIA LA RESPUESTA AL CLIENTE
                Mensaje accept_or_decline = Send("3B","ACCEPT");
                //ENVIA EL ACKNOWLEDGE PARA EL SERVIDOR
                String res = new String(accept_or_decline.getDatos(),StandardCharsets.UTF_8);
                Mensaje ackadm = Send("3D",res);
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
            //Mensaje received = Receive();
            //if(received != null) received.print();
            
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
