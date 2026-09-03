// RNPlaytimeHelpers.h

#import <Foundation/Foundation.h>
#import <PlaytimeSDK/PlaytimeSDK-Swift.h>

NS_ASSUME_NONNULL_BEGIN

@interface RNPlaytimeHelpers : NSObject

/// Builds `InitOptions` from the JS options dictionary, injecting the wrapper identifier
/// that the SDK expects under the `wrapper` key and converting the user profile's
/// ISO 8601 `birthday` string into an `NSDate`.
+ (InitOptions * _Nullable)initOptionsFromDictionary:(NSDictionary *)paramsDictionary error:(NSError **)error;

/// Parses an ISO 8601 date string as sent by JavaScript. Returns nil when the value is
/// empty or cannot be parsed.
+ (NSDate * _Nullable)dateFromISOString:(NSString *)value;

@end

NS_ASSUME_NONNULL_END
