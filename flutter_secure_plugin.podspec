Pod::Spec.new do |s|
  s.name             = 'flutter_secure_plugin'
  s.version          = '0.0.1'
  s.summary          = 'A Flutter plugin for secure encryption and decryption.'
  s.description      = <<-DESC
  A Flutter plugin that provides secure encryption and decryption functionality for iOS and Android.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.platform = :ios, '11.0'
  s.swift_version = '5.0'
end
