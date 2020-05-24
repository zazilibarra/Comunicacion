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
    private boolean isOk;
    private int countTries;
    
    public CheckerThread(DataInputStream in,DataOutputStream out){
        dos = out;
        dis = in;
        isOk = false;
        countTries = 0;
    }
    
    @Override
    public void run() {
      while(true)
        {   
            try{
                //Se escribe en el servidor PING
                Mensaje mensaje = Helper.Send("4A", "PING...", dos);
                byte[] data = mensaje.getDatos();
                String strData = new String(data,StandardCharsets.UTF_8);
                System.out.println(strData);  
                
                Mensaje mensaje_r = Helper.Receive(dis);
                if(mensaje != null){
                    byte[] data_r = mensaje_r.getDatos();
                    String strData_r = new String(data_r,StandardCharsets.UTF_8);
                    System.out.println(strData_r);  
                    isOk = true;
                }else{
                    Thread.sleep(3000);
                    countTries+=1;
                    
                    if(countTries >= 20){
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
