/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */
package cliente;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DataThread extends Thread {
    protected DataOutputStream dos;
    private Socket socket;
    
    public DataThread(Socket s){
        try{
            socket = s;
            dos = new DataOutputStream(socket.getOutputStream());
        }
        catch(Exception ex){
            System.out.println("Error al obtener outputstream\n"+ ex.getMessage());
        }
    }
    
    @Override
    public void run() {
        try
            {
                ServerSocket ss;
                InetAddress addr = InetAddress.getByName("127.0.0.5");
                ss = new ServerSocket(5000, 0, addr);
                System.out.println("Servidor Data...\t[OK]");
                System.out.println("Esperando Sensor...");
                Socket socketDat = ss.accept();
                System.out.println("Sensor conectado...");
                BufferedReader bfdat = new BufferedReader(new InputStreamReader (socketDat.getInputStream()));
                int i = 0;
                while(!Thread.interrupted())
                {
                    //Se recibe data del sensor
                    String dataSensor = bfdat.readLine();
                    
                    //Se escribe en el servidor usando su flujo de datos
                    //dos.writeUTF("Este es el mensaje número " + (i+1) + " desde Cliente\n");
                    Mensaje mensaje = Helper.Send("1A", dataSensor, dos);
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
}
