#import "RNPlaytimeBasic.h"
#import "RNPlaytimeHelpers.h"

@implementation RNPlaytimeBasic

// Use the same name as on Android for compatibility
RCT_EXPORT_MODULE(RNPlaytimeSdk)

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

+ (NSString *)campaignStateToString:(NSNumber *)state {
    if (state == nil) return nil;
    switch ([state integerValue]) {
        case 0: return @"READY";
        case 1: return @"BLOCKED";
        case 2: return @"VPN_DETECTED";
        case 3: return @"GEO_MISMATCH";
        default: return nil;
    }
}

RCT_EXPORT_METHOD(
    showCatalogWithOptions:(NSDictionary *)paramsDictionary
    resolve:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    NSError *error = nil;
    PlaytimeOptions *playtimeOptions = [RNPlaytimeHelpers playtimeOptionsFromDictionary:paramsDictionary error:&error];
    
    if (playtimeOptions == nil || error != nil) {
        reject(@"playtime_error", [RNPlaytimeBasic formatErrorMessage:@"Invalid parameters for showCatalog" withError:error], error);
        return;
    }
    
    [Playtime showCatalogWithOptions:playtimeOptions
                   completionHandler:^(NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error showing Playtime catalog: %@", error);
            reject(@"playtime_error", [RNPlaytimeBasic formatErrorMessage:@"Playtime catalog error" withError:error], error);
            return;
        }
        
        resolve(nil);
    }];
}

RCT_EXPORT_METHOD(
    setPlaytimeOptions:(NSDictionary *)paramsDictionary
    resolve:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    NSError *error = nil;
    PlaytimeOptions *playtimeOptions = [RNPlaytimeHelpers playtimeOptionsFromDictionary:paramsDictionary error:&error];
    
    if (playtimeOptions == nil || error != nil) {
        reject(@"playtime_error", [RNPlaytimeBasic formatErrorMessage:@"Invalid parameters for setPlaytimeOptions" withError:error], error);
        return;
    }
    
    [Playtime setPlaytimeOptionsWithOptions:playtimeOptions
                completionHandler:^(NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error setting Playtime options: %@", error);
            reject(@"playtime_error", [RNPlaytimeBasic formatErrorMessage:@"Playtime options error" withError:error], error);
            return;
        }
        
        resolve(nil);
    }];
}

RCT_EXPORT_METHOD(
    getStatus:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
     [Playtime getStatusWithCompletionHandler:^(PlaytimeStatus * _Nullable response, NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error getting status: %@", error);
            reject(@"playtime_error", [RNPlaytimeBasic formatErrorMessage:@"Error getting status" withError:error], error);
            return;
        }
        
        NSDictionary *responseDictionary = [response toJSONObject];
        if (response == nil) {
            RCTLogError(@"Error parsing status response");
            reject(@"playtime_error", @"Error parsing status response", nil);
            return;
        }
        
        NSMutableDictionary *mutableResponse = [responseDictionary mutableCopy];
        NSDictionary *details = mutableResponse[@"details"];
        if (details != nil) {
            NSMutableDictionary *mutableDetails = [details mutableCopy];
            NSArray *campaignsState = mutableDetails[@"campaignsState"];
            if (campaignsState != nil) {
                NSMutableArray *convertedStates = [NSMutableArray arrayWithCapacity:campaignsState.count];
                for (NSNumber *state in campaignsState) {
                    NSString *stateString = [RNPlaytimeBasic campaignStateToString:state];
                    if (stateString != nil) {
                        [convertedStates addObject:stateString];
                    }
                }
                mutableDetails[@"campaignsState"] = convertedStates;
            }
            mutableResponse[@"details"] = mutableDetails;
        }
        
        resolve(mutableResponse);
    }];
}

RCT_EXPORT_METHOD(
    getUserId:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    [Playtime getUserIdWithCompletionHandler:^(NSString * _Nullable userId, NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error getting user ID: %@", error);
            reject(@"playtime_error", [RNPlaytimeBasic formatErrorMessage:@"Error getting user ID" withError:error], error);
            return;
        }
        
        resolve(userId ?: [NSNull null]);
    }];
}

RCT_EXPORT_METHOD(
    teardown:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    [Playtime teardownWithCompletionHandler:^(NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error deinitializing SDK: %@", error);
            reject(@"playtime_error", [RNPlaytimeBasic formatErrorMessage:@"Error deinitializing SDK" withError:error], error);
            return;
        }
        
        resolve(nil);
    }];
}

@end
