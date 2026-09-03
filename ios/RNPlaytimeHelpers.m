// RNPlaytimeHelpers.m

#import "RNPlaytimeHelpers.h"

@implementation RNPlaytimeHelpers

/// Parses an ISO 8601 date string coming from JavaScript.
///
/// JS has no date type on the bridge, so `PlaytimeUserProfile.birthday` is sent as a
/// string (typically from `Date.prototype.toISOString()`). The SDK's
/// `init(JSONObject:)` expects a real `NSDate` and casts without parsing, so the
/// conversion has to happen here.
///
/// Both the fractional-seconds form (`2002-12-02T00:00:00.000Z`) and the plain form
/// (`2002-12-02T00:00:00Z`) are accepted, as is a bare calendar date
/// (`2002-12-02`), which is the natural way to express a birthday.
+ (NSDate * _Nullable)dateFromISOString:(NSString *)value
{
    if (value.length == 0) {
        return nil;
    }

    static NSISO8601DateFormatter *withFractionalSeconds = nil;
    static NSISO8601DateFormatter *withoutFractionalSeconds = nil;
    static NSISO8601DateFormatter *dateOnly = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        withFractionalSeconds = [[NSISO8601DateFormatter alloc] init];
        withFractionalSeconds.formatOptions = NSISO8601DateFormatWithInternetDateTime
            | NSISO8601DateFormatWithFractionalSeconds;

        withoutFractionalSeconds = [[NSISO8601DateFormatter alloc] init];
        withoutFractionalSeconds.formatOptions = NSISO8601DateFormatWithInternetDateTime;

        dateOnly = [[NSISO8601DateFormatter alloc] init];
        dateOnly.formatOptions = NSISO8601DateFormatWithFullDate;
    });

    for (NSISO8601DateFormatter *formatter in @[ withFractionalSeconds,
                                                 withoutFractionalSeconds,
                                                 dateOnly ]) {
        NSDate *date = [formatter dateFromString:value];
        if (date != nil) {
            return date;
        }
    }

    return nil;
}

/// Returns a copy of the user profile with `birthday` converted from an ISO 8601 string
/// to an `NSDate`. An unparseable or empty value is dropped rather than forwarded, so the
/// SDK sees "no birthday" instead of throwing a type mismatch.
+ (NSDictionary * _Nullable)normalizedUserProfile:(id)userProfile
{
    if (![userProfile isKindOfClass:[NSDictionary class]]) {
        return nil;
    }

    NSMutableDictionary *profile = [(NSDictionary *)userProfile mutableCopy];
    id birthday = profile[@"birthday"];

    if ([birthday isKindOfClass:[NSString class]]) {
        NSDate *parsed = [self dateFromISOString:(NSString *)birthday];
        if (parsed != nil) {
            profile[@"birthday"] = parsed;
        } else {
            [profile removeObjectForKey:@"birthday"];
        }
    }

    return profile;
}

+ (InitOptions *)initOptionsFromDictionary:(NSDictionary *)paramsDictionary error:(NSError **)error
{
    NSMutableDictionary *mutableParams = [paramsDictionary mutableCopy];
    mutableParams[@"wrapper"] = @"rn";

    NSDictionary *userProfile = [self normalizedUserProfile:mutableParams[@"userProfile"]];
    if (userProfile != nil) {
        mutableParams[@"userProfile"] = userProfile;
    }

    InitOptions *initOptions = [[InitOptions alloc] initWithJSONObject:mutableParams error:error];

    if (initOptions == nil || *error != nil) {
        return nil;
    }

    return initOptions;
}

@end
