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
    private String Password;
    
    //Esta clase se encarga unicamente de realizar el PING PONG con el servidor para verificar que la conexion
    //siga integra.
    public CheckerThread(Socket s,String password){
        try{
            socket = s;
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            Password = password;
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
                Helper.Send("4A", "PING...", dos,Password);
                Mensaje PONG = Helper.Receive(dis,"");
                if(PONG != null && (new String(PONG.getCabecera(),StandardCharsets.UTF_8).equals("4B"))){
                    byte[] data_r = PONG.getDatos();
                    String strData_r = new String(data_r,StandardCharsets.UTF_8);
                    if(strData_r.equals("PONG...")){
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
