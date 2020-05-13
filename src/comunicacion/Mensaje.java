/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

package comunicacion;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;

public class Mensaje {
    
    private byte[] Cabecera = new byte[2]; //2 bytes
    private byte[] Longitud = new byte[8]; // 8 bytes
    private byte[] Datos; // 0 - N bytes
    private byte[] Checksum; //Complemento a dos de Datos
    
    //Constructor que empaqueta
    public Mensaje(String c, String datos){
        Cabecera = c.getBytes();
        String s = new String(Cabecera, StandardCharsets.UTF_8);
        System.out.println("Constructor Cabecera:" + s);
        
        Longitud = Helper.longToByteArray(datos.length());
        System.out.println("Constructor Longitud:" + Helper.byteArrayToLong(Longitud));
        
        Datos = datos.getBytes();
        String d = new String(Datos, StandardCharsets.UTF_8);
        System.out.println("Constructor Datos:" + d);
        
        Checksum = datos.getBytes();
        String ch = new String(Checksum, StandardCharsets.UTF_8);
        System.out.println("Constructor Checksum:" + ch);
    }
    
    //Constructor que desempaqueta
    public Mensaje(byte[] paquete ){
        String p = new String(paquete, StandardCharsets.UTF_8);
        System.out.println("Constructor 2 Paquete:" + p + "Long Paquete: " +paquete.length);
        
        Cabecera = Arrays.copyOfRange(paquete, 0, 2);
        String s = new String(Cabecera, StandardCharsets.UTF_8);
        System.out.println("Constructor Cabecera:" + s);
                
        Longitud = Arrays.copyOfRange(paquete, 2, 10);
        System.out.println("Constructor Longitud:" + Helper.byteArrayToLong(Longitud));
        
        Datos = Arrays.copyOfRange(paquete, 10, 10 + (int)Helper.byteArrayToLong(Longitud));
        String d = new String(Datos, StandardCharsets.UTF_8);
        System.out.println("Constructor Datos:" + d);
        
        Checksum = Arrays.copyOfRange(paquete, 10 + (int)Helper.byteArrayToLong(Longitud), paquete.length);
        String ch = new String(Checksum, StandardCharsets.UTF_8);
        System.out.println("Constructor Checksum:" + ch);
    }
    
    //Obtiene el complemento a dos del arreglo de datos
    private byte[] getChecksum(String datos){
        byte[] checksum = {};
        
        
        
        return checksum;
    }
    
    public byte[] getPaquete(){
        System.out.println(Cabecera.length);
        System.out.println(Longitud.length);
        System.out.println(Datos.length);
        System.out.println(Checksum.length);

        byte[] paquete = new byte[Cabecera.length + Longitud.length + Datos.length + Checksum.length];
        
        System.arraycopy(Cabecera, 0, paquete, 0, Cabecera.length);
        System.arraycopy(Longitud, 0, paquete, Cabecera.length, Longitud.length);
        System.arraycopy(Datos, 0, paquete, Cabecera.length + Longitud.length, Datos.length);
        System.arraycopy(Checksum, 0, paquete, Cabecera.length + Longitud.length + Datos.length, Checksum.length);
        
        String p = new String(paquete, StandardCharsets.UTF_8);
        System.out.println("Paquete:" + p + "Long: " + p.length());
        
        return paquete;
    }
    
    public void print(){
        String s = new String(Cabecera, StandardCharsets.UTF_8);
        System.out.println("Cabecera:" + s);
        
        System.out.println("Longitud:" + Helper.byteArrayToLong(Longitud));
        
        String d = new String(Datos, StandardCharsets.UTF_8);
        System.out.println("Datos:" + d);
        
        String ch = new String(Checksum, StandardCharsets.UTF_8);
        System.out.println("Checksum:" + ch);
    }
    
}
