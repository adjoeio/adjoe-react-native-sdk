package io.adjoe.sdk.reactnative;

import static io.adjoe.sdk.reactnative.RNPlaytimeSdkModule.BasicUtil.constructOptionsFrom;
import static io.adjoe.sdk.reactnative.RNPlaytimeSdkModule.BasicUtil.constructPlaytimeParams;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.FrameLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.adjoe.sdk.Playtime;
import io.adjoe.sdk.PlaytimeInitialisationListener;
import io.adjoe.sdk.PlaytimeNotInitializedException;
import io.adjoe.sdk.PlaytimeOptions;
import io.adjoe.sdk.PlaytimeParams;
import io.adjoe.sdk.custom.PlaytimeAdvancePlusConfig;
import io.adjoe.sdk.custom.PlaytimeAdvancePlusEvent;
import io.adjoe.sdk.custom.PlaytimeCampaignListener;
import io.adjoe.sdk.custom.PlaytimeCampaignResponse;
import io.adjoe.sdk.custom.PlaytimeCampaignResponseError;
import io.adjoe.sdk.custom.PlaytimeCoinSetting;
import io.adjoe.sdk.custom.PlaytimeCustom;
import io.adjoe.sdk.custom.PlaytimeInAppPurchaseReward;
import io.adjoe.sdk.custom.PlaytimeInAppPurchaseRewardConfig;
import io.adjoe.sdk.custom.PlaytimeInAppPurchaseRewardEvent;
import io.adjoe.sdk.custom.PlaytimeStreakInfo;
import io.adjoe.sdk.custom.AppDetails;
import io.adjoe.sdk.custom.CategoryTranslation;
import io.adjoe.sdk.internal.PlaytimePartnerApp;
import io.adjoe.sdk.internal.PlaytimePromoEvent;
import io.adjoe.sdk.internal.TimedRewardMultiplierConfig;
import io.adjoe.sdk.internal.TimedRewardMultiplierEvent;

public class AdvancedIntegrationUtil {

    private static final DateFormat ISO_8601 = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.US);

    static void requestPartnerApps(ReactApplicationContext reactContext,
            RNPlaytimeSdkModule.WebViewSupplier webViewSupplier,
            Map<String, PlaytimePartnerApp> partnerApps,
            ReadableMap paramsMap, final Promise promise) {
        FrameLayout webViewContainer = null;
        if (webViewSupplier != null) {
            webViewContainer = webViewSupplier.getLayoutForWebView();
        }
        PlaytimeParams params = constructPlaytimeParams(paramsMap);
        PlaytimeCustom.requestPartnerApps(reactContext, webViewContainer, params,
                new PlaytimeCampaignListener() {

                    @Override
                    public void onCampaignsReceived(
                            PlaytimeCampaignResponse playtimeCampaignResponse) {
                        WritableArray apps = Arguments.createArray();
                        for (PlaytimePartnerApp app : playtimeCampaignResponse.partnerApps) {
                            apps.pushMap(partnerAppToWritableMap(app));
                            partnerApps.put(app.getPackageName(), app);
                        }
                        promise.resolve(apps);
                    }

                    @Override
                    public void onCampaignsReceivedError(
                            PlaytimeCampaignResponseError playtimeCampaignResponseError) {
                        if (playtimeCampaignResponseError.exception != null) {
                            promise.reject(playtimeCampaignResponseError.exception);
                        } else {
                            promise.reject("", "");
                        }
                    }
                });
    }

    static void getTestGroup(ReactApplicationContext reactContext, final Promise promise) {
        Integer testGroup = PlaytimeCustom.getTestGroup(reactContext);
        promise.resolve(testGroup);
    }

    static void requestPartnerAppsWithOptions(ReactApplicationContext reactContext,
            RNPlaytimeSdkModule.WebViewSupplier webViewSupplier,
            Map<String, PlaytimePartnerApp> partnerApps,
            ReadableMap optionsMap, final Promise promise) {
        FrameLayout webViewContainer = null;
        if (webViewSupplier != null) {
            webViewContainer = webViewSupplier.getLayoutForWebView();
        }

        PlaytimeOptions options = constructOptionsFrom(optionsMap);
        PlaytimeCustom.requestPartnerApps(reactContext, webViewContainer, options,
                new PlaytimeCampaignListener() {

                    @Override
                    public void onCampaignsReceived(
                            PlaytimeCampaignResponse playtimeCampaignResponse) {
                        WritableArray apps = Arguments.createArray();
                        for (PlaytimePartnerApp app : playtimeCampaignResponse.partnerApps) {
                            apps.pushMap(partnerAppToWritableMap(app));
                            partnerApps.put(app.getPackageName(), app);
                        }
                        promise.resolve(apps);
                    }

                    @Override
                    public void onCampaignsReceivedError(
                            PlaytimeCampaignResponseError playtimeCampaignResponseError) {
                        if (playtimeCampaignResponseError.exception != null) {
                            promise.reject(playtimeCampaignResponseError.exception);
                        } else {
                            promise.reject("", "");
                        }
                    }
                });
    }

    static void executePartnerAppClick(ReactApplicationContext reactContext,
            RNPlaytimeSdkModule.WebViewSupplier webViewSupplier,
            Map<String, PlaytimePartnerApp> partnerApps,
            final String packageName, ReadableMap map, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = partnerApps.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        FrameLayout webViewContainer = null;
        if (webViewSupplier != null) {
            webViewContainer = webViewSupplier.getLayoutForWebView();
        }
        PlaytimeParams params = constructPlaytimeParams(map);
        partnerApp.executeClick(
            reactContext,
            webViewContainer,
            params,
            new PlaytimePartnerApp.ClickListener() {

                    @Override
                    public void onFinished() {
                        promise.resolve(null);
                    }

                    @Override
                    public void onError(Exception exception) {
                        promise.reject(
                                new RuntimeException("Could not execute click for " + packageName));
                    }

                    @Override
                    public void onAlreadyClicking() {
                        promise.resolve("already_clicking");
                    }
                });
    }

    static void executeShowCampaignDetailClick(ReactApplicationContext reactContext,
            Map<String, PlaytimePartnerApp> partnerApps,
            final String packageName, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = partnerApps.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        partnerApp.executeShowCampaignDetailClick(
            reactContext,
            new PlaytimePartnerApp.ClickListener() {

                @Override
                public void onFinished() {
                    promise.resolve(null);
                }

                @Override
                public void onError(Exception exception) {
                    promise.reject(
                            new RuntimeException("Could not execute detail click for " + packageName));
                }

                @Override
                public void onAlreadyClicking() {
                    promise.resolve("already_clicking");
                }
                });
    }

    static void launchPartnerApp(ReactApplicationContext reactContext,
            Map<String, PlaytimePartnerApp> partnerApps,
            final String packageName, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = partnerApps.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }
        if (packageName.isEmpty()) {
            return;
        }

        try {
            final Intent launchIntent = reactContext.getPackageManager().getLaunchIntentForPackage(
                    packageName);
            if (launchIntent != null) {
                reactContext.startActivity(launchIntent);
                promise.resolve(true);
                return;
            }
        } catch (Exception ignored) {
        }

        try {
            final Intent marketLaunchIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName));
            marketLaunchIntent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
            marketLaunchIntent.setPackage("com.android.vending");
            reactContext.startActivity(marketLaunchIntent);
            promise.resolve(true);
        } catch (Exception exception) {
            promise.reject(exception);
        }
    }

    static void executePartnerAppView(ReactApplicationContext reactContext,
            RNPlaytimeSdkModule.WebViewSupplier webViewSupplier,
            Map<String, PlaytimePartnerApp> partnerApps,
            final String packageName, ReadableMap map, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = partnerApps.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        FrameLayout webViewContainer = null;
        if (webViewSupplier != null) {
            webViewContainer = webViewSupplier.getLayoutForWebView();
        }
        PlaytimeParams params = constructPlaytimeParams(map);
        partnerApp.executeView(reactContext, webViewContainer, params,
                new PlaytimePartnerApp.ViewListener() {

                    @Override
                    public void onFinished() {
                        promise.resolve(null);
                    }

                    @Override
                    public void onError() {
                        promise.reject(
                                new RuntimeException("Could not execute view for " + packageName));
                    }

                    @Override
                    public void onAlreadyViewing() {
                        promise.resolve("already_viewing");
                    }
                });
    }

    static void getRemainingTimeForPartnerApp(ReactApplicationContext reactContext,
            Map<String, PlaytimePartnerApp> partnerApps,
            String packageName, Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = partnerApps.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        long remainingTime = partnerApp.getRemainingUntilNextReward(reactContext);
        promise.resolve(String.valueOf(remainingTime));
    }

    static void getNextRewardLevelForPartnerApp(ReactApplicationContext reactContext,
            Map<String, PlaytimePartnerApp> partnerApps,
            String packageName, Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = partnerApps.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException("no partner app found for package name"));
            return;
        }

        PlaytimePartnerApp.RewardLevel level = partnerApp.getNextRewardLevel(reactContext);
        if (level == null) {
            promise.reject(new NullPointerException("partner app has no next reward level"));
        } else {
            promise.resolve(rewardLevelToWritableMap(level));
        }
    }

    static void requestInstalledPartnerApps(ReactApplicationContext reactContext,
            Map<String, PlaytimePartnerApp> partnerApps,
            ReadableMap map, final Promise promise) {
        PlaytimeParams params = constructPlaytimeParams(map);
        PlaytimeCustom.requestInstalledPartnerApps(reactContext, params,
                new PlaytimeCampaignListener() {

                    @Override
                    public void onCampaignsReceived(PlaytimeCampaignResponse playtimeCampaignResponse) {
                        WritableArray apps = Arguments.createArray();
                        for (PlaytimePartnerApp app : playtimeCampaignResponse.partnerApps) {
                            apps.pushMap(partnerAppToWritableMap(app));
                            partnerApps.put(app.getPackageName(), app);
                        }
                        promise.resolve(apps);
                    }

                    @Override
                    public void onCampaignsReceivedError(
                            PlaytimeCampaignResponseError playtimeCampaignResponseError) {
                        if (playtimeCampaignResponseError.exception != null) {
                            promise.reject(playtimeCampaignResponseError.exception);
                        } else {
                            promise.reject("", "");
                        }
                    }
                });
    }

    static void getInstallDate(Map<String, PlaytimePartnerApp> partnerApps,
            String packageName, Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = partnerApps.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        Date installDate = partnerApp.getInstallDate();
        if (installDate == null) {
            promise.reject(
                    new NullPointerException("no time found for this partner app" + packageName));
            return;
        }

        promise.resolve(ISO_8601.format(installDate));
    }

    static void _a(boolean a, final Promise promise) {
        try {
            Playtime.a(a);
            promise.resolve(null);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    static void setTosAccepted(ReactApplicationContext reactContext, final Promise promise) {
        PlaytimeCustom.setTosAccepted(reactContext, new PlaytimeInitialisationListener() {

            @Override
            public void onInitialisationFinished() {
                promise.resolve(null);
            }

            @Override
            public void onInitialisationError(Exception e) {
                promise.reject(e);
            }
        });
    }

    static void setUsagePermissionAccepted(ReactApplicationContext reactContext,
            final Promise promise) {
        PlaytimeCustom.setUsagePermissionAccepted(reactContext,
                new PlaytimeInitialisationListener() {

                    @Override
                    public void onInitialisationFinished() {
                        promise.resolve(null);
                    }

                    @Override
                    public void onInitialisationError(Exception e) {
                        promise.reject(e);
                    }
                });
    }

    static void showUsagePermissionScreen(ReactApplicationContext reactContext,
            final Promise promise) {
        try {
            Activity localActivity = reactContext.getCurrentActivity();
            if (localActivity != null) {
                PlaytimeCustom.showUsagePermissionScreen(reactContext.getCurrentActivity());
                promise.resolve(null);
            } else {
                promise.reject(new PlaytimeNotInitializedException("Not initialized"));
            }
        } catch (PlaytimeNotInitializedException e) {
            promise.reject(e);
        }
    }

    static void sendEvent(ReactApplicationContext reactContext, int event, String extra,
            ReadableMap map) {
        try {
            PlaytimeParams params = constructPlaytimeParams(map);
            Playtime.sendUserEvent(reactContext, event, extra, params);
        } catch (PlaytimeNotInitializedException e) {
            Log.w("RNAdjoeSDK", e);
        }
    }

    static void isBoostedEvent(Map<String, PlaytimePartnerApp> partnerApps,
            String packageName, ReadableMap event, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = partnerApps.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        PlaytimeAdvancePlusEvent playtimeAdvancePlusEvent = constructPlaytimeAdvancePlusEvent(event);
        promise.resolve(partnerApp.isBoostedEvent(playtimeAdvancePlusEvent));
    }

    static void isBoostedEventExpired(Map<String, PlaytimePartnerApp> partnerApps,
            String packageName, ReadableMap event, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = partnerApps.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        PlaytimeAdvancePlusEvent playtimeAdvancePlusEvent = constructPlaytimeAdvancePlusEvent(event);
        promise.resolve(partnerApp.isBoostedEventExpired(playtimeAdvancePlusEvent));
    }

    static void timeToExpireBoostedEventInSeconds(Map<String, PlaytimePartnerApp> partnerApps,
            String packageName, ReadableMap event, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = partnerApps.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        PlaytimeAdvancePlusEvent playtimeAdvancePlusEvent = constructPlaytimeAdvancePlusEvent(event);
        promise.resolve((int) partnerApp.timeToExpireBoostedEventInSeconds(playtimeAdvancePlusEvent));
    }

    static void faceVerification(ReactApplicationContext reactContext, final Promise promise) {
        Activity activity = reactContext.getCurrentActivity();

        if (activity == null) {
            promise.reject("0", "phoneVerificationSupplier.getActivity() == null");
            return;
        }

        PlaytimeCustom.faceVerification(activity, new PlaytimeCustom.FaceVerificationCallback() {

            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onAlreadyVerified() {
                promise.reject("1", "already_verified");
            }

            @Override
            public void onCancel() {
                promise.reject("2", "cancel");
            }

            @Override
            public void onNotInitialized() {
                promise.reject("3", "not_initialized");
            }

            @Override
            public void onTosIsNotAccepted() {
                promise.reject("4", "tos_not_accepted");
            }

            @Override
            public void onLivenessCheckFailed() {
                promise.reject("5", "liveness_check_failed");
            }

            @Override
            public void onError(Exception exception) {
                promise.reject("0", exception.getMessage(), exception);
            }

            @Override
            public void onPendingReview() {
                promise.reject("7", "pending_review");
            }

            @Override
            public void onMaxAttemptsReached() {
                promise.reject("8", "max_attempts_reached");
            }
        });
    }

    static void faceVerificationStatus(ReactApplicationContext reactContext,
            final Promise promise) {
        try {
            PlaytimeCustom.faceVerificationStatus(reactContext,
                    new PlaytimeCustom.FaceVerificationStatusCallback() {

                        @Override
                        public void onVerified() {
                            promise.resolve(null);
                        }

                        @Override
                        public void onNotVerified() {
                            promise.reject("1", "not_verified");
                        }

                        @Override
                        public void onNotInitialized() {
                            promise.reject("2", "not_initialized");
                        }

                        @Override
                        public void onTosIsNotAccepted() {
                            promise.reject("3", "tos_not_accepted");
                        }

                        @Override
                        public void onError(Exception exception) {
                            promise.reject("0", exception.getMessage(), exception);
                        }

                        @Override
                        public void onPendingReview() {
                            promise.reject("4", "pending_review");
                        }

                        @Override
                        public void onMaxAttemptsReached() {
                            promise.reject("5", "max_attempts_reached");
                        }
                    });
        } catch (PlaytimeNotInitializedException e) {
            promise.reject("0", e.getMessage(), e);
        }
    }

    static PlaytimeAdvancePlusEvent constructPlaytimeAdvancePlusEvent(ReadableMap event) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", event.getString("name"));
            jsonObject.put("Description", event.getString("description"));
            jsonObject.put("Coins", event.getInt("coins"));
            jsonObject.put("Type", 0);
            jsonObject.put("RewardedAt", "");
            jsonObject.put("TimedCoinsDuration", event.getInt("timedCoinsDurationInMin"));
            jsonObject.put("TimedCoins", event.getInt("timedCoins"));
            return new PlaytimeAdvancePlusEvent(jsonObject);
        } catch (JSONException e) {
            return null;
        }
    }

    static WritableMap partnerAppToWritableMap(PlaytimePartnerApp app) {
        WritableMap appMap = Arguments.createMap();
        appMap.putString(StudioUtil.JsonKey.CAMPAIGN_TYPE, app.getCampaignType());
        appMap.putString(StudioUtil.JsonKey.NAME, app.getName());
        appMap.putString("packageName", app.getPackageName());
        appMap.putString(StudioUtil.JsonKey.DESCRIPTION, app.getDescription());
        appMap.putString("iconUrl", app.getIconURL());
        appMap.putString("landscapeImageUrl", app.getLandscapeImageURL());
        appMap.putString("videoUrl", app.getVideoURL());
        appMap.putBoolean("inAppPurchaseEnabled", app.isInAppPurchaseEnabled());
        appMap.putDouble("createdAt",
                app.getCreatedAt() == null ? -1 : app.getCreatedAt().getTime());
        appMap.putDouble("multiplier", app.getMultiplier());
        appMap.putString("category", app.getAppCategory());
        appMap.putString("portraitImageUrl", app.getPortraitImageURL());
        appMap.putString("portraitVideoUrl", app.getPortraitVideoURL());
        appMap.putString("promotionTargetingType", app.getPromotionTargetingType());

        int rewardingExpiresAfter = app.getRewardingExpiresAfter();
        if (rewardingExpiresAfter == -1) {
            appMap.putNull(StudioUtil.JsonKey.REWARDING_EXPIRES_AFTER);
        } else {
            appMap.putInt(StudioUtil.JsonKey.REWARDING_EXPIRES_AFTER, rewardingExpiresAfter);
        }

        AppDetails appDetails = app.getAppDetails();
        if (appDetails != null) {
            appMap.putMap("appDetails", appDetailsToWritableMap(appDetails));
        }

        if (app.getAdvanceRewardCoins() > 0) {
            appMap.putInt("postInstallRewardEventCoins", app.getAdvanceRewardCoins());
            appMap.putInt("advanceRewardCoins", app.getAdvanceRewardCoins());
        }

        PlaytimePromoEvent event = app.getEvent();
        if (event != null && event.isRunningNow()) {
            appMap.putString("promoStartDate", String.valueOf(event.getStartDate().getTime()));
            appMap.putString("promoEndDate", String.valueOf(event.getEndDate().getTime()));
            appMap.putDouble("promoFactor", event.getFactor());
        }

        WritableArray rewardConfig = Arguments.createArray();
        for (Object level : app.getRewardConfig()) {
            rewardConfig.pushMap(rewardLevelToWritableMap((PlaytimePartnerApp.RewardLevel) level));
        }

        appMap.putArray("rewardConfig", rewardConfig);

        WritableMap eventConfigs = Arguments.createMap();


        WritableMap playtimeRewardMultiplierConfigMap = Arguments.createMap();
        WritableMap playtimeInAppPurchaseRewardConfigMap = Arguments.createMap();
        TimedRewardMultiplierConfig playtimeRewardMultiplierConfig = app.getTimedRewardMultiplierConfig();
        PlaytimeInAppPurchaseRewardConfig playtimeInAppPurchaseRewardConfig = app.getInAppPurchaseRewardConfig();
        if (playtimeRewardMultiplierConfig != null) {
            playtimeRewardMultiplierConfigMap.putBoolean("isPlaytimeWithMultiplier", playtimeRewardMultiplierConfig.isPlaytimeWithMultiplier());

            WritableArray eventsArray = Arguments.createArray();
            List<TimedRewardMultiplierEvent> playtimeRewardMultiplierEvents = playtimeRewardMultiplierConfig.getEvents();
            for (int i = 0; i < playtimeRewardMultiplierEvents.size(); i++) {
                TimedRewardMultiplierEvent playtimeRewardMultiplierEvent = playtimeRewardMultiplierEvents.get(i);
                WritableMap eventMap = Arguments.createMap();
                eventMap.putString(StudioUtil.JsonKey.EVENT_NAME, playtimeRewardMultiplierEvent.getEventName());
                eventMap.putInt(StudioUtil.JsonKey.MULTIPLIER_FACTOR_PERCENTAGE, playtimeRewardMultiplierEvent.getMultiplierFactorPercentage());
                eventMap.putInt(StudioUtil.JsonKey.MULTIPLIER_LEVELS, playtimeRewardMultiplierEvent.getMultiplierLevels());
                eventMap.putString(StudioUtil.JsonKey.DESCRIPTION, playtimeRewardMultiplierEvent.getDescription());


                TimedRewardMultiplierEvent.Status status = playtimeRewardMultiplierEvent.getStatus();
                eventMap.putString(StudioUtil.JsonKey.STATUS, status != null ? status.getValue() : null);

                if (playtimeRewardMultiplierEvent.getActivatedAt() != null) {
                    long activateAt = playtimeRewardMultiplierEvent.getActivatedAt().getTime();
                    eventMap.putDouble("activatedAt", activateAt);
                } else {
                    eventMap.putNull("activatedAt");
                }

                if (playtimeRewardMultiplierEvent.getLastUsedAt() != null) {
                    long lastUsedAt = playtimeRewardMultiplierEvent.getLastUsedAt().getTime();
                    eventMap.putDouble("lastUsedAt", lastUsedAt);
                } else {
                    eventMap.putNull("lastUsedAt");
                }

                eventMap.putInt(StudioUtil.JsonKey.USED_LEVELS, playtimeRewardMultiplierEvent.getUsedLevels());
                eventsArray.pushMap(eventMap);
            }

            playtimeRewardMultiplierConfigMap.putArray(StudioUtil.JsonKey.EVENTS, eventsArray);

            eventConfigs.putMap("timeRewardMultiplierConfig", playtimeRewardMultiplierConfigMap);
        }
        if (playtimeInAppPurchaseRewardConfig != null) {
            playtimeInAppPurchaseRewardConfigMap.putString(
                StudioUtil.JsonKey.DESCRIPTION, playtimeInAppPurchaseRewardConfig.getDescription()
            );

            if (playtimeInAppPurchaseRewardConfig.getExchangeRate() != null)  {
                playtimeInAppPurchaseRewardConfigMap.putDouble(
                    StudioUtil.JsonKey.EXCHANGE_RATE, playtimeInAppPurchaseRewardConfig.getExchangeRate()
                );
            } else {
                playtimeInAppPurchaseRewardConfigMap.putNull(StudioUtil.JsonKey.EXCHANGE_RATE);
            }

            if (playtimeInAppPurchaseRewardConfig.getMaxLimitPerCampaignUSD() != null) {
                playtimeInAppPurchaseRewardConfigMap.putDouble(
                    StudioUtil.JsonKey.MAX_LIMIT_PER_CAMPAIGN_USD, playtimeInAppPurchaseRewardConfig.getMaxLimitPerCampaignUSD()
                );
            } else {
                playtimeInAppPurchaseRewardConfigMap.putNull(StudioUtil.JsonKey.MAX_LIMIT_PER_CAMPAIGN_USD);
            }

            if (playtimeInAppPurchaseRewardConfig.getMaxLimitPerCampaignCoins() != null) {
                playtimeInAppPurchaseRewardConfigMap.putDouble(
                    StudioUtil.JsonKey.MAX_LIMIT_PER_CAMPAIGN_COINS, playtimeInAppPurchaseRewardConfig.getMaxLimitPerCampaignCoins()
                );
            } else {
                playtimeInAppPurchaseRewardConfigMap.putNull(StudioUtil.JsonKey.MAX_LIMIT_PER_CAMPAIGN_COINS);
            }

            WritableMap completedRewardsMap = Arguments.createMap();
            WritableMap pendingRewardsMap = Arguments.createMap();
            PlaytimeInAppPurchaseReward completedRewardsConfig
                = playtimeInAppPurchaseRewardConfig.getCompletedRewards();
            PlaytimeInAppPurchaseReward pendingRewardsConfig
                = playtimeInAppPurchaseRewardConfig.getPendingRewards();

            if (completedRewardsConfig != null) {
                if (completedRewardsConfig.getTotalCoins() != null) {
                    completedRewardsMap.putInt(StudioUtil.JsonKey.TOTAL_COINS, completedRewardsConfig.getTotalCoins());
                } else {
                    completedRewardsMap.putNull(StudioUtil.JsonKey.TOTAL_COINS);
                }

                WritableArray completedRewardsEventsArray = createInAppPurchaseReward(completedRewardsConfig);
                completedRewardsMap.putArray(StudioUtil.JsonKey.EVENTS, completedRewardsEventsArray);
                playtimeInAppPurchaseRewardConfigMap.putMap(StudioUtil.JsonKey.COMPLETED_REWARDS, completedRewardsMap);
            } else {
                playtimeInAppPurchaseRewardConfigMap.putNull(StudioUtil.JsonKey.COMPLETED_REWARDS);
            }

            if (pendingRewardsConfig != null) {
                if (pendingRewardsConfig.getTotalCoins() != null) {
                    pendingRewardsMap.putInt(StudioUtil.JsonKey.TOTAL_COINS, pendingRewardsConfig.getTotalCoins());
                } else {
                    pendingRewardsMap.putNull(StudioUtil.JsonKey.TOTAL_COINS);
                }
                WritableArray pendingRewardsEventsArray = createInAppPurchaseReward(pendingRewardsConfig);
                pendingRewardsMap.putArray(StudioUtil.JsonKey.EVENTS, pendingRewardsEventsArray);
                playtimeInAppPurchaseRewardConfigMap.putMap(StudioUtil.JsonKey.PENDING_REWARDS, pendingRewardsMap);
            } else {
                playtimeInAppPurchaseRewardConfigMap.putNull(StudioUtil.JsonKey.PENDING_REWARDS);
            }

            eventConfigs.putMap("inAppPurchaseRewardConfig", playtimeInAppPurchaseRewardConfigMap);
        }

        appMap.putMap("eventConfigs", eventConfigs);

        // new
        appMap.putInt("advanceDailyLimit", app.getAdvanceDailyLimit());
        appMap.putInt("advanceTotalLimit", app.getAdvanceTotalLimit());
        appMap.putInt("advancePlusCoins", app.getAdvancePlusCoins());
        appMap.putString("advancePlusActionDescription", app.getAdvancePlusActionDescription());
        appMap.putString("advancePlusRewardAction", app.getAdvancePlusRewardedAction());

        // streak info for request app
        appMap.putBoolean("isInCoinStreakExperiment", app.isInCoinStreakExperiment());
        appMap.putInt("coinStreakMaxCoinAmount", app.getCoinStreakMaxCoinAmount());

        // streak info for rewarded
        PlaytimeStreakInfo streakInfo = app.getStreakInfo();
        if (streakInfo != null) {
            WritableMap streakInfoMap = Arguments.createMap();
            streakInfoMap.putBoolean("streakInfoFailed", streakInfo.isFailed());
            streakInfoMap.putInt("streakInfoLastAchievedDay", streakInfo.getLastAchievedDay());
            WritableArray coinSettingsArray = Arguments.createArray();
            List<PlaytimeCoinSetting> coinSettings = streakInfo.getCoinSettings();
            for (PlaytimeCoinSetting coinSetting : coinSettings) {
                WritableMap coinSettingMap = Arguments.createMap();
                coinSettingMap.putInt("day", coinSetting.getDay());
                coinSettingMap.putInt(StudioUtil.JsonKey.COINS, coinSetting.getCoins());
                coinSettingsArray.pushMap(coinSettingMap);
            }
            streakInfoMap.putArray("coinSettings", coinSettingsArray);
            appMap.putMap("streakInfo", streakInfoMap);
        }

        PlaytimeAdvancePlusConfig advancePlusConfig = app.getAdvancePlusConfig();
        if (advancePlusConfig != null) {
            WritableMap advancePlusConfigMap = Arguments.createMap();
            advancePlusConfigMap.putInt(StudioUtil.JsonKey.TOTAL_COINS, advancePlusConfig.getTotalCoins());
            advancePlusConfigMap.putInt("highestBonusEventCoins",
                    advancePlusConfig.getHighestBonusEventCoins());
            advancePlusConfigMap.putInt("highestSequentialEventCoins",
                    advancePlusConfig.getHighestSequentialEventCoins());

            WritableArray sequentialEventsArray = createEventsWritableArray(advancePlusConfig.getSequentialEvents());
            advancePlusConfigMap.putArray("sequentialEvents", sequentialEventsArray);

            WritableArray bonusEventsArray = createEventsWritableArray(advancePlusConfig.getBonusEvents());
            advancePlusConfigMap.putArray("bonusEvents", bonusEventsArray);

            appMap.putMap("advancePlusConfig", advancePlusConfigMap);
        }
        return appMap;
    }

    static WritableArray createInAppPurchaseReward(PlaytimeInAppPurchaseReward rewardsConfig) {
        WritableArray rewardsEventsArray = Arguments.createArray();
        List<PlaytimeInAppPurchaseRewardEvent> rewardsEvents = rewardsConfig.getEvents();

        for (int i = 0; i < rewardsEvents.size(); i++) {
            PlaytimeInAppPurchaseRewardEvent rewardEvent = rewardsEvents.get(i);
            WritableMap eventMap = Arguments.createMap();
            if (rewardEvent.getCoins() != null) {
                eventMap.putInt(StudioUtil.JsonKey.COINS, rewardEvent.getCoins());
            } else {
                eventMap.putNull(StudioUtil.JsonKey.COINS);
            }

            if (rewardEvent.getProcessAt() != null) {
                long processAt = rewardEvent.getProcessAt().getTime();
                eventMap.putDouble(StudioUtil.JsonKey.PROCESS_AT, processAt);
            } else {
                eventMap.putNull(StudioUtil.JsonKey.PROCESS_AT);
            }

            if (rewardEvent.getReceivedAt() != null) {
                long receivedAt = rewardEvent.getReceivedAt().getTime();
                eventMap.putDouble(StudioUtil.JsonKey.RECEIVED_AT, receivedAt);
            } else {
                eventMap.putNull(StudioUtil.JsonKey.RECEIVED_AT);
            }

            rewardsEventsArray.pushMap(eventMap);
        }

        return rewardsEventsArray;
    }

    static WritableArray createEventsWritableArray(List<PlaytimeAdvancePlusEvent> events) {
        WritableArray eventsArray = Arguments.createArray();

        for (PlaytimeAdvancePlusEvent event : events) {
            WritableMap eventMap = Arguments.createMap();
            eventMap.putString(StudioUtil.JsonKey.NAME, event.getName());
            eventMap.putString(StudioUtil.JsonKey.DESCRIPTION, event.getDescription());
            eventMap.putInt(StudioUtil.JsonKey.COINS, event.getCoins());
            eventMap.putInt(StudioUtil.JsonKey.TIMED_COINS, event.getTimedCoins());
            eventMap.putInt("timedCoinsDurationInMin", (int) event.getTimedCoinsDuration());
            eventMap.putInt(StudioUtil.JsonKey.REWARDS_COUNT, event.getRewardsCount());
            eventMap.putInt(StudioUtil.JsonKey.COMPLETED_REWARDS, event.getCompletedRewards());

            String rewardedAt = event.getRewardedAt();
            if (rewardedAt != null && !rewardedAt.isEmpty()) {
                eventMap.putString(StudioUtil.JsonKey.REWARDED_AT, rewardedAt);
            }

            eventsArray.pushMap(eventMap);
        }
        return eventsArray;
    }

    static WritableMap rewardLevelToWritableMap(PlaytimePartnerApp.RewardLevel level) {
        WritableMap map = Arguments.createMap();
        if (level == null) {
            return map;
        }

        map.putInt(StudioUtil.JsonKey.LEVEL, level.getLevel());
        map.putDouble("seconds", level.getSeconds());
        map.putDouble("value", level.getValue());
        return map;
    }

    static WritableMap appDetailsToWritableMap(AppDetails details) {
        WritableMap map = Arguments.createMap();
        if (details == null) {
            return map;
        }
        map.putString("platform", details.getPlatform());
        map.putInt("androidVersion", details.getAndroidVersion());
        map.putString("rating", details.getRating());
        map.putString("numOfRatings", details.getNumOfRatings());
        map.putString("size", details.getSize());
        map.putString("installs", details.getInstalls());
        map.putString("ageRating", details.getAgeRating());
        map.putString("category", details.getCategory());
        map.putBoolean("hasInAppPurchases", details.getHasInAppPurchases());
        WritableArray array = Arguments.createArray();
        for (CategoryTranslation categoryTranslation : details.getCategoryTranslations()) {
            WritableMap categoryMap = Arguments.createMap();
            categoryMap.putString(StudioUtil.JsonKey.NAME, categoryTranslation.getName());
            categoryMap.putString("language", categoryTranslation.getLanguage());
            array.pushMap(categoryMap);
        }
        map.putArray("categoryTranslations", array);
        return map;
    }
}
