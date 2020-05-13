/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

package comunicacion;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.*;

public class Cliente extends Thread {
    protected Socket sk;
    protected DataOutputStream dos;
    protected DataInputStream dis;
    private String id;
    
    public Cliente(String name) {
        id = name;
    }
    
    @Override
    public void run() {
        try {
            sk = new Socket("127.0.0.3", 10578);
            dos = new DataOutputStream(sk.getOutputStream());
            dis = new DataInputStream(sk.getInputStream());
            System.out.println(id + " envía saludo");
            //dos.writeUTF("hola");
            
            Mensaje newMsg = new Mensaje("1A", "Hola"); 
            byte[] paquete = newMsg.getPaquete();
            //dos.write(newMsg.Paquete, 0, newMsg.Paquete.length);
            
            Mensaje test = new Mensaje(paquete);
            test.print();
            
            String respuesta="";
        } 
        catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class Main {
    public static void main(String[] args) {
        Thread client = new Cliente("Cliente");
        client.start();
    }
}