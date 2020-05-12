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
    public byte[] Paquete; //Todos los campos concatenados
    
    //Constructor que empaqueta
    public Mensaje(String c, String datos){
        //Cabecera = Helper.decodeHexString(c);
        Cabecera = c.getBytes();
        String s = new String(Cabecera, StandardCharsets.UTF_8);
        System.out.println("Constructor Cabecera:" + s);
        Longitud = String.valueOf(datos.length()).getBytes();
        System.out.println("Constructor Cabecera:" + String.valueOf(datos.length()));
        Datos = datos.getBytes();
        System.out.println("Constructor Datos:"+Datos);
        Checksum = getChecksum(datos);
        System.out.println("Constructor Checksum:"+Checksum);
        Paquete = empaqueta();
        String p = new String(Paquete, StandardCharsets.UTF_8);
        System.out.println("Constructor Paquete:" + p);
    }
    
    //Constructor que desempaqueta
    public Mensaje(byte[] paquete ){
        Cabecera = Arrays.copyOfRange(paquete, 0, 3);
        System.out.println(Cabecera);
        Longitud = Arrays.copyOfRange(paquete, 3, 12);
        System.out.println(Longitud);
        Datos = Arrays.copyOfRange(paquete, 12, 4 + 1);
        System.out.println(Datos);
        Checksum = Arrays.copyOfRange(paquete, 4 + 13, paquete.length + 1);
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
        String s = new String(Cabecera, StandardCharsets.UTF_8);
        System.out.println("Cabecera:" + s);
        String l = new String(Cabecera, StandardCharsets.UTF_8);
        System.out.println("Cabecera:" + l);
        String d = new String(Cabecera, StandardCharsets.UTF_8);
        System.out.println("Datos:"+d);
    }
    
}
