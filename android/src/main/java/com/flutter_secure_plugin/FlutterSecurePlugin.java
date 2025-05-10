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
import javax.crypto.spec.IvParameterSpec;

public class FlutterSecurePlugin implements FlutterPlugin, MethodCallHandler {
    private static final String USER_LABEL = "76D92340AB1FEC58";
    private static final String IV = "1234567890abcdef"; // 16 bytes IV
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
            Key key = new SecretKeySpec(USER_LABEL.getBytes("UTF-8"), "AES");
            
            // Create IV
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes("UTF-8"));
            
            // Encrypt with CBC mode and PKCS5Padding
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] encryptedBytes = cipher.doFinal(textBytes);

            // Convert to Base64 and replace '+' with 'plus'
            String base64String = Base64.getEncoder().encodeToString(encryptedBytes);
            return base64String.replace("+", "plus");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decrypt(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.isEmpty()) {
            throw new IllegalArgumentException("Encrypted value cannot be null or empty");
        }

        try {
            // Replace 'plus' with '+'
            String modifiedValue = encryptedValue.replace("plus", "+");
            
            // Convert from Base64
            byte[] encryptedBytes = Base64.getDecoder().decode(modifiedValue);
            
            // Create key
            Key key = new SecretKeySpec(USER_LABEL.getBytes("UTF-8"), "AES");
            
            // Create IV
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes("UTF-8"));
            
            // Decrypt with CBC mode and PKCS5Padding
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Convert to string and remove prefix character
            String decryptedText = new String(decryptedBytes, "UTF-8");
            
            // Validate the decrypted text starts with 'X'
            if (decryptedText == null || !decryptedText.startsWith("X")) {
                throw new SecurityException("Invalid decryption result");
            }
            
            return decryptedText.substring(1);
        } catch (Exception e) {
            throw new SecurityException("Decryption failed: " + e.getMessage(), e);
        }
    }
}
