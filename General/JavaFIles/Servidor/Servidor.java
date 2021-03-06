/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class Servidor {
    //Lista para guardar los clientes conectados al servidor
    static List<ServidorHilo> clientes;
    
    public static void main(String[] args) {
        //Se inicializa lista
        clientes = new ArrayList<ServidorHilo>();
        
        ServerSocket ss;
        System.out.print("Inicializando servidor... ");
        
        try {
            //Se crea una nueva instancia de ServerSocket para recibir comunicaciones
            //en el puerto 10578
	    //Con la direccionn IP 192.168.1.139

            int puerto = 10578;
           //InetAddress addr = InetAddress.getByName("192.168.1.139");

            ss = new ServerSocket(puerto);
            //ss = new ServerSocket(puerto,50,addr);

            System.out.println("\t[OK]");
	    System.out.println("\nServidor en: " + InetAddress.getLocalHost());            
            System.out.println("\nEn el puerto: " + puerto);

            int idCliente = 0;
            /*Siempre espera nuevas conexiones, cuando identifica una nueva,
            crea una instancia de Socket y lo agrega a la lista de clientes*/
            while (true) {
                Socket socket;
                socket = ss.accept();
                System.out.println("Nueva conexión entrante: "+socket);
                ServidorHilo nCliente = new ServidorHilo(socket, idCliente);
                clientes.add(nCliente);
                nCliente.start();
                idCliente++;
            }
        } 
        catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
