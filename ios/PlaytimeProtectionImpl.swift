import Foundation
import React

/// iOS implementation of the `PlaytimeProtection` TurboModule.
///
/// The protection features are provided by the Android SDK only, so every
/// method here is a stub that rejects as unsupported. The members still have to
/// exist because the Codegen-generated protocol is shared across platforms.
@objc public class PlaytimeProtectionImpl: NSObject {

  @objc public func faceVerification(_ resolve: @escaping RCTPromiseResolveBlock,
                                     reject: @escaping RCTPromiseRejectBlock) {
    ErrorsUtil.rejectUnsupported("faceVerification", reject)
  }

  @objc public func faceVerificationStatus(_ resolve: @escaping RCTPromiseResolveBlock,
                                           reject: @escaping RCTPromiseRejectBlock) {
    ErrorsUtil.rejectUnsupported("faceVerificationStatus", reject)
  }
}
