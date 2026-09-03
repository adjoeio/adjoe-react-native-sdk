// RNPlaytimeBasic.mm
//
// Thin Objective-C++ shim for the `RNPlaytimeSdk` TurboModule.
//
// Swift cannot conform to the Codegen-generated C++ TurboModule protocol or
// implement `getTurboModule:` directly, so this shim:
//   * adopts the generated `NativeRNPlaytimeSdkSpec` protocol,
//   * holds a Swift `PlaytimeSdkImpl` instance and forwards each call to it,
//   * implements `getTurboModule:` to return the generated JSI class.
//
// The real SDK logic lives in PlaytimeSdkImpl.swift.

#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>

// Generated spec header (produced by Codegen for codegenConfig name
// "RNPlaytimeSdkSpec").
#import "RNPlaytimeSdkSpec.h"

// Generated Swift umbrella header (module name derived from the pod name
// "react-native-adjoe-sdk"). Only exists after a build.
#import "react_native_adjoe_sdk-Swift.h"

@interface RNPlaytimeBasic : NSObject <NativeRNPlaytimeSdkSpec>
@end

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

// MARK: - TurboModule

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativeRNPlaytimeSdkSpecJSI>(params);
}

@end
