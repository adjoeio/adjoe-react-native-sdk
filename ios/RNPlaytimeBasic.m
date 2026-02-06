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
        
        resolve(responseDictionary);
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

@end
