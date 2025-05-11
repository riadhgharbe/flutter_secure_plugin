package com.flutter_secure_plugin;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class FlutterSecurePlugin implements FlutterPlugin, MethodCallHandler {
    private static final String USER_LABEL = "76D92340AB1FEC58";
    private MethodChannel channel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_secure_plugin");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "encrypt":
                String plainText = call.argument("plainText");
                result.success(encrypt(plainText));
                break;
            case "encryptarabic":
                String arabicText = call.argument("plainText");
                result.success(encryptarabic(arabicText));
                break;
            case "decrypt":
                String encryptedValue = call.argument("encryptedValue");
                result.success(decrypt(encryptedValue));
                break;
            default:
                result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    private String encrypt(String plainText) {
        try {
            // Add prefix character
            String modifiedText = "X" + plainText;

            // Convert to UTF-8 bytes
            byte[] textBytes = modifiedText.getBytes("UTF-8");

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

            // Encrypt
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(textBytes);

            // Convert to Base64 and replace '+' with 'plus'
            String base64String = Base64.getEncoder().encodeToString(encryptedBytes);
            return base64String.replace("+", "plus");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String encryptarabic(String plainText) {
        try {
            // Add a random Arabic character as prefix (ا to ي)
            // Unicode range for Arabic letters: 0x0627 (ا) to 0x064A (ي)
            char arabicChar = (char) (0x0627 + (int) (Math.random() * (0x064A - 0x0627 + 1)));
            String modifiedText = arabicChar + plainText;

            // Convert to UTF-8 bytes
            byte[] textBytes = modifiedText.getBytes("UTF-8");

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

            // Encrypt
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(textBytes);

            // Convert to Base64 and replace '+' with 'plus'
            String base64String = Base64.getEncoder().encodeToString(encryptedBytes);
            return base64String.replace("+", "plus");
        } catch (Exception e) {
            e.printStackTrace();
            return plainText;
        }
    }

    private String decrypt(String encryptedValue) {
        try {
            // Replace 'plus' with '+'
            String modifiedValue = encryptedValue.replace("plus", "+");

            byte[] encryptedBytes;
            try {
                // First try standard Base64 decoding
                encryptedBytes = Base64.getDecoder().decode(modifiedValue);
            } catch (Exception e) {
                try {
                    // If standard decoding fails, try URL-safe Base64 decoding
                    encryptedBytes = Base64.getUrlDecoder().decode(modifiedValue);
                } catch (Exception e2) {
                    // If both fail, try with Apache Commons Codec style decoding
                    // This is a fallback for compatibility with the encryptarabic function in the issue description
                    // which uses Apache Commons Codec's Base64.encodeBase64()
                    String paddedValue = modifiedValue;
                    // Add padding if needed
                    while (paddedValue.length() % 4 != 0) {
                        paddedValue += "=";
                    }
                    encryptedBytes = Base64.getDecoder().decode(paddedValue);
                }
            }

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
