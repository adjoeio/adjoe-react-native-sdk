#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import "RNPlaytimeSdkSpec.h"
#endif

#if __has_include(<react_native_adjoe_sdk/react_native_adjoe_sdk-Swift.h>)
#import <react_native_adjoe_sdk/react_native_adjoe_sdk-Swift.h>
#else
#import "react_native_adjoe_sdk-Swift.h"
#endif

@interface RNPlaytimeProtection : NSObject <RCTBridgeModule>
@end

#ifdef RCT_NEW_ARCH_ENABLED
@interface RNPlaytimeProtection () <NativePlaytimeProtectionSpec>
@end
#endif

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

#ifdef RCT_NEW_ARCH_ENABLED

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

#else // Old Architecture (legacy bridge)

RCT_EXPORT_METHOD(faceVerification:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  [_impl faceVerification:resolve reject:reject];
}

RCT_EXPORT_METHOD(faceVerificationStatus:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  [_impl faceVerificationStatus:resolve reject:reject];
}

#endif // RCT_NEW_ARCH_ENABLED

@end
