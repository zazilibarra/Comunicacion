/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacion;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author USUARIO DELL
 */
public class Encrypt {
    
    public byte[] Do(String sinCifrar) throws Exception{
        final byte[] bytes = sinCifrar.getBytes("UTF-8");
        final Cipher aes = obtieneCipher(true);
	final byte[] cifrado = aes.doFinal(bytes);
	return cifrado;
    }
    
    public String Undo(byte[] cifrado) throws Exception{
        final Cipher aes = obtieneCipher(false);
	final byte[] bytes = aes.doFinal(cifrado);
	final String sinCifrar = new String(bytes, "UTF-8");
	return sinCifrar;
    }
    
    public Cipher obtieneCipher(boolean isCifrar) throws Exception {
            final String frase = "FraseLargaConDiferentesLetrasNumerosYCaracteresEspeciales_áÁéÉíÍóÓúÚüÜñÑ1234567890!#%$&()=%_NO_USAR_ESTA_FRASE!_";
	final MessageDigest digest = MessageDigest.getInstance("SHA");
	digest.update(frase.getBytes("UTF-8"));
	final SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");

	final Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
	if (isCifrar) {
		aes.init(Cipher.ENCRYPT_MODE, key);
	} else {
		aes.init(Cipher.DECRYPT_MODE, key);
	}

	return aes;
    }
    
}
