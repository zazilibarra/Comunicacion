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
            socket = new Socket("192.168.1.71", 10578);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());

            checker = new CheckerThread(dis,dos);
            data = new DataThread(dos);
        } 
        catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        try {
            checker.start();
            data.start();
        } 
        catch(Exception error)
        {
        }
    }
    
    public boolean tryConnection(){
        boolean response = false;
        
        try
        {
            //EL CLIENTE ENVIA UN MENSAJE CONNECT
            Mensaje connect = Helper.Send("1A", "HOLA", dos);
            //El CLIENTE RECIBE LA CONTRASEÑA PARA ENCRIPTAR LOS MENSAJES POSTERIORES, DEL SERVIDOR
            Mensaje connback = Helper.Receive(dis);
            //EL CLIENTE HA RECIBIDO RESPUESTA DEL SERVIDOR, POR LO TANTO CONTINUA
            if(connback != null){
                //OBTIENE LA CONTRASEÑA GENERADA POR EL SERVIDOR PARA ESTE CLIENTE
                Password = new String(connback.getDatos(),StandardCharsets.UTF_8);
                //ENVIA EL ACKNOWLEDGE PARA EL SERVIDOR
                Mensaje ackconn = Helper.Send("1C", Password, dos);
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
            Mensaje subs = Helper.Send("2A", "CANAL?", dos);
            //El CLIENTE RECIBE LA RESPUESTA DEL SERVIDOR
            Mensaje subsback = Helper.Receive(dis);
            //EL CLIENTE HA RECIBIDO RESPUESTA DEL SERVIDOR, POR LO TANTO CONTINUA
            if(subsback != null){
                //ENVIA EL ACKNOWLEDGE PARA EL SERVIDOR
                Mensaje acksubs = Helper.Send("2C","CANAL2 ", dos);
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
            Mensaje offeradm = Helper.Send("3A", "ip admin?", dos);
            //El CLIENTE RECIBE LA RESPUESTA DEL SERVIDOR
            Mensaje accept_or_decline = Helper.Receive(dis);
            //EL CLIENTE HA RECIBIDO RESPUESTA DEL SERVIDOR, POR LO TANTO CONTINUA
            if(accept_or_decline != null){
                //ENVIA EL ACKNOWLEDGE PARA EL SERVIDOR
                String res = new String(accept_or_decline.getDatos(),StandardCharsets.UTF_8);
                Mensaje ackadm = Helper.Send("3D", res, dos);
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
            Mensaje ping = Helper.Send("4A", "", dos);
            //EL CLIENTE RECIBE UN MENSAJE PONG
            Mensaje pong = Helper.Receive(dis);
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
    
    public void closeConnection(){
        try {
            if(socket != null)
                socket.close();
        } catch (IOException ex) {
            System.out.println("Error al cerrar el socket\n" + ex.getMessage());
        }
    }
}

class Main {
    public static void main(String[] args) throws IOException {
        Thread client = new Cliente("Cliente");
        client.start();
    }
}
