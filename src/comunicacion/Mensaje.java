/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

package comunicacion;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;

public class Mensaje {
    
    private byte[] Cabecera; //2 bytes
    private byte[] Longitud; // 8 bytes
    private byte[] Datos; // 0 - N bytes
    private byte[] Checksum; //Complemento a dos de Datos
    public byte[] Paquete; //Todos los campos concatenados
    
    //Constructor que empaqueta
    public Mensaje(String c, String datos){
        Cabecera = Helper.decodeHexString(c);
        System.out.println("Constructor Cabecera:"+Cabecera);
        Longitud = Helper.longToByteArray(datos.length());
        System.out.println("Constructor Longitud:"+Longitud);
        Datos = datos.getBytes();
        System.out.println("Constructor Datos:"+Datos);
        Checksum = getChecksum(datos);
        System.out.println("Constructor Checksum:"+Checksum);
        Paquete = empaqueta();
        System.out.println("Constructor Paquete:"+Paquete);
    }
    
    //Constructor que desempaqueta
    public Mensaje(byte[] paquete ){
        // slice from index 5 to index 9
        Cabecera = Arrays.copyOfRange(paquete, 0, 3);
        System.out.println(Cabecera);
        Longitud = Arrays.copyOfRange(paquete, 3, 12);
        System.out.println(Longitud);
        Datos = Arrays.copyOfRange(paquete, 12, (int)Helper.byteArrayToLong(Longitud) + 1);
        System.out.println(Datos);
        Checksum = Arrays.copyOfRange(paquete, (int)Helper.byteArrayToLong(Longitud) + 13, paquete.length + 1);
        System.out.println(Checksum);

        Paquete = paquete;
        System.out.println(Paquete);
    }
    
    //Obtiene el complemento a dos del arreglo de datos
    private byte[] getChecksum(String datos){
        byte[] checksum = {};
        
        return checksum;
    }
    
    private byte[] empaqueta(){
        byte[] paquete = new byte[Cabecera.length + Longitud.length + Datos.length + Checksum.length];
        
        System.arraycopy(Cabecera, 0, paquete, 0, Cabecera.length);
        System.arraycopy(Longitud, 0, paquete, Cabecera.length, Longitud.length);
        System.arraycopy(Datos, 0, paquete, Cabecera.length + Longitud.length, Datos.length);
        System.arraycopy(Checksum, 0, paquete, Cabecera.length + Longitud.length + Datos.length, Checksum.length);
        
        return paquete;
    }
    
    public void print(){
        System.out.println("Cabecera:" + Cabecera.toString());
        System.out.println("Cabecera:" + Longitud.toString());
        System.out.println("Cabecera:" + Datos.toString());
        System.out.println("Cabecera:" + Checksum.toString());

    }
    
}
