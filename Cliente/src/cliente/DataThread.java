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
    
    public DataThread(DataOutputStream dosCliente){
        dos = dosCliente;
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
                while(true)
                {
                    //Se recibe data del sensor
                    String dataSensor = bfdat.readLine();
                    System.out.println(dataSensor);
                    
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
