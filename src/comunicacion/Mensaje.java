/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacion;

import java.util.Arrays;

/**
 *
 * @author USUARIO DELL
 */
public class Mensaje {
    
    private byte[] Cabecera; //4 bytes
    private byte[] Longitud; // 8 bytes
    private byte[] Datos; // 0 - N bytes
    private byte[] Checksum; //Complemento a dos de Datos
    public byte[] Paquete; //Todos los campos concatenados
    
    //Constructor que empaqueta
    public Mensaje(byte[] cabecera, byte[] datos){
        Cabecera = cabecera;
        Longitud = Helper.longToBytes(datos.length);
        Datos = datos;
        Checksum = getChecksum(datos);
        Paquete = empaqueta();
    }
    
    //Constructor que desempaqueta
    public Mensaje(byte[] paquete ){
        // slice from index 5 to index 9
        Cabecera = Arrays.copyOfRange(paquete, 0, 5);
        Longitud = Arrays.copyOfRange(paquete, 5, 14);
        Datos = Arrays.copyOfRange(paquete, 14, (int)Helper.bytesToLong(Longitud) + 1);
        Checksum = Arrays.copyOfRange(paquete, (int)Helper.bytesToLong(Longitud) + 15, paquete.length + 1);
        Paquete = paquete;
    }
    
    //Obtiene el complemento a dos del arreglo de datos
    private byte[] getChecksum(byte[] datos){
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
    
}
