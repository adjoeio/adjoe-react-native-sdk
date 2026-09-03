// RNPlaytimeProtection.mm
//
// Thin Objective-C++ shim for the `PlaytimeProtection` TurboModule. It adopts
// the Codegen-generated `NativePlaytimeProtectionSpec` protocol and forwards
// each call to a Swift `PlaytimeProtectionImpl` instance.
//
// The protection features exist on Android only, so the Swift implementation
// rejects every call as unsupported. The module is still registered on iOS so
// that `TurboModuleRegistry.getEnforcing` does not throw when the JS layer is
// loaded on both platforms.

#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>

#import "RNPlaytimeSdkSpec.h"
#import "react_native_adjoe_sdk-Swift.h"

@interface RNPlaytimeProtection : NSObject <NativePlaytimeProtectionSpec>
@end

@implementation RNPlaytimeProtection {
  PlaytimeProtectionImpl *_impl;
}

RCT_EXPORT_MODULE(PlaytimeProtection)

- (instancetype)init
{
  if (self = [super init]) {
    _impl = [PlaytimeProtectionImpl new];
  }
  return self;
}

+ (BOOL)requiresMainQueueSetup
{
  return YES;
}

- (void)faceVerification:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject
{
  [_impl faceVerification:resolve reject:reject];
}

- (void)faceVerificationStatus:(RCTPromiseResolveBlock)resolve
                        reject:(RCTPromiseRejectBlock)reject
{
  [_impl faceVerificationStatus:resolve reject:reject];
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativePlaytimeProtectionSpecJSI>(params);
}

@end
