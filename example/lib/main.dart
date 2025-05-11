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
            ],
          ),
        ),
      ),
    );
  }
}
