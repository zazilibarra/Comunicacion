/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

package comunicacion;

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
            ss = new ServerSocket(10578);
            System.out.println("\t[OK]");
            
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
