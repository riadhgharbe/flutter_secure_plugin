import Flutter
import CommonCrypto

public class SwiftFlutterSecurePlugin: NSObject, FlutterPlugin {
    private let userLabel = "76D92340AB1FEC58"

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "flutter_secure_plugin", binaryMessenger: registrar.messenger())
        let instance = SwiftFlutterSecurePlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "encrypt":
            guard let args = call.arguments as? [String: Any],
                  let plainText = args["plainText"] as? String else {
                result(FlutterError(code: "INVALID_ARGUMENTS",
                                  message: "Invalid arguments provided",
                                  details: nil))
                return
            }
            result(encrypt(plainText))
        case "encryptarabic":
            guard let args = call.arguments as? [String: Any],
                  let plainText = args["plainText"] as? String else {
                result(FlutterError(code: "INVALID_ARGUMENTS",
                                  message: "Invalid arguments provided",
                                  details: nil))
                return
            }
            result(encryptarabic(plainText))
        case "decrypt":
            guard let args = call.arguments as? [String: Any],
                  let encryptedValue = args["encryptedValue"] as? String else {
                result(FlutterError(code: "INVALID_ARGUMENTS",
                                  message: "Invalid arguments provided",
                                  details: nil))
                return
            }
            result(decrypt(encryptedValue))
        default:
            result(FlutterMethodNotImplemented)
        }
    }

    private func encrypt(_ plainText: String) -> String? {
        // Add prefix character
        let modifiedPlainText = "X" + plainText

        // Convert to data using UTF-8
        guard let plainData = modifiedPlainText.data(using: .utf8) else {
            return nil
        }

        // Get key data using UTF-8
        guard let keyData = userLabel.data(using: .utf8), keyData.count == 16 else {
            return nil
        }

        // Create buffer for encrypted data
        let bufferSize = plainData.count + kCCBlockSizeAES128
        var buffer = [UInt8](repeating: 0, count: bufferSize)

        // Perform encryption
        let status = CCCrypt(CCOperation(kCCEncrypt),
                            CCAlgorithm(kCCAlgorithmAES),
                            CCOptions(kCCOptionPKCS7Padding),
                            keyData.bytes, keyData.count,
                            nil,
                            plainData.bytes, plainData.count,
                            &buffer, buffer.count,
                            nil)

        if status != CCCryptorStatus(kCCSuccess) {
            return nil
        }

        // Convert to Base64 and replace '+' with 'plus'
        let encryptedData = Data(buffer[0..<Int(buffer.count)])
        let base64String = encryptedData.base64EncodedString()
        return base64String.replacingOccurrences(of: "+", with: "plus")
    }

    private func encryptarabic(_ plainText: String) -> String? {
        // Add a random Arabic character as prefix (ا to ي)
        // Unicode range for Arabic letters: 0x0627 (ا) to 0x064A (ي)
        let arabicCharCode = Int.random(in: 0x0627...0x064A)
        let arabicChar = UnicodeScalar(arabicCharCode)!
        let modifiedPlainText = String(arabicChar) + plainText

        // Convert to data using UTF-8
        guard let plainData = modifiedPlainText.data(using: .utf8) else {
            return nil
        }

        // Get key data using UTF-8
        guard let keyData = userLabel.data(using: .utf8), keyData.count == 16 else {
            return nil
        }

        // Create buffer for encrypted data
        let bufferSize = plainData.count + kCCBlockSizeAES128
        var buffer = [UInt8](repeating: 0, count: bufferSize)

        // Perform encryption
        let status = CCCrypt(CCOperation(kCCEncrypt),
                            CCAlgorithm(kCCAlgorithmAES),
                            CCOptions(kCCOptionPKCS7Padding),
                            keyData.bytes, keyData.count,
                            nil,
                            plainData.bytes, plainData.count,
                            &buffer, buffer.count,
                            nil)

        if status != CCCryptorStatus(kCCSuccess) {
            return nil
        }

        // Convert to Base64 and replace '+' with 'plus'
        let encryptedData = Data(buffer.prefix(while: { $0 != 0 }))
        let base64String = encryptedData.base64EncodedString()
        return base64String.replacingOccurrences(of: "+", with: "plus")
    }

    private func decrypt(_ encryptedValue: String) -> String? {
        // Replace 'plus' with '+'
        let modifiedValue = encryptedValue.replacingOccurrences(of: "plus", with: "+")

        // Base64 decode
        guard let encryptedData = Data(base64Encoded: modifiedValue) else {
            return nil
        }

        // Get key data
        guard let keyData = userLabel.data(using: .utf8), keyData.count == 16 else {
            return nil
        }

        // Create buffer for decrypted data
        let bufferSize = encryptedData.count + kCCBlockSizeAES128
        var buffer = [UInt8](repeating: 0, count: bufferSize)

        // Perform decryption
        let status = CCCrypt(CCOperation(kCCDecrypt),
                            CCAlgorithm(kCCAlgorithmAES),
                            CCOptions(kCCOptionPKCS7Padding),
                            keyData.bytes, keyData.count,
                            nil,
                            encryptedData.bytes, encryptedData.count,
                            &buffer, buffer.count,
                            nil)

        if status != CCCryptorStatus(kCCSuccess) {
            return nil
        }

        // Convert decrypted data to string using UTF-8
        guard let decryptedData = Data(buffer[0..<Int(buffer.count)]),
              let decryptedString = String(data: decryptedData, encoding: .utf8) else {
            return nil
        }

        // Remove prefix character
        return String(decryptedString.dropFirst())
    }
}
