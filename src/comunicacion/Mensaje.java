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
        Longitud = Helper.longToByteArray(datos.length());
        Datos = datos.getBytes();
        Checksum = datos.getBytes();
    }
    
    //Constructor que desempaqueta
    public Mensaje(byte[] paqueteEncr ){
        
        try
        {
            String Password = "APEX_LEGENDS";
            byte[] paquete = Encrypt.Undo(Password,paqueteEncr),
            Cabecera = Arrays.copyOfRange(paquete, 0, 2);
            Longitud = Arrays.copyOfRange(paquete, 2, 10);
            Datos = Arrays.copyOfRange(paquete, 10, 10 + (int)Helper.byteArrayToLong(Longitud));
            Checksum = Arrays.copyOfRange(paquete, 10 + (int)Helper.byteArrayToLong(Longitud), paquete.length);
        }
        catch(Exception error){
            System.out.println("HA OCURRIDO UN ERROR AL DESEMPAQUETAR EL MENSAJE: " + error.getMessage());
        }
        
    }
    
    //Obtiene el complemento a dos del arreglo de datos
    private byte[] getChecksum(String datos){
        byte[] checksum = {};
        
        return checksum;
    }
    
    //Funcion que devuelve el arreglo de bytes del mensaje completo
    public byte[] getPaquete(){
        
        byte[] paquete = new byte[Cabecera.length + Longitud.length + Datos.length + Checksum.length];
        try
        {
            System.arraycopy(Cabecera, 0, paquete, 0, Cabecera.length);
            System.arraycopy(Longitud, 0, paquete, Cabecera.length, Longitud.length);
            System.arraycopy(Datos, 0, paquete, Cabecera.length + Longitud.length, Datos.length);
            System.arraycopy(Checksum, 0, paquete, Cabecera.length + Longitud.length + Datos.length, Checksum.length);

            String Password = "APEX_LEGENDS";
            byte[] paqueteEncryptado = Encrypt.Do(Password,paquete);
            return paqueteEncryptado;
        }
        catch(Exception error){
            System.out.println("HA OCURRIDO UN ERROR AL OBTENER EL PAQUETE: " + error.getMessage());
        }
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
