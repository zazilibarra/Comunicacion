/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */


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
            sk = new Socket("192.168.1.139", 10578);
            dos = new DataOutputStream(sk.getOutputStream());
            dis = new DataInputStream(sk.getInputStream());
            System.out.println(id + " envía saludo");
            dos.writeUTF("hola");
            String respuesta="";
        } 
        catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
