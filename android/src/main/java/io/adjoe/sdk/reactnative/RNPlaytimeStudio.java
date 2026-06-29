package io.adjoe.sdk.reactnative;

import static io.adjoe.sdk.reactnative.StudioUtil.campaignToReadableMap;
import static io.adjoe.sdk.reactnative.RNPlaytimeSdkModule.BasicUtil.constructOptionsFrom;
import static io.adjoe.sdk.reactnative.Util.extractTokens;
import static io.adjoe.sdk.reactnative.RNPlaytimeSdkModule.BasicUtil.permissionToReadableMap;
import static io.adjoe.sdk.reactnative.Util.putBooleanOrNull;
import static io.adjoe.sdk.reactnative.Util.putFloatOrNull;
import static io.adjoe.sdk.reactnative.Util.putIntOrNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.adjoe.sdk.Playtime;
import io.adjoe.sdk.PlaytimeOptions;
import io.adjoe.sdk.studio.PlaytimeCampaign;
import io.adjoe.sdk.studio.PlaytimeCampaignsListener;
import io.adjoe.sdk.studio.PlaytimeDeeplinkListener;
import io.adjoe.sdk.studio.PlaytimeCampaignsResponse;
import io.adjoe.sdk.studio.PlaytimeOpenInstalledCampaignListener;
import io.adjoe.sdk.studio.PlaytimeOpenStoreListener;
import io.adjoe.sdk.studio.PlaytimeEngagementType;
import io.adjoe.sdk.studio.PlaytimeExecuteEngagementListener;
import io.adjoe.sdk.studio.PlaytimePermissionsListener;
import io.adjoe.sdk.studio.PlaytimePermissionsResponse;
import io.adjoe.sdk.connect.RewardsConnectRegistrationListener;
import io.adjoe.sdk.connect.RewardsConnectRegistrationFailureException;
import io.adjoe.sdk.connect.RewardsConnectResetListener;
import io.adjoe.sdk.connect.RewardsConnectResetFailureException;
import io.adjoe.sdk.studio.PlaytimeResponseError;
import io.adjoe.sdk.studio.PlaytimeStudio;

public class RNPlaytimeStudio extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private final Map<String, PlaytimeCampaign> cache = new HashMap<>();


    public RNPlaytimeStudio(ReactApplicationContext context) {
        super(context);
        this.reactContext = context;
    }

    @Override
    public Map<String, Object> getConstants() {
        Map<String, Object> constants = new HashMap<>();
        constants.put("VERSION", Playtime.getVersion());
        return constants;
    }

    @Override
    public String getName() {
        return "PlaytimeStudio";
    }

    @ReactMethod
    public void getCampaigns(ReadableMap optionsMap, Promise promise) {
        PlaytimeOptions options = constructOptionsFrom(optionsMap);
        List<String> tokens = extractTokens(optionsMap);

        if (tokens.isEmpty()) {
            getCampaigns(options, promise);
        } else {
            getCampaigns(options, tokens, promise);
        }
    }

    @ReactMethod
    public void getInstalledCampaigns(ReadableMap optionsMap, Promise promise) {
        PlaytimeOptions options = constructOptionsFrom(optionsMap);
        PlaytimeStudio.getInstalledCampaigns(reactContext, options, createCampaignsListener(promise));
    }

    @ReactMethod
    public void openInStore(ReadableMap campaignMap, Promise promise) {
        String campaignUUID = campaignMap.getString(StudioUtil.JsonKey.CAMPAIGN_UUID);

        PlaytimeCampaign campaign = cache.get(campaignUUID);

        if (campaignUUID == null || campaign == null) {
            promise.reject("Open Campaign In Store Error", "Campaign not found");
            return;
        }

        PlaytimeStudio.openInStore(reactContext, campaign, new PlaytimeOpenStoreListener() {
            @Override
            public void onFinished() {
                promise.resolve(null);
            }

            @Override
            public void onError(@Nullable PlaytimeResponseError playtimeResponseError) {
                if (playtimeResponseError == null) {
                    promise.reject("Open Campaign In Store Error", "Unknown error occurred");
                    return;
                }

                promise.reject("Open Campaign In Store Error", playtimeResponseError.getError().getMessage());
            }

            @Override
            public void onAlreadyClicking() {
                promise.reject("Open Campaign In Store Error", "Request is already in progress");
            }
        });
    }

    @ReactMethod
    public void openInstalledCampaign(ReadableMap campaignMap, Promise promise) {
        String campaignUUID = campaignMap.getString(StudioUtil.JsonKey.CAMPAIGN_UUID);

        PlaytimeCampaign campaign = cache.get(campaignUUID);

        if (campaignUUID == null || campaign == null) {
            promise.reject("Open Installed Campaign Error", "Campaign not found");
            return;
        }

        PlaytimeStudio.openInstalledCampaign(reactContext, campaign, new PlaytimeOpenInstalledCampaignListener() {
            @Override
            public void onOpened() {
                promise.resolve(null);
            }

            @Override
            public void onError(@Nullable PlaytimeResponseError playtimeResponseError) {
                if (playtimeResponseError == null) {
                    promise.reject("Open Installed Campaign Error", "Unknown error occurred");
                    return;
                }
                promise.reject("Open Installed Campaign Error", playtimeResponseError.getError().getMessage());
            }
        });
    }

    @ReactMethod
    public void getPermissions(Promise promise) {
        PlaytimeStudio.getPermissions(reactContext, new PlaytimePermissionsListener() {
            @Override
            public void onReceived(@NonNull PlaytimePermissionsResponse playtimePermissionsResponse) {
                promise.resolve(permissionToReadableMap(playtimePermissionsResponse));
            }

            @Override
            public void onError(@NonNull PlaytimeResponseError playtimeResponseError) {
                promise.reject("Get Permissions Error", playtimeResponseError.getError().getMessage());
            }
        });
    }

    @ReactMethod
    public void showPermissionsPrompt(Promise promise) {
        try {
            if (getCurrentActivity() != null) {
                PlaytimeStudio.showPermissionsPrompt(getCurrentActivity(), new PlaytimePermissionsListener() {
                    @Override
                    public void onReceived(@NonNull PlaytimePermissionsResponse playtimePermissionsResponse) {
                        promise.resolve(permissionToReadableMap(playtimePermissionsResponse));
                    }

                    @Override
                    public void onError(@NonNull PlaytimeResponseError playtimeResponseError) {
                        promise.reject("Get Permissions Error", playtimeResponseError.getError().getMessage());
                    }
                });
            } else  {
                promise.reject("Show Permission Prompt Error", "Activity is null");
            }
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void showAppDetails(ReadableMap campaignMap, Promise promise) {
        String campaignUUID = campaignMap.getString(StudioUtil.JsonKey.CAMPAIGN_UUID);
        PlaytimeCampaign campaign = cache.get(campaignUUID);

        if (getCurrentActivity() != null) {
            PlaytimeStudio.showAppDetails(
                getCurrentActivity(), 
                campaign,
                createShowDetailsListener(promise)
            );
        } else  {
            promise.reject("Show App Details Error", "Activity is null");
        }
    }

    @ReactMethod
    public void showAppDetailsWithToken(String token, String appId, Promise promise) {
        if (getCurrentActivity() != null) {
            PlaytimeStudio.showAppDetails(
                getCurrentActivity(), 
                token,
                appId,
                createShowDetailsListener(promise)
            );
        } else  {
            promise.reject("Show App Details Error", "Activity is null");
        }
    }

    @ReactMethod
    public void showInstalledApps(Promise promise) {
        if (getCurrentActivity() != null) {
            PlaytimeStudio.showInstalledApps(
                getCurrentActivity(),
                createShowDetailsListener(promise)
            );
        } else  {
            promise.reject("Show Installed Apps Error", "Activity is null");
        }
    }

    @ReactMethod
    public void registerRewardsConnect(final Promise promise) {
        if (getCurrentActivity() != null) {
            PlaytimeStudio.registerRewardsConnect(
                getCurrentActivity(),
                new RewardsConnectRegistrationListener() {
                    @Override
                    public void onSuccess() {
                        promise.resolve(null);
                    }

                    @Override
                    public void onFailure(@NonNull RewardsConnectRegistrationFailureException registrationError) {
                        promise.reject("Register Error", registrationError.getMessage());
                    }
                }
            );
        } else {
            promise.reject("Register Error", "Activity is null");
        }
    }

    @ReactMethod
    public void resetRewardsConnect(final Promise promise) {
        if (getCurrentActivity() != null) {
            PlaytimeStudio.resetRewardsConnect(
                getCurrentActivity(),
                new RewardsConnectResetListener() {
                    @Override
                    public void onSuccess() {
                        promise.resolve(null);
                    }

                    @Override
                    public void onFailure(@NonNull RewardsConnectResetFailureException registrationError) {
                        promise.reject("Reset Error", registrationError.getMessage());
                    }
                }
            );
        } else {
            promise.reject("Reset Error", "Activity is null");
        }
    }

    @ReactMethod
    public void openChatbot(ReadableMap campaignMap, Promise promise) {
        try {
            String campaignUUID = campaignMap == null ? null : campaignMap.getString(StudioUtil.JsonKey.CAMPAIGN_UUID);
            PlaytimeCampaign campaign = cache.get(campaignUUID);

            if (getCurrentActivity() != null) {
                PlaytimeStudio.openChatbot(
                    getCurrentActivity(), 
                    campaign,
                    createOpenChatbotListener(promise)
                );
            } else  {
                promise.reject("Open Chatbot Error", "Activity is null");
            }
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void executeEngagement(ReadableMap campaignMap, String engagementType, Promise promise) {
        String campaignUUID = campaignMap.getString(StudioUtil.JsonKey.CAMPAIGN_UUID);

        PlaytimeCampaign campaign = cache.get(campaignUUID);
        PlaytimeEngagementType playtimeEngagementType = PlaytimeEngagementType.DEFAULT;

        if (engagementType.equals("engaged")) {
            playtimeEngagementType = PlaytimeEngagementType.ENGAGED;
        }

        if (campaignUUID == null || campaign == null) {
            promise.reject("Execute Engagement Error", "Campaign not found");
            return;
        }

        PlaytimeStudio.executeEngagement(reactContext, campaign, playtimeEngagementType, new PlaytimeExecuteEngagementListener() {
            @Override
            public void onFinished() {
                promise.resolve(null);
            }

            @Override
            public void onError(@Nullable PlaytimeResponseError playtimeResponseError) {
                if (playtimeResponseError == null) {
                    promise.reject("Execute Engagement Error", "Unknown error occurred");
                    return;
                }

                promise.reject("Execute Engagement Error", playtimeResponseError.getError().getMessage());
            }

            @Override
            public void onAlreadyEngaging() {
                promise.reject("Execute Engagement Error", "Request is already in progress");
            }
        });
    }

    @ReactMethod
    public void executeEngagementWithToken(String appID, String token, Promise promise) {
        PlaytimeStudio.executeEngagement(reactContext, appID, token, new PlaytimeExecuteEngagementListener() {
            @Override
            public void onFinished() {
                promise.resolve(null);
            }

            @Override
            public void onError(@Nullable PlaytimeResponseError playtimeResponseError) {
                if (playtimeResponseError == null) {
                    promise.reject("Execute Engagement Error", "Unknown error occurred");
                    return;
                }

                promise.reject("Execute Engagement Error", playtimeResponseError.getError().getMessage());
            }

            @Override
            public void onAlreadyEngaging() {
                promise.reject("Execute Engagement Error", "Request is already in progress");
            }
        });
    }

    private void getCampaigns(PlaytimeOptions options, Promise promise) {
        PlaytimeStudio.getCampaigns(reactContext, options, createCampaignsListener(promise));
    }

    private void getCampaigns(PlaytimeOptions options, List<String> tokens, Promise promise) {
        PlaytimeStudio.getCampaigns(reactContext, tokens, options, createCampaignsListener(promise));
    }

    private PlaytimeCampaignsListener createCampaignsListener(Promise promise) {
        return new PlaytimeCampaignsListener() {
            @Override
            public void onReceived(@NonNull PlaytimeCampaignsResponse playtimeCampaignsResponse) {
                WritableArray campaigns = Arguments.createArray();
                for (PlaytimeCampaign campaign : playtimeCampaignsResponse.getCampaigns()) {
                    cache.put(campaign.getCampaignUUID(), campaign);
                    campaigns.pushMap(campaignToReadableMap(campaign));
                }

                WritableMap result = Arguments.createMap();
                result.putArray(StudioUtil.JsonKey.CAMPAIGNS ,campaigns);
                promise.resolve(result);
            }

            @Override
            public void onError(@NonNull PlaytimeResponseError playtimeResponseError) {
                promise.reject("Get Campaigns Error", playtimeResponseError.getError().getMessage());
            }
        };
    }


    private PlaytimeDeeplinkListener createShowDetailsListener(Promise promise) {
       return new PlaytimeDeeplinkListener() {
            @Override
            public void onOpened() {
                promise.resolve(null);
            }

            @Override
            public void onError(PlaytimeResponseError playtimeResponseError) {
                if (playtimeResponseError == null) {
                    promise.reject("Deeplinking Error");
                    return;
                }
                promise.reject("Deeplinking Error", playtimeResponseError.getError().getMessage());
            }
        }; 
    }

    private PlaytimeDeeplinkListener createOpenChatbotListener(Promise promise) {
       return new PlaytimeDeeplinkListener() {
            @Override
            public void onOpened() {
                promise.resolve(null);
            }

            @Override
            public void onError(PlaytimeResponseError playtimeResponseError) {
                if (playtimeResponseError == null) {
                    promise.reject("Open Chatbot Error");
                    return;
                }
                promise.reject("Open Chatbot Error", playtimeResponseError.getError().getMessage());
            }
        }; 
    }
}

class StudioUtil {

    static String campaignStatusToString(PlaytimeCampaign.PlaytimeCampaignStatus status) {
        if (status == null) return null;
        switch (status) {
            case AVAIlABLE:
                return "AVAILABLE";
            case PENDING:
                return "PENDING";
            case INSTALLED:
                return "INSTALLED";
            case FAILED:
                return "FAILED";
            default:
                return null;
        }
    }

    // Studio Utils
    static ReadableMap campaignToReadableMap(PlaytimeCampaign campaign) {
        WritableMap appMap = Arguments.createMap();

        appMap.putString(JsonKey.CAMPAIGN_UUID, campaign.getCampaignUUID());
        appMap.putString(JsonKey.APP_NAME, campaign.getAppName());
        appMap.putString(JsonKey.APP_DESCRIPTION, campaign.getAppDescription());
        appMap.putString(JsonKey.APP_ID, campaign.getAppID());
        appMap.putString(JsonKey.INSTALLED_AT, campaign.getInstalledAt());
        appMap.putString(JsonKey.UNINSTALLED_AT, campaign.getUninstalledAt());
        putIntOrNull(appMap, campaign.getRewardingExpiresAfter(), JsonKey.REWARDING_EXPIRES_AFTER);
        appMap.putString(JsonKey.REWARDING_EXPIRES_AT, campaign.getRewardingExpiresAt());
        appMap.putMap(JsonKey.EVENT_CONFIG, eventConfigToReadableMap(campaign.getEventConfig()));
        appMap.putString(JsonKey.APP_CATEGORY, campaign.getAppCategory());
        appMap.putString(JsonKey.CAMPAIGN_EXPIRES_AT, campaign.getCampaignExpiresAt());
        appMap.putString(JsonKey.CAMPAIGN_TYPE, campaign.getCampaignType());
        putIntOrNull(appMap, campaign.getFeaturedPosition(), JsonKey.FEATURED_POSITION);
        putFloatOrNull(appMap, campaign.getScore(), JsonKey.SCORE);
        appMap.putMap(JsonKey.IMAGE, mediaToReadableMap(campaign.getImage()));
        appMap.putMap(JsonKey.VIDEO, mediaToReadableMap(campaign.getVideo()));
        appMap.putString(JsonKey.ICON_IMAGE, campaign.getIconImage());
        appMap.putMap(JsonKey.PROMOTION, promotionToReadableMap(campaign.getPromotion()));
        appMap.putBoolean(JsonKey.IS_COMPLETED, campaign.isCompleted());
        appMap.putString(JsonKey.STATUS, campaignStatusToString(campaign.getStatus()));
        appMap.putBoolean(JsonKey.IS_CPA, campaign.isCpa());
        appMap.putDouble(JsonKey.CPA, campaign.getCpa());

        return appMap;
    }

    static ReadableMap mediaToReadableMap(PlaytimeCampaign.PlaytimeMedia media) {
        if (media == null) return null;

        WritableMap mediaMap = Arguments.createMap();

        mediaMap.putString(JsonKey.LANDSCAPE, media.getLandscape());
        mediaMap.putString(JsonKey.PORTRAIT, media.getPortrait());

        return mediaMap;
    }

    static ReadableMap promotionToReadableMap(PlaytimeCampaign.PlaytimePromotion promotion) {
        if (promotion == null) return null;

        WritableMap promotionMap = Arguments.createMap();

        promotionMap.putString(JsonKey.NAME, promotion.getName());
        promotionMap.putString(JsonKey.PROMOTION_DESCRIPTION, promotion.getPromotionDescription());
        putFloatOrNull(promotionMap, promotion.getBoostFactor(), JsonKey.BOOST_FACTOR);
        promotionMap.putString(JsonKey.START_TIME, promotion.getStartTime());
        promotionMap.putString(JsonKey.END_TIME, promotion.getEndTime());
        promotionMap.putString(JsonKey.TARGETING_TYPE, promotion.getTargetingType());

        return promotionMap;

    }

    static ReadableMap eventConfigToReadableMap(PlaytimeCampaign.EventConfig eventConfig) {
        if (eventConfig == null) return null;

        WritableMap eventConfigMap = Arguments.createMap();

        eventConfigMap.putArray(JsonKey.SEQUENTIAL_ACTIONS, rewardActionsToArrayMap(eventConfig.getSequentialActions()));
        eventConfigMap.putArray(JsonKey.BONUS_ACTIONS, rewardActionsToArrayMap(eventConfig.getBonusActions()));
        eventConfigMap.putArray(JsonKey.TIME_BASED_ACTIONS, rewardActionsToArrayMap(eventConfig.getTimeBasedActions()));
        eventConfigMap.putArray(JsonKey.CPA_ACTIONS, rewardActionsToArrayMap(eventConfig.getCpaActions()));
        putIntOrNull(eventConfigMap, eventConfig.getTotalCoinsCollected(), JsonKey.TOTAL_COINS_COLLECTED);
        putIntOrNull(eventConfigMap, eventConfig.getTotalCoinsPossible(), JsonKey.TOTAL_COINS_POSSIBLE);
        putIntOrNull(eventConfigMap, eventConfig.getSecondsToNextLevel(), JsonKey.SECONDS_TO_NEXT_LEVEL);
        putIntOrNull(eventConfigMap, eventConfig.getTotalOriginalCoinsPossible(), JsonKey.TOTAL_ORIGINAL_COINS_POSSIBLE);
        putIntOrNull(eventConfigMap, eventConfig.getTotalSequentialCoins(), JsonKey.TOTAL_SEQUENTIAL_COINS);
        putIntOrNull(eventConfigMap, eventConfig.getTotalOriginalSequentialCoins(), JsonKey.TOTAL_ORIGINAL_SEQUENTIAL_COINS);
        putIntOrNull(eventConfigMap, eventConfig.getTotalBonusCoins(), JsonKey.TOTAL_BONUS_COINS);
        putIntOrNull(eventConfigMap, eventConfig.getTotalOriginalBonusCoins(), JsonKey.TOTAL_ORIGINAL_BONUS_COINS);
        eventConfigMap.putMap(JsonKey.CASH_BACK_REWARD, cashBackRewardConfigToReadableMap(eventConfig.getCashbackReward()));
        eventConfigMap.putArray(JsonKey.MULTIPLIERS_ACTIONS, multipliersActionsToArrayMap(eventConfig.getMultipliersActions()));

        return eventConfigMap;
    }

    static ReadableArray rewardActionsToArrayMap(List<PlaytimeCampaign.PlaytimeRewardAction> actions) {
        WritableArray actionsArray = Arguments.createArray();

        if (actions == null) return actionsArray;

        for (int i = 0; i < actions.size(); i++) {
            WritableMap actionMap = Arguments.createMap();

            PlaytimeCampaign.PlaytimeRewardAction action = actions.get(i);

            actionMap.putString(JsonKey.NAME, action.getName());
            actionMap.putString(JsonKey.TASK_DESCRIPTION, action.getTaskDescription());
            actionMap.putString(JsonKey.TASK_TYPE, action.getTaskType());
            putIntOrNull(actionMap, action.getPlayDuration(), JsonKey.PLAY_DURATION);
            actionMap.putInt(JsonKey.AMOUNT, action.getAmount());
            actionMap.putString(JsonKey.REWARDED_AT, action.getRewardedAt());
            putIntOrNull(actionMap, action.getLevel(), JsonKey.LEVEL);
            putIntOrNull(actionMap, action.getRewardsCount(), JsonKey.REWARDS_COUNT);
            putIntOrNull(actionMap, action.getCompletedRewards(), JsonKey.COMPLETED_REWARDS);
            putIntOrNull(actionMap, action.getTimedCoinsDuration(), JsonKey.TIMED_COINS_DURATION);
            putIntOrNull(actionMap, action.getTimedCoins(), JsonKey.TIMED_COINS);
            putIntOrNull(actionMap, action.getOriginalCoins(), JsonKey.ORIGINAL_COINS);
            putBooleanOrNull(actionMap, action.isTimed(), JsonKey.IS_TIMED);
            putBooleanOrNull(actionMap, action.isRewardedForPromotion(), JsonKey.IS_REWARDED_FOR_PROMOTION);
            actionMap.putString(JsonKey.BOOSTER_EXPIRES_AT, action.getBoosterExpiresAt());

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

            actionMap.putString(JsonKey.EVENT_NAME, action.getEventName());
            actionMap.putString(JsonKey.EVENT_DESCRIPTION, action.getEventDescription());
            putIntOrNull(actionMap, action.getMultiplierFactorPercentage(), JsonKey.MULTIPLIER_FACTOR_PERCENTAGE);
            putIntOrNull(actionMap, action.getMultiplierLevels(), JsonKey.MULTIPLIER_LEVELS);
            actionMap.putString(JsonKey.STATUS, action.getStatus());
            putIntOrNull(actionMap, action.getUsedLevels(), JsonKey.USED_LEVELS);

            actionsArray.pushMap(actionMap);
        }

        return actionsArray;
    }

    static ReadableMap cashBackRewardConfigToReadableMap(PlaytimeCampaign.PlaytimeCashbackConfig cashbackRewardConfig) {
        if (cashbackRewardConfig == null) return null;

        WritableMap cashbackRewardMap = Arguments.createMap();

        putFloatOrNull(cashbackRewardMap, cashbackRewardConfig.getExchangeRate(), JsonKey.EXCHANGE_RATE);
        cashbackRewardMap.putString(JsonKey.CASHBACK_DESCRIPTION, cashbackRewardConfig.getCashbackDescription());
        putFloatOrNull(cashbackRewardMap, cashbackRewardConfig.getMaxLimitPerCampaignUSD(), JsonKey.MAX_LIMIT_PER_CAMPAIGN_USD);
        putFloatOrNull(cashbackRewardMap, cashbackRewardConfig.getMaxLimitPerCampaignCoins(), JsonKey.MAX_LIMIT_PER_CAMPAIGN_COINS);
        cashbackRewardMap.putMap(JsonKey.COMPLETED_REWARDS, cashBackRewardToReadableMap(cashbackRewardConfig.getCompletedRewards()));
        cashbackRewardMap.putMap(JsonKey.PENDING_REWARDS, cashBackRewardToReadableMap(cashbackRewardConfig.getPendingRewards()));

        return cashbackRewardMap;
    }

    static ReadableMap cashBackRewardToReadableMap(PlaytimeCampaign.PlaytimeCashbackReward rewards) {
        if (rewards == null) return null;

        WritableMap cashbackRewardMap = Arguments.createMap();

        putIntOrNull(cashbackRewardMap, rewards.getTotalCoins(), JsonKey.TOTAL_COINS);
        cashbackRewardMap.putArray(JsonKey.EVENTS, cashbackRewardEventToArrayMap(rewards.getEvents()));

        return cashbackRewardMap;
    }

    static ReadableArray cashbackRewardEventToArrayMap(List<PlaytimeCampaign.PlaytimeCashbackRewardEvent> events) {
        WritableArray eventsArray = Arguments.createArray();

        if (events == null) return eventsArray;

        for (int i = 0; i < events.size(); i++) {
            WritableMap eventMap = Arguments.createMap();

            PlaytimeCampaign.PlaytimeCashbackRewardEvent event = events.get(i);
            putIntOrNull(eventMap, event.getCoins(), JsonKey.COINS);
            eventMap.putString(JsonKey.PROCESS_AT, event.getProcessAt());
            eventMap.putString(JsonKey.RECEIVED_AT, event.getReceivedAt());

            eventsArray.pushMap(eventMap);
        }

        return eventsArray;
    }

    static class JsonKey {
        static final String CAMPAIGN_UUID= "campaignUUID";
        static final String NAME = "name";
        static final String APP_NAME = "appName";
        static final String DESCRIPTION = "description";
        static final String APP_DESCRIPTION = "appDescription";
        static final String APP_ID = "appID";
        static final String INSTALLED_AT = "installedAt";
        static final String UNINSTALLED_AT = "uninstalledAt";
        static final String REWARDING_EXPIRES_AFTER = "rewardingExpiresAfter";
        static final String REWARDING_EXPIRES_AT = "rewardingExpiresAt";
        static final String EVENT_CONFIG = "eventConfig";
        static final String APP_CATEGORY = "appCategory";
        static final String CAMPAIGN_TYPE = "campaignType";
        static final String CAMPAIGN_EXPIRES_AT = "campaignExpiresAt";
        static final String FEATURED_POSITION = "featuredPosition";
        static final String SCORE = "score";
        static final String IMAGE = "image";
        static final String VIDEO = "video";
        static final String ICON_IMAGE = "iconImage";
        static final String PROMOTION = "promotion";
        static final String IS_COMPLETED = "isCompleted";
        static final String STATUS = "status";
        static final String IS_CPA = "isCpa";
        static final String CPA = "cpa";

        static final String PORTRAIT = "portrait";
        static final String LANDSCAPE = "landscape";

        static final String PROMOTION_DESCRIPTION = "promotionDescription";
        static final String BOOST_FACTOR = "boostFactor";
        static final String START_TIME = "startTime";
        static final String END_TIME = "endTime";
        static final String TARGETING_TYPE = "targetingType";

        static final String SEQUENTIAL_ACTIONS = "sequentialActions";
        static final String BONUS_ACTIONS = "bonusActions";
        static final String TIME_BASED_ACTIONS = "timeBasedActions";
        static final String CPA_ACTIONS = "cpaActions";
        static final String TOTAL_COINS_COLLECTED = "totalCoinsCollected";
        static final String TOTAL_COINS_POSSIBLE = "totalCoinsPossible";
        static final String CASH_BACK_REWARD = "cashbackReward";
        static final String SECONDS_TO_NEXT_LEVEL = "secondsToNextLevel";
        static final String TOTAL_ORIGINAL_COINS_POSSIBLE = "totalOriginalCoinsPossible";
        static final String TOTAL_SEQUENTIAL_COINS = "totalSequentialCoins";
        static final String TOTAL_ORIGINAL_SEQUENTIAL_COINS = "totalOriginalSequentialCoins";
        static final String TOTAL_BONUS_COINS = "totalBonusCoins";
        static final String TOTAL_ORIGINAL_BONUS_COINS = "totalOriginalBonusCoins";
        static final String MULTIPLIERS_ACTIONS = "multipliersActions";

        static final String TASK_DESCRIPTION = "taskDescription";
        static final String TASK_TYPE = "taskType";
        static final String PLAY_DURATION = "playDuration";
        static final String AMOUNT = "amount";
        static final String REWARDED_AT = "rewardedAt";
        static final String LEVEL = "level";
        static final String REWARDS_COUNT = "rewardsCount";
        static final String COMPLETED_REWARDS = "completedRewards";
        static final String TIMED_COINS_DURATION = "timedCoinsDuration";
        static final String TIMED_COINS = "timedCoins";
        static final String ORIGINAL_COINS = "originalCoins";
        static final String IS_TIMED = "isTimed";
        static final String IS_REWARDED_FOR_PROMOTION = "isRewardedForPromotion";
        static final String BOOSTER_EXPIRES_AT = "boosterExpiresAt";

        static final String EVENT_NAME = "eventName";
        static final String EVENT_DESCRIPTION = "eventDescription";
        static final String MULTIPLIER_FACTOR_PERCENTAGE = "multiplierFactorPercentage";
        static final String MULTIPLIER_LEVELS = "multiplierLevels";
        static final String USED_LEVELS = "usedLevels";

        static final String EXCHANGE_RATE = "exchangeRate";
        static final String CASHBACK_DESCRIPTION = "cashbackDescription";
        static final String MAX_LIMIT_PER_CAMPAIGN_USD = "maxLimitPerCampaignUSD";
        static final String MAX_LIMIT_PER_CAMPAIGN_COINS = "maxLimitPerCampaignCoins";
        static final String PENDING_REWARDS = "pendingRewards";

        static final String TOTAL_COINS = "totalCoins";
        static final String EVENTS = "events";
        static final String COINS = "coins";
        static final String PROCESS_AT = "processAt";
        static final String RECEIVED_AT = "receivedAt";

        static final String CAMPAIGNS = "campaigns";

    }
}
