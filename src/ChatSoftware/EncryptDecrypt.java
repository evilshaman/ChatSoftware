package ChatSoftware;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Ekansh Gupta
 */
public class EncryptDecrypt {
    public static String ALG = "AES";
    public static byte[] keyV =  new byte[] { 'n', 's', 'a', 'f', 'l', 'o', 'h', 'k', 'x', 'c', 'b','b', 'b', 'y', 'e', 'q' };

        public static String decrypt(String encryptedData) throws Exception {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALG);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
            byte[] decValue = c.doFinal(decordedValue);
            String decrypted = new String(decValue);
            return decrypted;
        }
        
        public static String encrypt(String Data) throws Exception {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALG);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(Data.getBytes());
            String encryptedValue = new BASE64Encoder().encode(encVal);
            return encryptedValue;
        }
                
        private static Key generateKey() throws Exception {
            Key key = new SecretKeySpec(keyV, ALG);
            return key;
        }
}