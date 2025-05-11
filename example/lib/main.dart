import 'package:flutter/material.dart';
import 'package:flutter_secure_plugin/flutter_secure_plugin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter Secure Plugin Example'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: () async {
                  // Test the problem token
                  final problemToken =
                      "DLIMo5lVmFHkatfAEx8aIzfUMQVtZiz8PAdsq9wrrfI=";

                  print("Original token: $problemToken");

                  String? decrypted =
                      await FlutterSecurePlugin.decrypt(problemToken);

                  print("Decrypted: $decrypted");
                },
                child: const Text('Test Decryption'),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: () async {
                  // Test the new PKI encryption and decryption methods
                  final plainText = "Hello, PKI encryption!";

                  print("Original text: $plainText");

                  // Encrypt using the new encryptWithPKI method
                  String? encrypted =
                      await FlutterSecurePlugin.encryptWithPKI(plainText);

                  print("Encrypted with PKI: $encrypted");

                  // Decrypt using the new decryptWithPKI method
                  String? decrypted =
                      await FlutterSecurePlugin.decryptWithPKI(encrypted!);

                  print("Decrypted with PKI: $decrypted");

                  // Verify that decryption succeeded and matches the original text
                  if (plainText == decrypted) {
                    print("SUCCESS: Decrypted value matches original text");
                  } else {
                    print(
                        "FAILURE: Decrypted value does not match original text");
                    print("Original: $plainText");
                    print("Decrypted: $decrypted");
                  }
                },
                child: const Text('Test PKI Encryption/Decryption'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
