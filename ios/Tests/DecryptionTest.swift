import Foundation
import CommonCrypto

extension Data {
    var bytes: UnsafeRawPointer {
        return (self as NSData).bytes
    }
}

class DecryptionTest {
    private let userLabel = "76D92340AB1FEC58"

    func testDecryption() {
        // Test both formats mentioned in the issue description
        let encryptedValue1 = "QGq1S5vBPL98/UHHyvp6gw=="
        let encryptedValue2 = "DLIMo5lVmFHkatfAEx8aIzfUMQVtZiz8PAdsq9wrrfI="

        print("Testing format 1: \(encryptedValue1)")
        if let decryptedValue1 = decrypt(encryptedValue1) {
            print("Decrypted value 1: \(decryptedValue1)")
        } else {
            print("Decryption of format 1 failed")
        }

        print("\nTesting format 2: \(encryptedValue2)")
        if let decryptedValue2 = decrypt(encryptedValue2) {
            print("Decrypted value 2: \(decryptedValue2)")
        } else {
            print("Decryption of format 2 failed")
        }
    }

    private func decrypt(_ encryptedValue: String) -> String? {
        // Replace 'plus' with '+'
        let modifiedValue = encryptedValue.replacingOccurrences(of: "plus", with: "+")

        // Try different Base64 decoding approaches
        var encryptedData: Data?

        // 1. Try standard Base64 decoding
        if let data = Data(base64Encoded: modifiedValue) {
            encryptedData = data
            print("Standard Base64 decoding succeeded")
        } 
        // 2. Try URL-safe Base64 decoding (replace - with + and _ with /)
        else if let data = Data(base64Encoded: modifiedValue
            .replacingOccurrences(of: "-", with: "+")
            .replacingOccurrences(of: "_", with: "/")) {
            encryptedData = data
            print("URL-safe Base64 decoding succeeded")
        }
        // 3. Try adding padding if needed
        else {
            var paddedValue = modifiedValue
            // Add padding if needed
            while paddedValue.count % 4 != 0 {
                paddedValue += "="
            }
            if let data = Data(base64Encoded: paddedValue) {
                encryptedData = data
                print("Padded Base64 decoding succeeded")
            }
        }

        // If all decoding attempts failed, return nil
        guard let encryptedData = encryptedData else {
            print("All Base64 decoding attempts failed")
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
            print("Decryption failed with status: \(status)")
            return nil
        }

        // Convert decrypted data to string using UTF-8
        // Use prefix(while:) to get only the non-zero bytes
        let decryptedData = Data(buffer.prefix(while: { $0 != 0 }))
        guard let decryptedString = String(data: decryptedData, encoding: .utf8) else {
            print("Failed to convert decrypted data to string")
            return nil
        }

        // Remove prefix character
        return String(decryptedString.dropFirst())
    }
}

// Run the test
let test = DecryptionTest()
test.testDecryption()
