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
        let encryptedValue = "DLIMo5lVmFHkatfAEx8aIzfUMQVtZiz8PAdsq9wrrfI="
        if let decryptedValue = decrypt(encryptedValue) {
            print("Decrypted value: \(decryptedValue)")
        } else {
            print("Decryption failed")
        }
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
        let decryptedData = Data(buffer[0..<Int(buffer.count)])
        guard let decryptedString = String(data: decryptedData, encoding: .utf8) else {
            return nil
        }

        // Remove prefix character
        return String(decryptedString.dropFirst())
    }
}

// Run the test
let test = DecryptionTest()
test.testDecryption()
