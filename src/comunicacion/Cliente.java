/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

package comunicacion;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.*;

public class Cliente extends Thread {
    protected Socket socket;
    protected Socket socketDat;
    protected DataOutputStream dos;
    protected DataInputStream dis;
    private String id;
    private String Password;
    private DataInputStream disdat;
    
    public Cliente(String name) {
        id = name;
    }
    
    @Override
    public void run() {
        try {
            ServerSocket ss;
            InetAddress addr = InetAddress.getByName("127.0.0.5");
            ss = new ServerSocket(5000, 0, addr);
            //ss = new ServerSocket(5000);
            System.out.println("Servidor Data...\t[OK]");
            System.out.println("Esperando Sensor...");
            socketDat = ss.accept();
            System.out.println("Sensor conectado...");
            
            socket = new Socket("192.168.1.5", 10578);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            //disdat = new DataInputStream(socketDat.getInputStream());
            BufferedReader bfdat = new BufferedReader(new InputStreamReader (socketDat.getInputStream()));
            try
            {
                int i = 0;
                while(true)
                {
                    //Se recibe data del sensor
                    String dataSensor = bfdat.readLine();
                    System.out.println(dataSensor);
                    
                    //Se escribe en el servidor usando su flujo de datos
                    //dos.writeUTF("Este es el mensaje número " + (i+1) + " desde Cliente\n");
                    Mensaje mensaje = Send("1A",dataSensor);
                    byte[] data = mensaje.getDatos();
                    String strData = new String(data,StandardCharsets.UTF_8);
                    System.out.println(strData);
                    //Thread.sleep(3000);
                    i++;
                }
                
                //boolean isConexion = tryConnection();
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
