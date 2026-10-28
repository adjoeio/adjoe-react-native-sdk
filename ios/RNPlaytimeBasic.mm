#import <React/RCTBridge.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>

#ifdef RCT_NEW_ARCH_ENABLED
// Generated spec header (produced by Codegen for codegenConfig name
// "RNPlaytimeSdkSpec"). Only available on the New Architecture.
#import "RNPlaytimeSdkSpec.h"
#endif

// Generated Swift umbrella header (module name derived from the pod name
// "react-native-adjoe-sdk"). Only exists after a build. The angle-bracket form
// resolves when the pod builds as a framework (use_frameworks! :linkage =>
// :static, required by Firebase and common in Expo); the quoted form resolves
// when it builds as a plain static library.
#if __has_include(<react_native_adjoe_sdk/react_native_adjoe_sdk-Swift.h>)
#import <react_native_adjoe_sdk/react_native_adjoe_sdk-Swift.h>
#else
#import "react_native_adjoe_sdk-Swift.h"
#endif

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

@synthesize bridge = _bridge;

// Use the same name as on Android for compatibility.
RCT_EXPORT_MODULE(RNPlaytimeSdk)

// The device event name under which SDK events are delivered to JavaScript. Mirrors the
// Android constant and the JS NativeEventEmitter subscription.
static NSString *const kPlaytimeEventName = @"onPlaytimeEvent";

- (instancetype)init
{
  if (self = [super init]) {
    _impl = [PlaytimeSdkImpl new];

    // Forward SDK events emitted by the Swift impl to JavaScript as a device event. A
    // publisher's JS listener object cannot cross the bridge, so the JS NativeEventEmitter
    // relays this device event back to the registered listener's `didReceive` callback.
    __weak __typeof(self) weakSelf = self;
    _impl.onEvent = ^(NSDictionary *payload) {
      __strong __typeof(weakSelf) strongSelf = weakSelf;
      [strongSelf->_bridge enqueueJSCall:@"RCTDeviceEventEmitter"
                                  method:@"emit"
                                    args:@[kPlaytimeEventName, payload ?: @{}]
                              completion:NULL];
    };
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

- (void)addEventListener:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject
{
  [_impl addEventListener:resolve reject:reject];
}

- (void)removeEventListener:(RCTPromiseResolveBlock)resolve
                     reject:(RCTPromiseRejectBlock)reject
{
  [_impl removeEventListener:resolve reject:reject];
}

// Required by the NativeEventEmitter contract. Event delivery is driven by the SDK
// listener, so these are no-ops; they exist so the JS emitter has a native counterpart.
- (void)addListener:(NSString *)eventName {}
- (void)removeListeners:(double)count {}

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

RCT_EXPORT_METHOD(addEventListener:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  [_impl addEventListener:resolve reject:reject];
}

RCT_EXPORT_METHOD(removeEventListener:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  [_impl removeEventListener:resolve reject:reject];
}

// Required by the NativeEventEmitter contract on the Old Architecture. Event delivery is
// driven by the SDK listener, so these are no-ops.
RCT_EXPORT_METHOD(addListener:(NSString *)eventName) {}
RCT_EXPORT_METHOD(removeListeners:(double)count) {}

#endif

@end
