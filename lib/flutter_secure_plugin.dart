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

  static Future<String?> encryptarabic(String plainText) async {
    try {
      final result = await _channel
          .invokeMethod('encryptarabic', {'plainText': plainText});
      return result as String?;
    } on PlatformException catch (e) {
      print('Error encrypting Arabic text: ${e.message}');
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

  /// Encrypts the given text using PKICipherAES.
  /// This method is based on the encryptarabic method from the issue description.
  static Future<String?> encryptWithPKI(String plainText) async {
    try {
      final result =
          await _channel.invokeMethod('encryptWithPKI', {'plainText': plainText});
      return result as String?;
    } on PlatformException catch (e) {
      print('Error encrypting with PKI: ${e.message}');
      return null;
    } catch (e) {
      print('Unexpected error: $e');
      return null;
    }
  }

  /// Decrypts the given encrypted value using PKICipherAES.
  /// This method is the counterpart to encryptWithPKI.
  static Future<String?> decryptWithPKI(String encryptedValue) async {
    try {
      final result = await _channel
          .invokeMethod('decryptWithPKI', {'encryptedValue': encryptedValue});
      return result as String?;
    } on PlatformException catch (e) {
      print('Error decrypting with PKI: ${e.message}');
      return null;
    } catch (e) {
      print('Unexpected error: $e');
      return null;
    }
  }
}
