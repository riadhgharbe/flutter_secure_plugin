import 'package:flutter/services.dart';

class FlutterSecurePlugin {
  static const MethodChannel _channel = MethodChannel('flutter_secure_plugin');

  static Future<String?> encrypt(String plainText) async {
    try {
      final result =
          await _channel.invokeMethod('encrypt', {'plainText': plainText});
      return result as String?;
    } on PlatformException catch (e) {
      print('Error encrypting: ${e.message}');
      return null;
    } catch (e) {
      print('Unexpected error: $e');
      return null;
    }
  }

  static Future<String?> decrypt(String encryptedValue) async {
    try {
      final result = await _channel
          .invokeMethod('decrypt', {'encryptedValue': encryptedValue});
      return result as String?;
    } on PlatformException catch (e) {
      print('Error decrypting: ${e.message}');
      return null;
    } catch (e) {
      print('Unexpected error: $e');
      return null;
    }
  }
}
