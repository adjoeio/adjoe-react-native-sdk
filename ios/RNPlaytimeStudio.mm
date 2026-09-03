// RNPlaytimeStudio.mm
//
// Thin Objective-C++ shim for the `PlaytimeStudio` TurboModule. It adopts the
// Codegen-generated `NativePlaytimeStudioSpec` protocol and forwards each call
// to a Swift `PlaytimeStudioImpl` instance, which holds the real SDK logic.

#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>

#import "RNPlaytimeSdkSpec.h"
#import "react_native_adjoe_sdk-Swift.h"

@interface RNPlaytimeStudio : NSObject <NativePlaytimeStudioSpec>
@end

@implementation RNPlaytimeStudio {
  PlaytimeStudioImpl *_impl;
}

RCT_EXPORT_MODULE(PlaytimeStudio)

- (instancetype)init
{
  if (self = [super init]) {
    _impl = [PlaytimeStudioImpl new];
  }
  return self;
}

+ (BOOL)requiresMainQueueSetup
{
  return YES;
}

- (void)updateTosConsent:(double)action
                 resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject
{
  [_impl updateTosConsent:action resolve:resolve reject:reject];
}

- (void)getCampaigns:(NSDictionary *)options
             resolve:(RCTPromiseResolveBlock)resolve
              reject:(RCTPromiseRejectBlock)reject
{
  [_impl getCampaigns:options resolve:resolve reject:reject];
}

- (void)getOfferDetails:(NSArray *)tokens
        campaignOptions:(NSDictionary *)options
                resolve:(RCTPromiseResolveBlock)resolve
                 reject:(RCTPromiseRejectBlock)reject
{
  [_impl getOfferDetails:tokens campaignOptions:options resolve:resolve reject:reject];
}

- (void)getInstalledCampaigns:(NSDictionary *)options
                      resolve:(RCTPromiseResolveBlock)resolve
                       reject:(RCTPromiseRejectBlock)reject
{
  [_impl getInstalledCampaigns:options resolve:resolve reject:reject];
}

- (void)openInStore:(NSDictionary *)campaign
   useInlineInstall:(BOOL)useInlineInstall
            resolve:(RCTPromiseResolveBlock)resolve
             reject:(RCTPromiseRejectBlock)reject
{
  [_impl openInStore:campaign useInlineInstall:useInlineInstall resolve:resolve reject:reject];
}

- (void)openInstalledCampaign:(NSDictionary *)campaign
                      resolve:(RCTPromiseResolveBlock)resolve
                       reject:(RCTPromiseRejectBlock)reject
{
  [_impl openInstalledCampaign:campaign resolve:resolve reject:reject];
}

- (void)openChatbot:(NSDictionary *)campaign
            resolve:(RCTPromiseResolveBlock)resolve
             reject:(RCTPromiseRejectBlock)reject
{
  [_impl openChatbot:campaign resolve:resolve reject:reject];
}

- (void)resetRewardsConnect:(RCTPromiseResolveBlock)resolve
                     reject:(RCTPromiseRejectBlock)reject
{
  [_impl resetRewardsConnect:resolve reject:reject];
}

- (void)executeCampaignImpression:(NSDictionary *)campaign
                   impressionType:(double)impressionType
                          resolve:(RCTPromiseResolveBlock)resolve
                           reject:(RCTPromiseRejectBlock)reject
{
  [_impl executeCampaignImpression:campaign impressionType:impressionType resolve:resolve reject:reject];
}

- (void)executeOfferImpression:(NSString *)token
                         appID:(NSString *)appID
                       resolve:(RCTPromiseResolveBlock)resolve
                        reject:(RCTPromiseRejectBlock)reject
{
  [_impl executeOfferImpression:token appID:appID resolve:resolve reject:reject];
}

- (void)registerRewardsConnect:(RCTPromiseResolveBlock)resolve
                        reject:(RCTPromiseRejectBlock)reject
{
  [_impl registerRewardsConnect:resolve reject:reject];
}

- (void)showCampaignDetails:(NSDictionary *)campaign
                    resolve:(RCTPromiseResolveBlock)resolve
                     reject:(RCTPromiseRejectBlock)reject
{
  [_impl showCampaignDetails:campaign resolve:resolve reject:reject];
}

- (void)showOfferDetails:(NSString *)token
                   appID:(NSString *)appID
                 resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject
{
  [_impl showOfferDetails:token appID:appID resolve:resolve reject:reject];
}

- (void)showInstalledCampaigns:(RCTPromiseResolveBlock)resolve
                        reject:(RCTPromiseRejectBlock)reject
{
  [_impl showInstalledCampaigns:resolve reject:reject];
}

- (void)showPermissionsPrompt:(RCTPromiseResolveBlock)resolve
                       reject:(RCTPromiseRejectBlock)reject
{
  [_impl showPermissionsPrompt:resolve reject:reject];
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativePlaytimeStudioSpecJSI>(params);
}

@end
