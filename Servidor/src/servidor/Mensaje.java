/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

package servidor;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;

//Esta clase representa la estructura del paquete que se enviara a traves de los sockets la cual contiene la informacion
//del sistema. Aporta las funciones necesarias para empaquetar y desempaquetar los datos.
public class Mensaje {
    private byte[] Cabecera = new byte[2]; //2 bytes
    private byte[] Longitud = new byte[8]; // 8 bytes
    private byte[] Datos; // 0 - N bytes
    private byte[] Checksum; //Complemento a dos de Datos
    private String Password;
    private static String DEFAULT_PASSWORD = "DEFAULT";
    
    //Constructor que empaqueta
    public Mensaje(String c, String datos,String password){
        Cabecera = c.getBytes();
        Longitud = Helper.longToByteArray(datos.length());
        Datos = datos.getBytes();
        Checksum = getChecksum(datos);
        if(password.equals(""))
            Password = DEFAULT_PASSWORD;
        else
            Password = password;
    }
    
    //Constructor que desempaqueta
    //Este obtiene el mensaje empaquetado y se encargaba de obtener cada uno de los datos de la estructura de los datos.
    public Mensaje(byte[] paqueteEncr,String password){
        try
        {
            if(password.equals(""))
                Password = DEFAULT_PASSWORD;
            else
                Password = password;
            
            byte[] paquete = Encrypt.Undo(Password,paqueteEncr);
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
    public boolean Checksum(){
        boolean response = false;
        
        String sDatos = new String(Datos, StandardCharsets.UTF_8);
        String sChecksum = new String(Checksum, StandardCharsets.UTF_8);
        byte[] bChecksum = getChecksum(sChecksum);
        String ssChecksum = new String(bChecksum, StandardCharsets.UTF_8); 
        
        if(sDatos.equals(ssChecksum)){
            response = true;
        }
        
        
        return response;
    }
    
    public byte[] getDatos(){ return Datos; }
    
    public byte[] getCabecera(){ return Cabecera; }
    
    //Funcion que devuelve el arreglo de bytes del mensaje completo
    //En otras palabras, empaqueta el mensaje para hacer apto su envio.
    public byte[] getPaquete(){
        
        byte[] paquete = new byte[Cabecera.length + Longitud.length + Datos.length + Checksum.length];
        try
        {
            System.arraycopy(Cabecera, 0, paquete, 0, Cabecera.length);
            System.arraycopy(Longitud, 0, paquete, Cabecera.length, Longitud.length);
            System.arraycopy(Datos, 0, paquete, Cabecera.length + Longitud.length, Datos.length);
            System.arraycopy(Checksum, 0, paquete, Cabecera.length + Longitud.length + Datos.length, Checksum.length);

            byte[] paqueteEncryptado = Encrypt.Do(Password,paquete);
            return paqueteEncryptado;
        }
        catch(Exception error){
            System.out.println("HA OCURRIDO UN ERROR AL OBTENER EL PAQUETE: " + error.getMessage());
        }
        return paquete;
    }
    public static byte[] getChecksum(String datos){
        String checkDatos = "";
        for(int i = 0; i < datos.length(); i++){
            char caracter = datos.charAt(i);
            int caracterAscii = (int)caracter;
            int complemento = Helper.getComplement2(caracterAscii);
            char caracterComplemento = (char)complemento;
            checkDatos += caracterComplemento;
        }
        byte[] check = checkDatos.getBytes();
        return check;
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
