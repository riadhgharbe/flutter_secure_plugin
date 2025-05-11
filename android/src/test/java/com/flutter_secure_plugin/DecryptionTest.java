import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class DecryptionTest {
    private static final String USER_LABEL = "76D92340AB1FEC58";
    
    public static void main(String[] args) {
        String encryptedValue = "DLIMo5lVmFHkatfAEx8aIzfUMQVtZiz8PAdsq9wrrfI=";
        String decryptedValue = decrypt(encryptedValue);
        System.out.println("Decrypted value: " + decryptedValue);
    }
    
    private static String decrypt(String encryptedValue) {
        try {
            // Replace 'plus' with '+'
            String modifiedValue = encryptedValue.replace("plus", "+");
            
            // Convert from Base64
            byte[] encryptedBytes = Base64.getDecoder().decode(modifiedValue);
            
            // Create key
            Key key = new SecretKeySpec(USER_LABEL.getBytes("UTF-8"), "AES");
            
            // Decrypt
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            
            // Convert to string and remove prefix character
            String decryptedText = new String(decryptedBytes, "UTF-8");
            return decryptedText.substring(1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}