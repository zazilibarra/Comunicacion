/**
 * @author Alvarez Esaú
 * @author Ibarra Zazil
 * @author Torres Daniel
 */

package comunicacion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.lang.Math; 
import java.util.Random;

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
    
    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }
    
    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException("Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }
    
    public static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
              "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }
    
    public static int getComplement2(final int x){
        //C2 = 2˄n - N
        //N = 2˄n - C2
        
        //0 0 0 1 1 0 0 0
        //1 1 1 0 0 1 1 1
        //1 1 1 0 1 0 0 0
        //24
        //C2 24 = 2˄8 - 24 = 232;
        //24 = 256 - 232
        
        int x1 = ((int)Math.pow(2, 32)) - x;
        return x1;
        
        //Oso 3bytes
        //70 71 70 12bytes
        
    }
    
    public static String getRandomAlphaNumString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
          .limit(targetStringLength)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();

        return generatedString;
    }
    
    public static Mensaje Send(String cabecera, String datos, DataOutputStream dos) throws Exception{
            //CREA UN MENSAJE CONNECT
            Mensaje mensaje = new Mensaje(cabecera, datos); 
            byte[] paquete = mensaje.getPaquete();
            //SE ENVIA EL TAMAÑO DEL PAQUETE
            dos.writeInt(paquete.length);
            //SE ENVIA EL PAQUETE
            dos.write(paquete);
            return mensaje;
    }
    
    public static Mensaje Receive(DataInputStream dis) throws Exception{
            Mensaje mensaje = null;
            //LEE EL TAMAÑO DEL PAQUETE
            int length = dis.readInt();
            if(length > 0){
                //SE LEEN LOS BYTES DEL PAQUETE
                byte[] paquete = new byte[length];
                dis.readFully(paquete, 0, length);
                mensaje = new Mensaje(paquete);
                
            }
            return mensaje;
    }
}
