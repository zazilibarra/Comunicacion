/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

package comunicacion;

import java.io.*;
import java.net.*;
import java.util.logging.*;

public class ServidorHilo extends Thread {
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String idCliente;
    private String estado;
    
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
    
    /*Recibe mensaje del cliente*/
    @Override
    public void run() {
        String accion = "";
        
        try {
            accion = dis.readUTF();
            if(accion.equals("hola")){
                System.out.println("El Sensor con id "+this.idCliente+" saluda");
            }
        } 
        catch (IOException ex) {
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
