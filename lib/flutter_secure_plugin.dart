import 'package:flutter/services.dart';

class FlutterSecurePlugin {
  static const MethodChannel _channel = MethodChannel('flutter_secure_plugin');

  static Future<String> encrypt(String plainText) async {
    final String result = await _channel.invokeMethod('encrypt', {'plainText': plainText});
    return result;
  }

  static Future<String> decrypt(String encryptedValue) async {
    final String result = await _channel.invokeMethod('decrypt', {'encryptedValue': encryptedValue});
    return result;
  }
}
