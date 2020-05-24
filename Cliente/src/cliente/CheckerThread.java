/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */
package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CheckerThread extends Thread {
    
    private DataOutputStream dos;
    private DataInputStream dis;
    private Socket socket;
    private boolean isOk;
    private int countTries;
    
    public CheckerThread(Socket s){
        try{
            socket = s;
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        }
        catch(Exception error){
            System.out.println("Error al inicializar CheckerThread\n" + error.getMessage());
        }
        isOk = false;
        countTries = 0;
    }
    
    @Override
    public void run() {
      while(!Thread.interrupted())
        {   
            try{
                //Se escribe en el servidor PING
                Thread.sleep(5000);
                Mensaje mensaje = Helper.Send("4A", "PING...", dos);
                byte[] data = mensaje.getDatos();
                String strData = new String(data,StandardCharsets.UTF_8);
                System.out.println(strData);  
                Mensaje mensaje_r = Helper.Receive(dis);
                if(mensaje != null){
                    byte[] data_r = mensaje_r.getDatos();
                    String strData_r = new String(data_r,StandardCharsets.UTF_8);
                    if(strData_r.equals("PONG...")){
                        System.out.println("Estatus del Socket " +socket.isClosed());
                        System.out.println(strData_r);  
                        isOk = true;
                    }else{
                      countTries+=1;
                      
                      if(countTries >=5){
                          //Desconectarse
                      }
                    }
                    
                }else{
                    countTries+=1;
                    
                    if(countTries >= 5){
                        //No hubo respuesta del servidor, por lo tanto 
                        //se considera que el cliente se debe cerrar
                        
                    }
                }
                
            }
            catch(Exception ex){
                isOk = false;
                System.out.println("Error en el PING PONG\n" + ex.getMessage());
            }
        }
    }
}
