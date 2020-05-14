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
    
    public Cliente(String name) {
        id = name;
    }
    
    @Override
    public void run() {
        try {
            sk = new Socket("127.0.0.4", 10578);
            dos = new DataOutputStream(sk.getOutputStream());
            dis = new DataInputStream(sk.getInputStream());
            System.out.println(id + " envía saludo");
            //dos.writeUTF("hola");
            
            Mensaje newMsg = new Mensaje("1A", "ESTE ES UN MENSAJE SUMAMENTE LARGO PARA CUESTIONES DE PRUEBA Y ESAS COSAS, TAMBIEN INCLUYE ANAGRAMAS Y COSAS RARAS DE CHINOS"); 
            byte[] paquete = newMsg.getPaquete();
            boolean isIgual = newMsg.Checksum();
            System.out.println("Es igual = " + isIgual);
            //dos.write(newMsg.Paquete, 0, newMsg.Paquete.length);
            
            Mensaje test = new Mensaje(paquete);
            //test.print();
            //String ms = "Hola mundo cadena larga hola hola";
            //byte[] bComplemento = test.getChecksum(ms);
            
            
            //String sComplemento = new String(bComplemento, StandardCharsets.UTF_8);
            //System.out.println(ms);
            //System.out.println(sComplemento);
            //System.out.println(ms.length());
            //System.out.println(sComplemento.length());
            
            
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