# react-native-adjoe-sdk.podspec

require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-adjoe-sdk"
  s.version      = package["version"]
  s.summary      = "Monetization solution for iOS"
  s.description  = "A native iOS monetization solution for app publishers aiming to boost revenue and engagement through rewarded gaming and app interactions."
  s.license      = { :type => 'Commercial', :text => 'adjoe GmbH' }
  s.homepage     = "https://adjoe.io/solutions/monetize-your-app/playtime/"
  s.platforms    = { :ios => '13.0' }
  s.source       = { :git => "https://github.com/github_account/react-native-adjoe-sdk.git", :tag => "#{s.version}" }
  s.author       = { "adjoe" => "support@adjoe.atlassian.net" }

  # Includes the Objective-C++ TurboModule shims (.mm), the Swift logic classes
  # (.swift) and the shared ObjC helper (.h/.m).
  s.source_files = "ios/**/*.{h,m,mm,swift}"
  s.requires_arc = true

  s.dependency 'PlaytimeSDK', '5.0.0-beta.3'

  # Wires up the New Architecture (TurboModules) dependencies and the
  # RCT_NEW_ARCH_ENABLED / Codegen setup. Provided by the React Native pod
  # scripts; on the legacy architecture it falls back to depending on
  # React-Core.
  install_modules_dependencies(s)
end
