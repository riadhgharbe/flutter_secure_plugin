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
            // Ensure key is exactly 16 bytes (128 bits) for AES-128
            byte[] keyBytes = USER_LABEL.getBytes("UTF-8");
            if (keyBytes.length != 16) {
                // If key is not 16 bytes, use first 16 bytes or pad with zeros
                byte[] adjustedKey = new byte[16];
                System.arraycopy(keyBytes, 0, adjustedKey, 0, Math.min(keyBytes.length, 16));
                keyBytes = adjustedKey;
            }
            Key key = new SecretKeySpec(keyBytes, "AES");

            // Decrypt
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Convert to string and remove prefix character
            String decryptedText = new String(decryptedBytes, "UTF-8");
            if (decryptedText.length() > 0) {
                return decryptedText.substring(1);
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
