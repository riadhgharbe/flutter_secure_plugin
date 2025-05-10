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

            // Convert to ISO-8859-1 bytes
            byte[] textBytes = modifiedText.getBytes("ISO-8859-1");

            // Create key
            Key key = new SecretKeySpec(USER_LABEL.getBytes("ISO-8859-1"), "AES");

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

    private String decrypt(String encryptedValue) {
        try {
            // Replace 'plus' with '+'
            String modifiedValue = encryptedValue.replace("plus", "+");

            // Convert from Base64
            byte[] encryptedBytes = Base64.getDecoder().decode(modifiedValue);

            // Create key
            Key key = new SecretKeySpec(USER_LABEL.getBytes("ISO-8859-1"), "AES");

            // Decrypt
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Convert to string and remove prefix character
            String decryptedText = new String(decryptedBytes, "ISO-8859-1");
            return decryptedText.substring(1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
