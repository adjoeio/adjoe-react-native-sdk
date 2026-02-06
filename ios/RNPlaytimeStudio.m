// RNPlaytimeStudio.m

#import "RNPlaytimeStudio.h"
#import "RNPlaytimeHelpers.h"

@implementation RNPlaytimeStudio

RCT_EXPORT_MODULE(PlaytimeStudio)

+ (NSString *)formatErrorMessage:(NSString *)baseMessage withError:(NSError *)error {
    if (error != nil && error.localizedDescription != nil && error.localizedDescription.length > 0) {
        return [NSString stringWithFormat:@"%@: %@", baseMessage, error.localizedDescription];
    }
    return baseMessage;
}

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

- (NSDictionary *)constantsToExport
{
  return @{
    @"VERSION": [Playtime getVersion],
  };
}

RCT_EXPORT_METHOD(
    getCampaigns:(NSDictionary *)paramsDictionary
    resolve:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    NSError *error = nil;
    PlaytimeOptions *playtimeOptions = [RNPlaytimeHelpers playtimeOptionsFromDictionary:paramsDictionary error:&error];
    
    if (playtimeOptions == nil || error != nil) {
        reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Invalid parameters for getCampaigns" withError:error], error);
        return;
    }
    
    id tokensValue = paramsDictionary[@"tokens"];
    NSMutableArray<NSString *> *tokens;

    if ([tokensValue isKindOfClass:[NSArray class]]) {
        NSArray *rawArray = (NSArray *)tokensValue;
        NSMutableArray<NSString *> *stringArray = [NSMutableArray array];

        for (id item in rawArray) {
            if ([item isKindOfClass:[NSString class]]) {
                [stringArray addObject:(NSString *)item];
            }
        }

        if (stringArray.count > 0) {
            tokens = stringArray;
        }
    }
    
    if (tokens == nil) {
        [PlaytimeStudio getCampaignsWithOptions:playtimeOptions
                                       completionHandler: ^(PlaytimeCampaignsResponse * _Nullable response, NSError * _Nullable error) {
            if (error != nil) {
                RCTLogError(@"Error getting campaigns: %@", error);
                reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Error getting campaigns" withError:error], error);
                return;
            }
            
            NSDictionary *responseDictionary = [response toJSONObject];
            if (response == nil) {
                RCTLogError(@"Error parsing campaigns response");
                reject(@"playtime_error", @"Error parsing campaigns response", nil);
                return;
            }
            
            resolve(responseDictionary);
        }];
    } else {
        [PlaytimeStudio getCampaignsWithTokens:tokens
                                       options:playtimeOptions
                             completionHandler: ^(PlaytimeCampaignsResponse * _Nullable response, NSError * _Nullable error) {
            if (error != nil) {
                RCTLogError(@"Error getting campaigns with tokens: %@", error);
                reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Error getting campaigns with tokens" withError:error], error);
                return;
            }
            
            NSDictionary *responseDictionary = [response toJSONObject];
            if (response == nil) {
                RCTLogError(@"Error parsing campaigns with tokens response");
                reject(@"playtime_error", @"Error parsing campaigns with tokens response", nil);
                return;
            }
            
            resolve(responseDictionary);
        }];
    }
}

RCT_EXPORT_METHOD(
      getInstalledCampaigns:(NSDictionary *)paramsDictionary
      resolve:(RCTPromiseResolveBlock)resolve
      reject:(RCTPromiseRejectBlock)reject)
{
    NSError *error = nil;
    PlaytimeOptions *playtimeOptions = [RNPlaytimeHelpers playtimeOptionsFromDictionary:paramsDictionary error:&error];
    
    if (playtimeOptions == nil || error != nil) {
        reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Invalid parameters for getCampaigns" withError:error], error);
        return;
    }
    
    [PlaytimeStudio getInstalledCampaignsWithOptions:playtimeOptions
                                   completionHandler: ^(PlaytimeCampaignsResponse * _Nullable response, NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error getting installed apps: %@", error);
            reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Error getting installed apps" withError:error], error);
            return;
        }
        
        NSDictionary *responseDictionary = [response toJSONObject];
        if (response == nil) {
            RCTLogError(@"Error parsing installed apps response");
            reject(@"playtime_error", @"Error parsing installed apps response", nil);
            return;
        }
        
        resolve(responseDictionary);
    }];
}

RCT_EXPORT_METHOD(
    openInStore:(NSDictionary *)campaignDictionary
    resolver:(RCTPromiseResolveBlock)resolve
    rejecter:(RCTPromiseRejectBlock)reject)
{
    NSError *error = nil;
    PlaytimeCampaign *campaign = [[PlaytimeCampaign alloc] initWithJSONObject:campaignDictionary error:&error];
    
    if (campaign == nil || error != nil) {
        reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Invalid parameters for openStore" withError:error], error);
        return;
    }
    
    [PlaytimeStudio openInStoreWithCampaign:campaign
                  completionHandler:^(NSError * _Nullable error) {
        if (!error) {
            RCTLog(@"Opened store successfully");
            resolve(nil);
        } else {
            RCTLogError(@"Error opening store: %@", error);
            reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Open store error" withError:error], error);
        }
    }];
}

RCT_EXPORT_METHOD(
    openInstalledCampaign:(NSDictionary *)campaignDictionary
    resolver:(RCTPromiseResolveBlock)resolve
    rejecter:(RCTPromiseRejectBlock)reject)
{
    NSError *error = nil;
    PlaytimeCampaign *campaign = [[PlaytimeCampaign alloc] initWithJSONObject:campaignDictionary error:&error];
    
    if (campaign == nil || error != nil) {
        reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Invalid parameters for openStore" withError:error], error);
        return;
    }
    
    [PlaytimeStudio openInstalledCampaignWithCampaign:campaign
                  completionHandler:^(NSError * _Nullable error) {
        if (!error) {
            RCTLog(@"Opened store successfully");
            resolve(nil);
        } else {
            RCTLogError(@"Error opening store: %@", error);
            reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Open store error" withError:error], error);
        }
    }];
}

RCT_EXPORT_METHOD(
    getPermissions:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    [PlaytimeStudio getPermissionsWithCompletionHandler:^(PlaytimePermissionsResponse * _Nullable response, NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error getting permissions: %@", error);
            reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Error getting permissions" withError:error], error);
            return;
        }
        
        NSDictionary *responseDictionary = [response toJSONObject];
        if (response == nil) {
            RCTLogError(@"Error parsing permissions response");
            reject(@"playtime_error", @"Error parsing permissions response", nil);
            return;
        }
        
        resolve(responseDictionary);
    }];
}

RCT_EXPORT_METHOD(
    showPermissionsPrompt:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    [PlaytimeStudio showPermissionsPromptWithCompletionHandler:^(PlaytimePermissionsResponse * _Nullable response, NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error getting permissions: %@", error);
            reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Error getting permissions" withError:error], error);
            return;
        }
        
        NSDictionary *responseDictionary = [response toJSONObject];
        if (response == nil) {
            RCTLogError(@"Error parsing permissions response");
            reject(@"playtime_error", @"Error parsing permissions response", nil);
            return;
        }
        
        resolve(responseDictionary);
    }];
}

RCT_EXPORT_METHOD(
    registerRewardsConnect:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    [PlaytimeStudio registerRewardsConnectWithCompletionHandler:^(NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error registering for rewards connect: %@", error);
            reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Error registering for rewards connect" withError:error], error);
            return;
        }
        
        resolve(nil);
    }];
}

RCT_EXPORT_METHOD(
    resetRewardsConnect:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    [PlaytimeStudio resetRewardsConnectWithCompletionHandler:^(NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error resetting rewards connect: %@", error);
            reject(@"playtime_error", [RNPlaytimeStudio formatErrorMessage:@"Error resetting rewards connect" withError:error], error);
            return;
        }
        
        resolve(nil);
    }];
}

@end
