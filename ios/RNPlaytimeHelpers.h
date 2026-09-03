// RNPlaytimeHelpers.h

#import <Foundation/Foundation.h>
#import <PlaytimeMonetize/PlaytimeMonetize-Swift.h>

NS_ASSUME_NONNULL_BEGIN

@interface RNPlaytimeHelpers : NSObject

+ (PlaytimeOptions * _Nullable)playtimeOptionsFromDictionary:(NSDictionary *)paramsDictionary error:(NSError **)error;

@end

NS_ASSUME_NONNULL_END
