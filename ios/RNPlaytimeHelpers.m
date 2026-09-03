// RNPlaytimeHelpers.m

#import "RNPlaytimeHelpers.h"

@implementation RNPlaytimeHelpers

+ (PlaytimeOptions *)playtimeOptionsFromDictionary:(NSDictionary *)paramsDictionary error:(NSError **)error
{
    NSMutableDictionary *mutableParams = [paramsDictionary mutableCopy];
    mutableParams[@"wrapper"] = @"rn";
    
    PlaytimeOptions *playtimeOptions = [[PlaytimeOptions alloc] initWithJSONObject:mutableParams error:error];
        
    if (playtimeOptions == nil || *error != nil) {
        return nil;
    }
    
    return playtimeOptions;
}

@end
