#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>

#ifdef RCT_NEW_ARCH_ENABLED
// Generated spec header (produced by Codegen for codegenConfig name
// "RNPlaytimeSdkSpec"). Only available on the New Architecture.
#import "RNPlaytimeSdkSpec.h"
#endif

// Generated Swift umbrella header (module name derived from the pod name
// "react-native-adjoe-sdk"). Only exists after a build.
#import "react_native_adjoe_sdk-Swift.h"

// The module always conforms to `RCTBridgeModule` so it registers on the legacy
// Native Modules registry (Old Architecture). On the New Architecture it also
// conforms to the Codegen-generated spec protocol via the class extension below.
@interface RNPlaytimeBasic : NSObject <RCTBridgeModule>
@end

#ifdef RCT_NEW_ARCH_ENABLED
@interface RNPlaytimeBasic () <NativeRNPlaytimeSdkSpec>
@end
#endif

@implementation RNPlaytimeBasic {
  PlaytimeSdkImpl *_impl;
}

// Use the same name as on Android for compatibility.
RCT_EXPORT_MODULE(RNPlaytimeSdk)

- (instancetype)init
{
  if (self = [super init]) {
    _impl = [PlaytimeSdkImpl new];
  }
  return self;
}

+ (BOOL)requiresMainQueueSetup
{
  return YES;
}

// MARK: - Basic integration

#ifdef RCT_NEW_ARCH_ENABLED

- (void)initPlaytime:(NSDictionary *)options
             resolve:(RCTPromiseResolveBlock)resolve
              reject:(RCTPromiseRejectBlock)reject
{
  [_impl initPlaytime:options resolve:resolve reject:reject];
}

- (void)showCatalog:(NSDictionary *)catalogOptions
            resolve:(RCTPromiseResolveBlock)resolve
             reject:(RCTPromiseRejectBlock)reject
{
  [_impl showCatalog:catalogOptions resolve:resolve reject:reject];
}

- (void)getStatus:(RCTPromiseResolveBlock)resolve
           reject:(RCTPromiseRejectBlock)reject
{
  [_impl getStatus:resolve reject:reject];
}

- (void)teardown:(RCTPromiseResolveBlock)resolve
          reject:(RCTPromiseRejectBlock)reject
{
  [_impl teardown:resolve reject:reject];
}

- (void)executeImpressionEvent:(NSDictionary *)catalogPlacement
                       resolve:(RCTPromiseResolveBlock)resolve
                        reject:(RCTPromiseRejectBlock)reject
{
  [_impl executeImpressionEvent:catalogPlacement resolve:resolve reject:reject];
}

// MARK: - TurboModule

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativeRNPlaytimeSdkSpecJSI>(params);
}

#else // RCT_NEW_ARCH_ENABLED not defined -> Old Architecture (legacy bridge)

RCT_EXPORT_METHOD(initPlaytime:(NSDictionary *)options
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  [_impl initPlaytime:options resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(showCatalog:(NSDictionary *)catalogOptions
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  [_impl showCatalog:catalogOptions resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(getStatus:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  [_impl getStatus:resolve reject:reject];
}

RCT_EXPORT_METHOD(teardown:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  [_impl teardown:resolve reject:reject];
}

RCT_EXPORT_METHOD(executeImpressionEvent:(NSDictionary *)catalogPlacement
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  [_impl executeImpressionEvent:catalogPlacement resolve:resolve reject:reject];
}

#endif

@end
