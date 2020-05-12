/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacion;

import java.nio.ByteBuffer;
import java.lang.Math; 

/**
 *
 * @author USUARIO DELL
 */
public class Helper {
    
    public static byte[] longToByteArray(final long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    
    public static long byteArrayToLong(final byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip 
        return buffer.getLong();
    }
    
    public static byte[] intToByteArray( final int i ) {
        ByteBuffer bb = ByteBuffer.allocate(4); 
        bb.putInt(i); 
        return bb.array();
    }
    
    public static byte[] getComplement2(final byte[] datos){
        
        long x = byteArrayToLong(datos); //Numero del arreglo de bytes
        int n = (datos.length); //Cantidad de bits que contiene ese numero
        //0 0 0 1 1 0 0 0
        //1 1 1 0 0 1 1 1
        //1 1 1 0 1 0 0 0
        //24
        //C2 24 = 2˄8 - 24 = 232;
        
        
        //C2 de N = 2˄n - N
        long x1 = ((long)Math.pow(2, n)) - x;
        
        return longToByteArray(x1);
        
    }
    
    
}
