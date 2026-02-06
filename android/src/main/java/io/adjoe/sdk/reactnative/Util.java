package io.adjoe.sdk.reactnative;

import android.os.Build;
import android.text.TextUtils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.adjoe.sdk.PlaytimeStatusDetails;
import io.adjoe.sdk.PlaytimeExtensions;
import io.adjoe.sdk.PlaytimeGender;
import io.adjoe.sdk.PlaytimeOptions;
import io.adjoe.sdk.PlaytimeParams;
import io.adjoe.sdk.PlaytimeUserProfile;
import io.adjoe.sdk.studio.PlaytimeCampaign;
import io.adjoe.sdk.PlaytimeStatus;
import io.adjoe.sdk.studio.PlaytimePermissionsResponse;

public class Util {
    static PlaytimeParams constructPlaytimeParams(ReadableMap paramsMap) {
        PlaytimeParams.Builder builder = new PlaytimeParams.Builder();
        if (paramsMap != null) {
            if (paramsMap.hasKey("placement")) {
                builder.setPlacement(paramsMap.getString("placement"));
            }
            if (paramsMap.hasKey("uaNetwork")) {
                builder.setUaNetwork(paramsMap.getString("uaNetwork"));
            }
            if (paramsMap.hasKey("uaChannel")) {
                builder.setUaChannel(paramsMap.getString("uaChannel"));
            }
            if (paramsMap.hasKey("uaSubPublisherCleartext")) {
                builder.setUaSubPublisherCleartext(paramsMap.getString("uaSubPublisherCleartext"));
            }
            if (paramsMap.hasKey("uaSubPublisherEncrypted")) {
                builder.setUaSubPublisherEncrypted(paramsMap.getString("uaSubPublisherEncrypted"));
            }
            if (paramsMap.hasKey("promotionTag")) {
                builder.setPromotionTag(paramsMap.getString("promotionTag"));
            }
        }
        return builder.build();
    }

    static PlaytimeExtensions constructPlaytimeExtension(ReadableMap extensionMap) {
        PlaytimeExtensions.Builder extensions = new PlaytimeExtensions.Builder();
        if (extensionMap == null) return extensions.build();
        return extensions
                .setSubId1(extensionMap.getString("subId1"))
                .setSubId2(extensionMap.getString("subId2"))
                .setSubId3(extensionMap.getString("subId3"))
                .setSubId4(extensionMap.getString("subId4"))
                .setSubId5(extensionMap.getString("subId5"))
                .build();
    }

    static PlaytimeUserProfile constructPlaytimeUserProfile(ReadableMap userProfileMap) {
        if (userProfileMap == null) return null;
        String gender = userProfileMap.getString("gender");
        PlaytimeGender playtimeGender;
        if ("male".equalsIgnoreCase(gender)) {
            playtimeGender = PlaytimeGender.MALE;
        } else if ("female".equalsIgnoreCase(gender)) {
            playtimeGender = PlaytimeGender.FEMALE;
        } else {
            playtimeGender = PlaytimeGender.UNKNOWN;
        }

        String birthdate = userProfileMap.getString("birthday");
        Date birthday = null;
        if (birthdate != null && !TextUtils.isEmpty(birthdate)) {
            Instant dateInstant = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateInstant = Instant.parse(birthdate);
                birthday = Date.from(dateInstant);
            } else {
                SimpleDateFormat sdf =
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                try {
                    birthday = sdf.parse(birthdate);
                } catch (Exception e) {

                }
            }
        }
        return new PlaytimeUserProfile(playtimeGender, birthday);
    }

    static PlaytimeOptions constructOptionsFrom(ReadableMap optionsMap) {
        PlaytimeOptions options = new PlaytimeOptions();
        if (optionsMap != null) {
            if (optionsMap.hasKey("userId")) {
                options.setUserId(optionsMap.getString("userId"));
            }

            if (optionsMap.hasKey("params")) {
                ReadableMap paramsMap = optionsMap.getMap("params");
                PlaytimeParams params = constructPlaytimeParams(paramsMap);
                options.setParams(params);
            }
            if (optionsMap.hasKey("extensions")) {
                ReadableMap extensionMap = optionsMap.getMap("extensions");
                PlaytimeExtensions extensions = constructPlaytimeExtension(extensionMap);
                options.setExtensions(extensions);
            }
            if (optionsMap.hasKey("userProfile")) {
                ReadableMap userProfileMap = optionsMap.getMap("userProfile");
                PlaytimeUserProfile userProfile = constructPlaytimeUserProfile(userProfileMap);
                options.setUserProfile(userProfile);
            }

            if (optionsMap.hasKey("sdkHash")) {
                String sdkHash = optionsMap.getString("sdkHash");
                if (sdkHash != null) {
                    options.setSDKHash(sdkHash);
                }
            }
        }
        options.w("RN");

        return options;
    }

    static ReadableMap campaignToReadableMap(PlaytimeCampaign campaign) {
        WritableMap appMap = Arguments.createMap();

        appMap.putString(Constants.JsonKey.CAMPAIGN_UUID, campaign.getCampaignUUID());
        appMap.putString(Constants.JsonKey.APP_NAME, campaign.getAppName());
        appMap.putString(Constants.JsonKey.APP_DESCRIPTION, campaign.getAppDescription());
        appMap.putString(Constants.JsonKey.APP_ID, campaign.getAppID());
        appMap.putString(Constants.JsonKey.INSTALLED_AT, campaign.getInstalledAt());
        appMap.putString(Constants.JsonKey.UNINSTALLED_AT, campaign.getUninstalledAt());
        putIntOrNull(appMap, campaign.getRewardingExpiresAfter(), Constants.JsonKey.REWARDING_EXPIRES_AFTER);
        appMap.putString(Constants.JsonKey.REWARDING_EXPIRES_AT, campaign.getRewardingExpiresAt());
        appMap.putMap(Constants.JsonKey.EVENT_CONFIG, eventConfigToReadableMap(campaign.getEventConfig()));
        appMap.putString(Constants.JsonKey.APP_CATEGORY, campaign.getAppCategory());
        appMap.putString(Constants.JsonKey.CAMPAIGN_EXPIRES_AT, campaign.getCampaignExpiresAt());
        appMap.putString(Constants.JsonKey.CAMPAIGN_TYPE, campaign.getCampaignType());
        putIntOrNull(appMap, campaign.getFeaturedPosition(), Constants.JsonKey.FEATURED_POSITION);
        putFloatOrNull(appMap, campaign.getScore(), Constants.JsonKey.SCORE);
        appMap.putMap(Constants.JsonKey.IMAGE, mediaToReadableMap(campaign.getImage()));
        appMap.putMap(Constants.JsonKey.VIDEO, mediaToReadableMap(campaign.getVideo()));
        appMap.putString(Constants.JsonKey.ICON_IMAGE, campaign.getIconImage());
        appMap.putMap(Constants.JsonKey.PROMOTION, promotionToReadableMap(campaign.getPromotion()));
        appMap.putBoolean(Constants.JsonKey.IS_COMPLETED, campaign.isCompleted());

        return appMap;
    }

    static ReadableMap statusToReadableMap(PlaytimeStatus status) {
        WritableMap statusMap = Arguments.createMap();
        statusMap.putBoolean(Constants.JsonKey.IS_INITIALIZED, status.isInitialized());
        statusMap.putMap(Constants.JsonKey.DETAILS, statusDetailsToReadableMap(status.getDetails()));
        return statusMap;
    }

    static ReadableMap statusDetailsToReadableMap(PlaytimeStatusDetails details) {
        WritableMap detailsMap = Arguments.createMap();
        detailsMap.putBoolean(Constants.JsonKey.IS_FRAUD, details.isFraud());
        return detailsMap;
    }

    static ReadableMap mediaToReadableMap(PlaytimeCampaign.PlaytimeMedia media) {
        if (media == null) return null;

        WritableMap mediaMap = Arguments.createMap();

        mediaMap.putString(Constants.JsonKey.LANDSCAPE, media.getLandscape());
        mediaMap.putString(Constants.JsonKey.PORTRAIT, media.getPortrait());

        return mediaMap;
    }

    static ReadableMap promotionToReadableMap(PlaytimeCampaign.PlaytimePromotion promotion) {
        if (promotion == null) return null;

        WritableMap promotionMap = Arguments.createMap();

        promotionMap.putString(Constants.JsonKey.NAME, promotion.getName());
        promotionMap.putString(Constants.JsonKey.PROMOTION_DESCRIPTION, promotion.getPromotionDescription());
        putFloatOrNull(promotionMap, promotion.getBoostFactor(), Constants.JsonKey.BOOST_FACTOR);
        promotionMap.putString(Constants.JsonKey.START_TIME, promotion.getStartTime());
        promotionMap.putString(Constants.JsonKey.END_TIME, promotion.getEndTime());
        promotionMap.putString(Constants.JsonKey.TARGETING_TYPE, promotion.getTargetingType());

        return promotionMap;

    }

    static ReadableMap eventConfigToReadableMap(PlaytimeCampaign.EventConfig eventConfig) {
        if (eventConfig == null) return null;

        WritableMap eventConfigMap = Arguments.createMap();

        eventConfigMap.putArray(Constants.JsonKey.SEQUENTIAL_ACTIONS, rewardActionsToArrayMap(eventConfig.getSequentialActions()));
        eventConfigMap.putArray(Constants.JsonKey.BONUS_ACTIONS, rewardActionsToArrayMap(eventConfig.getBonusActions()));
        eventConfigMap.putArray(Constants.JsonKey.TIME_BASED_ACTIONS, rewardActionsToArrayMap(eventConfig.getTimeBasedActions()));
        putIntOrNull(eventConfigMap, eventConfig.getTotalCoinsCollected(), Constants.JsonKey.TOTAL_COINS_COLLECTED);
        putIntOrNull(eventConfigMap, eventConfig.getTotalCoinsPossible(), Constants.JsonKey.TOTAL_COINS_POSSIBLE);
        eventConfigMap.putMap(Constants.JsonKey.CASH_BACK_REWARD, cashBackRewardConfigToReadableMap(eventConfig.getCashbackReward()));
        eventConfigMap.putArray(Constants.JsonKey.MULTIPLIERS_ACTIONS, multipliersActionsToArrayMap(eventConfig.getMultipliersActions()));

        return eventConfigMap;
    }

    static ReadableArray rewardActionsToArrayMap(List<PlaytimeCampaign.PlaytimeRewardAction> actions) {
        WritableArray actionsArray = Arguments.createArray();

        if (actions == null) return actionsArray;

        for (int i = 0; i < actions.size(); i++) {
            WritableMap actionMap = Arguments.createMap();

            PlaytimeCampaign.PlaytimeRewardAction action = actions.get(i);

            actionMap.putString(Constants.JsonKey.NAME, action.getName());
            actionMap.putString(Constants.JsonKey.TASK_DESCRIPTION, action.getTaskDescription());
            actionMap.putString(Constants.JsonKey.TASK_TYPE, action.getTaskType());
            putIntOrNull(actionMap, action.getPlayDuration(), Constants.JsonKey.PLAY_DURATION);
            actionMap.putInt(Constants.JsonKey.AMOUNT, action.getAmount());
            actionMap.putString(Constants.JsonKey.REWARDED_AT, action.getRewardedAt());
            putIntOrNull(actionMap, action.getLevel(), Constants.JsonKey.LEVEL);
            putIntOrNull(actionMap, action.getRewardsCount(), Constants.JsonKey.REWARDS_COUNT);
            putIntOrNull(actionMap, action.getCompletedRewards(), Constants.JsonKey.COMPLETED_REWARDS);
            putIntOrNull(actionMap, action.getTimedCoinsDuration(), Constants.JsonKey.TIMED_COINS_DURATION);
            putIntOrNull(actionMap, action.getTimedCoins(), Constants.JsonKey.TIMED_COINS);
            putIntOrNull(actionMap, action.getOriginalCoins(), Constants.JsonKey.ORIGINAL_COINS);
            putBooleanOrNull(actionMap, action.isTimed(), Constants.JsonKey.IS_TIMED);
            putBooleanOrNull(actionMap, action.isRewardedForPromotion(), Constants.JsonKey.IS_REWARDED_FOR_PROMOTION);
            actionMap.putString(Constants.JsonKey.BOOSTER_EXPIRES_AT, action.getBoosterExpiresAt());

            actionsArray.pushMap(actionMap);
        }

        return actionsArray;
    }

    static ReadableArray multipliersActionsToArrayMap(List<PlaytimeCampaign.PlaytimeRewardActionMultiplier> actions) {
        WritableArray actionsArray = Arguments.createArray();

        if (actions == null) return actionsArray;

        for (int i = 0; i < actions.size(); i++) {
            WritableMap actionMap = Arguments.createMap();

            PlaytimeCampaign.PlaytimeRewardActionMultiplier action = actions.get(i);

            actionMap.putString(Constants.JsonKey.EVENT_NAME, action.getEventName());
            actionMap.putString(Constants.JsonKey.EVENT_DESCRIPTION, action.getEventDescription());
            putIntOrNull(actionMap, action.getMultiplierFactorPercentage(), Constants.JsonKey.MULTIPLIER_FACTOR_PERCENTAGE);
            putIntOrNull(actionMap, action.getMultiplierLevels(), Constants.JsonKey.MULTIPLIER_LEVELS);
            actionMap.putString(Constants.JsonKey.STATUS, action.getStatus());
            putIntOrNull(actionMap, action.getUsedLevels(), Constants.JsonKey.USED_LEVELS);

            actionsArray.pushMap(actionMap);
        }

        return actionsArray;
    }

    static ReadableMap cashBackRewardConfigToReadableMap(PlaytimeCampaign.PlaytimeCashbackConfig cashbackRewardConfig) {
        if (cashbackRewardConfig == null) return null;

        WritableMap cashbackRewardMap = Arguments.createMap();

        putFloatOrNull(cashbackRewardMap, cashbackRewardConfig.getExchangeRate(), Constants.JsonKey.EXCHANGE_RATE);
        cashbackRewardMap.putString(Constants.JsonKey.CASHBACK_DESCRIPTION, cashbackRewardConfig.getCashbackDescription());
        putFloatOrNull(cashbackRewardMap, cashbackRewardConfig.getMaxLimitPerCampaignUSD(), Constants.JsonKey.MAX_LIMIT_PER_CAMPAIGN_USD);
        putFloatOrNull(cashbackRewardMap, cashbackRewardConfig.getMaxLimitPerCampaignCoins(), Constants.JsonKey.MAX_LIMIT_PER_CAMPAIGN_COINS);
        cashbackRewardMap.putMap(Constants.JsonKey.COMPLETED_REWARDS, cashBackRewardToReadableMap(cashbackRewardConfig.getCompletedRewards()));
        cashbackRewardMap.putMap(Constants.JsonKey.PENDING_REWARDS, cashBackRewardToReadableMap(cashbackRewardConfig.getPendingRewards()));

        return cashbackRewardMap;
    }

    static ReadableMap cashBackRewardToReadableMap(PlaytimeCampaign.PlaytimeCashbackReward rewards) {
        if (rewards == null) return null;

        WritableMap cashbackRewardMap = Arguments.createMap();

        putIntOrNull(cashbackRewardMap, rewards.getTotalCoins(), Constants.JsonKey.TOTAL_COINS);
        cashbackRewardMap.putArray(Constants.JsonKey.EVENTS,cashbackRewardEventToArrayMap(rewards.getEvents()) );

        return cashbackRewardMap;
    }

    static ReadableArray cashbackRewardEventToArrayMap(List<PlaytimeCampaign.PlaytimeCashbackRewardEvent> events) {
        WritableArray eventsArray = Arguments.createArray();

        if (events == null) return eventsArray;

        for (int i = 0; i < events.size(); i++) {
            WritableMap eventMap = Arguments.createMap();

            PlaytimeCampaign.PlaytimeCashbackRewardEvent event = events.get(i);
            putIntOrNull(eventMap, event.getCoins(), Constants.JsonKey.COINS);
            eventMap.putString(Constants.JsonKey.PROCESS_AT, event.getProcessAt());
            eventMap.putString(Constants.JsonKey.RECEIVED_AT, event.getReceivedAt());

            eventsArray.pushMap(eventMap);
        }

        return eventsArray;
    }

    static ReadableMap permissionToReadableMap(PlaytimePermissionsResponse response) {
        WritableMap permissions = Arguments.createMap();

        WritableMap result = Arguments.createMap();
        result.putBoolean("isUsagePermissionAccepted", response.getPermissions().isUsagePermissionAccepted());
        result.putBoolean("isTOSAccepted",  response.getPermissions().isTOSAccepted());

        permissions.putMap("permissions", result);

        return permissions;
    }

    private static void putIntOrNull(WritableMap map, Integer value, String key) {
        if (value == null) {
            map.putNull(key);
        } else {
            map.putInt(key, value);
        }
    }

    private static void putFloatOrNull(WritableMap map, Float value, String key) {
        if (value == null) {
            map.putNull(key);
        } else {
            map.putDouble(key, value);
        }
    }

    private static void putBooleanOrNull(WritableMap map, Boolean value, String key) {
        if (value == null) {
            map.putNull(key);
        } else {
            map.putBoolean(key, value);
        }
    }

    static List<String> extractTokens(ReadableMap map) {
        ArrayList<String> tokens = new ArrayList<>();

        try {
            ReadableArray tokensArray = map.getArray(Constants.JsonKey.TOKENS);
            for (int i = 0; i < tokensArray.size(); i++) {
                String token = tokensArray.getString(i);
                tokens.add(token);
            }
        } catch (Exception ignore) {}

        return tokens;
    }
}

