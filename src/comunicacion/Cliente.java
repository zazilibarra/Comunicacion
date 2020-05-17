/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

package comunicacion;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.*;

public class Cliente extends Thread {
    protected Socket sk;
    protected DataOutputStream dos;
    protected DataInputStream dis;
    private String id;
    private String Password;
    
    public Cliente(String name) {
        id = name;
    }
    
    @Override
    public void run() {
        try {
            sk = new Socket("127.0.0.4", 10578);
            dos = new DataOutputStream(sk.getOutputStream());
            dis = new DataInputStream(sk.getInputStream());
            
            try
            {
                boolean isConexion = tryConnection();
                 
            }
            catch(Exception error)
            {
                
            }
        } 
        catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public boolean tryConnection(){
        boolean response = false;
        
        try
        {
            //EL CLIENTE ENVIA UN MENSAJE CONNECT
            Mensaje connect = Send("1A","HOLA");
            //El CLIENTE RECIBE LA CONTRASEÑA PARA ENCRIPTAR LOS MENSAJES POSTERIORES, DEL SERVIDOR
            Mensaje connback = Receive();
            //EL CLIENTE HA RECIBIDO RESPUESTA DEL SERVIDOR, POR LO TANTO CONTINUA
            if(connback != null){
                //OBTIENE LA CONTRASEÑA GENERADA POR EL SERVIDOR PARA ESTE CLIENTE
                Password = new String(connback.getDatos(),StandardCharsets.UTF_8);
                //ENVIA EL ACKNOWLEDGE PARA EL SERVIDOR
                Mensaje ackconn = Send("1C",Password);
                response = true;
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
            //EL CLIENTE ENVIA UN MENSAJE SUBS
            Mensaje subs = Send("2A","CANAL?");
            //El CLIENTE RECIBE LA RESPUESTA DEL SERVIDOR
            Mensaje subsback = Receive();
            //EL CLIENTE HA RECIBIDO RESPUESTA DEL SERVIDOR, POR LO TANTO CONTINUA
            if(subsback != null){
                //ENVIA EL ACKNOWLEDGE PARA EL SERVIDOR
                Mensaje acksubs = Send("2C","CANAL2 ");
                response = true;
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
            //EL CLIENTE ENVIA UN MENSAJE OFFERADM
            Mensaje offeradm = Send("3A","ip admin?");
            //El CLIENTE RECIBE LA RESPUESTA DEL SERVIDOR
            Mensaje accept_or_decline = Receive();
            //EL CLIENTE HA RECIBIDO RESPUESTA DEL SERVIDOR, POR LO TANTO CONTINUA
            if(accept_or_decline != null){
                //ENVIA EL ACKNOWLEDGE PARA EL SERVIDOR
                String res = new String(accept_or_decline.getDatos(),StandardCharsets.UTF_8);
                Mensaje ackadm = Send("3D",res);
                if(res.equals("ACCEPT")){
                    response = true;
                }
                
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
            //EL CLIENTE ENVIA UN MENSAJE PING
            Mensaje ping = Send("4A","");
            //EL CLIENTE RECIBE UN MENSAJE PONG
            Mensaje pong = Receive();
            //EL CLIENTE HA RECIBIDO RESPUESTA DEL SERVIDOR, POR LO TANTO CONTINUA
            if(pong != null){
                response = true;
            } 
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
}

class Main {
    public static void main(String[] args) {
        Thread client = new Cliente("Cliente");
        client.start();
    }
}