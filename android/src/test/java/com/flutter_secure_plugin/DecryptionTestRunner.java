package com.flutter_secure_plugin;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class DecryptionTestRunner {
    private static final String USER_LABEL = "76D92340AB1FEC58";

    public static void main(String[] args) {
        // Test the encrypted value from the issue description
        String encryptedValue = "DLIMo5lVmFHkatfAEx8aIzfUMQVtZiz8PAdsq9wrrfI=";
        String decryptedValue = decrypt(encryptedValue);
        
        System.out.println("Encrypted value: " + encryptedValue);
        System.out.println("Decrypted value: " + decryptedValue);
        
        // Also test with the key from the DecryptArabic class
        String decryptedWithPlaceholderKey = decryptWithKey(encryptedValue, "placeholderkey123");
        System.out.println("Decrypted with placeholder key: " + decryptedWithPlaceholderKey);
    }

    private static String decrypt(String encryptedValue) {
        return decryptWithKey(encryptedValue, USER_LABEL);
    }

    private static String decryptWithKey(String encryptedValue, String keyString) {
        try {
            // Replace 'plus' with '+'
            String modifiedValue = encryptedValue.replace("plus", "+");

            byte[] encryptedBytes;
            try {
                // First try standard Base64 decoding
                encryptedBytes = Base64.getDecoder().decode(modifiedValue);
                System.out.println("Standard Base64 decoding succeeded");
            } catch (Exception e) {
                try {
                    // If standard decoding fails, try URL-safe Base64 decoding
                    encryptedBytes = Base64.getUrlDecoder().decode(modifiedValue);
                    System.out.println("URL-safe Base64 decoding succeeded");
                } catch (Exception e2) {
                    // If both fail, try with Apache Commons Codec style decoding
                    String paddedValue = modifiedValue;
                    // Add padding if needed
                    while (paddedValue.length() % 4 != 0) {
                        paddedValue += "=";
                    }
                    encryptedBytes = Base64.getDecoder().decode(paddedValue);
                    System.out.println("Padded Base64 decoding succeeded");
                }
            }

            // Create key
            // Ensure key is exactly 16 bytes (128 bits) for AES-128
            byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
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
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
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