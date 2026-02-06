package io.adjoe.sdk.reactnative;

import static io.adjoe.sdk.reactnative.Util.statusToReadableMap;
import static io.adjoe.sdk.reactnative.Util.constructOptionsFrom;
import static io.adjoe.sdk.reactnative.Util.constructPlaytimeParams;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.adjoe.sdk.Playtime;
import io.adjoe.sdk.PlaytimeException;
import io.adjoe.sdk.PlaytimeStatus;
import io.adjoe.sdk.PlaytimeInitialisationListener;
import io.adjoe.sdk.PlaytimeNotInitializedException;
import io.adjoe.sdk.PlaytimeOptions;
import io.adjoe.sdk.PlaytimeOptionsListener;
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

@SuppressWarnings("unused")
public class RNPlaytimeSdkModule extends ReactContextBaseJavaModule {

    private static final DateFormat ISO_8601 = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.US);
    private static final DateFormat BIRTHDAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    static final Map<String, PlaytimePartnerApp> PARTNER_APPS = new HashMap<>();
    static WebViewSupplier webViewSupplier;
    private final ReactApplicationContext reactContext;

    public RNPlaytimeSdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNPlaytimeSdk";
    }

    /* -----------------------------
             CONSTANTS START
       ----------------------------- */

    @Override
    public Map<String, Object> getConstants() {
        Map<String, Object> constants = new HashMap<>();
        constants.put("VERSION", Playtime.getVersion());
        constants.put("VERSION_NAME", Playtime.getVersionName());
        constants.put("EVENT_AGB_SHOWN", PlaytimeCustom.EVENT_AGB_SHOWN);
        constants.put("EVENT_AGB_ACCEPTED", PlaytimeCustom.EVENT_AGB_ACCEPTED);
        constants.put("EVENT_AGB_DECLINED", PlaytimeCustom.EVENT_AGB_DECLINED);
        constants.put("EVENT_USAGE_PERMISSION_ACCEPTED", PlaytimeCustom.EVENT_USAGE_PERMISSION_ACCEPTED);
        constants.put("EVENT_USAGE_PERMISSION_DENIED", PlaytimeCustom.EVENT_USAGE_PERMISSION_DENIED);
        constants.put("EVENT_VIDEO_PLAY", PlaytimeCustom.EVENT_VIDEO_PLAY);
        constants.put("EVENT_VIDEO_PAUSE", PlaytimeCustom.EVENT_VIDEO_PAUSE);
        constants.put("EVENT_VIDEO_ENDED", PlaytimeCustom.EVENT_VIDEO_ENDED);
        constants.put("EVENT_CAMPAIGNS_SHOWN", PlaytimeCustom.EVENT_CAMPAIGNS_SHOWN);
        constants.put("EVENT_APP_OPEN", PlaytimeCustom.EVENT_APP_OPEN);
        constants.put("EVENT_FIRST_IMPRESSION", PlaytimeCustom.EVENT_FIRST_IMPRESSION);
        constants.put("EVENT_TEASER_SHOWN", Playtime.EVENT_TEASER_SHOWN);
        return constants;
    }

    /* -----------------------------
              CONSTANTS END
       ----------------------------- */

    /* -----------------------------
           STATIC METHODS START
    ----------------------------- */

    public static void setWebViewSupplier(WebViewSupplier supplier) {
        RNPlaytimeSdkModule.webViewSupplier = supplier;
    }

     public static void setPhoneVerificationSupplier(PhoneVerificationSupplier supplier) { }

    /* -----------------------------
            STATIC METHODS END
    ----------------------------- */

    /* -----------------------------
      BASIC INTEGRATION METHODS START
       ----------------------------- */

    @ReactMethod
    public void init(String apiKey, ReadableMap optionsMap, final Promise promise) {
        try {
            PlaytimeOptions options = constructOptionsFrom(optionsMap);

            Playtime.init(reactContext, apiKey, options, new PlaytimeInitialisationListener() {

                @Override
                public void onInitialisationFinished() {
                    promise.resolve(null);
                }

                @Override
                public void onInitialisationError(Exception e) {
                    promise.reject(e);
                }
            });
        } catch (Exception e) {
            Log.w("RNAdjoeSDK", e);
            promise.reject(e);
        }
    }

    @ReactMethod
    public void showCatalog(ReadableMap configMap, Promise promise) {
        Intent catalogIntent;
        try {
            PlaytimeParams params = constructPlaytimeParams(configMap);
            catalogIntent = Playtime.getCatalogIntent(reactContext, params);
        } catch (PlaytimeException e) {
            promise.reject(e);
            return;
        }
        try {
            if (getCurrentActivity() != null) {
                getCurrentActivity().startActivity(catalogIntent);
                promise.resolve(null);
            } else {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    catalogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                reactContext.startActivity(catalogIntent);
                promise.resolve(null);
            }
        } catch (Exception e) {
            Log.w("RNAdjoeSDK", e);
            promise.reject(e);
        }
    }

    @ReactMethod
    public void showCatalogWithOptions(ReadableMap configMap, Promise promise) {
        try {
            PlaytimeOptions options = constructOptionsFrom(configMap);
            Activity localActivity = getCurrentActivity();
            if (localActivity != null) {
                Playtime.showCatalog(localActivity, options);
                promise.resolve(null);
            } else {
                Log.w("RNAdjoeSDK","Can't load the catalog, activity is null");
                promise.resolve(null);
            }
        } catch (PlaytimeException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setUAParams(ReadableMap paramsMap, final Promise promise) {
        PlaytimeParams params = constructPlaytimeParams(paramsMap);
        Playtime.setUAParams(reactContext, params);
        promise.resolve(null);
    }

    @ReactMethod
    public void setPlaytimeOptions(ReadableMap paramsMap, final Promise promise) {
        PlaytimeOptions options = constructOptionsFrom(paramsMap);
        Playtime.setPlaytimeOptions(options, new PlaytimeOptionsListener() {
            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onError(String error) {
                promise.reject(error);
            }
        });
    }


    /* -----------------------------
       BASIC INTEGRATION METHODS END
       ----------------------------- */

    /* -----------------------------
     ADVANCED INTEGRATION METHODS START
       ----------------------------- */

    @ReactMethod
    public void requestPartnerApps(ReadableMap paramsMap, final Promise promise) {
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

                            PARTNER_APPS.put(app.getPackageName(), app);
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

    @ReactMethod
    public void getTestGroup(final Promise promise) {
        Integer testGroup = PlaytimeCustom.getTestGroup(reactContext);
        promise.resolve(testGroup);
    }

    @ReactMethod
    public void requestPartnerAppsWithOptions(ReadableMap optionsMap, final Promise promise) {
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

                            PARTNER_APPS.put(app.getPackageName(), app);
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

    @ReactMethod
    public void executePartnerAppClick(final String packageName, ReadableMap map,
            final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

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
                    public void onError() {
                        promise.reject(
                                new RuntimeException("Could not execute click for " + packageName));
                    }

                    @Override
                    public void onAlreadyClicking() {
                        promise.resolve("already_clicking");
                    }
                });
    }

    @ReactMethod
    public void executeShowCampaignDetailClick(final String packageName, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

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
                public void onError() {
                    promise.reject(
                            new RuntimeException("Could not execute detail click for " + packageName));
                }

                @Override
                public void onAlreadyClicking() {
                    promise.resolve("already_clicking");
                }
                });
    }

    @ReactMethod
    public void launchPartnerApp(final String packageName, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

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

    @ReactMethod
    public void executePartnerAppView(final String packageName, ReadableMap map,
            final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

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

    @ReactMethod
    public void getRemainingTimeForPartnerApp(String packageName, Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        long remainingTime = partnerApp.getRemainingUntilNextReward(reactContext);
        promise.resolve(String.valueOf(remainingTime));
    }

    @ReactMethod
    public void getNextRewardLevelForPartnerApp(String packageName, Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

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

    @ReactMethod
    public void requestInstalledPartnerApps(ReadableMap map, final Promise promise) {
        PlaytimeParams params = constructPlaytimeParams(map);
        PlaytimeCustom.requestInstalledPartnerApps(reactContext, params,
                new PlaytimeCampaignListener() {

                    @Override
                    public void onCampaignsReceived(PlaytimeCampaignResponse playtimeCampaignResponse) {
                        WritableArray apps = Arguments.createArray();
                        for (PlaytimePartnerApp app : playtimeCampaignResponse.partnerApps) {
                            apps.pushMap(partnerAppToWritableMap(app));
                            PARTNER_APPS.put(app.getPackageName(), app);
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

    @ReactMethod
    public void getInstallDate(String packageName, Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

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

    @ReactMethod
    public void _a(boolean a, final Promise promise) {
        try {
            Playtime.a(a);
            promise.resolve(null);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setTosAccepted(final Promise promise) {
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

    @ReactMethod
    public void setUsagePermissionAccepted(final Promise promise) {
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

    @ReactMethod
    public void showUsagePermissionScreen(final Promise promise) {
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

    @ReactMethod
    public void sendEvent(int event, String extra, ReadableMap map) {
        try {
            PlaytimeParams params = constructPlaytimeParams(map);
            Playtime.sendUserEvent(reactContext, event, extra, params);
        } catch (PlaytimeNotInitializedException e) {
            Log.w("RNAdjoeSDK", e);
        }
    }

    @ReactMethod
    public void isBoostedEvent(String packageName, ReadableMap event, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        PlaytimeAdvancePlusEvent playtimeAdvancePlusEvent = constructPlaytimeAdvancePlusEvent(event);
        promise.resolve(partnerApp.isBoostedEvent(playtimeAdvancePlusEvent));
    }

    @ReactMethod
    public void isBoostedEventExpired(String packageName, ReadableMap event, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        PlaytimeAdvancePlusEvent playtimeAdvancePlusEvent = constructPlaytimeAdvancePlusEvent(event);
        promise.resolve(partnerApp.isBoostedEventExpired(playtimeAdvancePlusEvent));
    }

    @ReactMethod
    public void timeToExpireBoostedEventInSeconds(String packageName, ReadableMap event, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        PlaytimeAdvancePlusEvent playtimeAdvancePlusEvent = constructPlaytimeAdvancePlusEvent(event);
        promise.resolve((int) partnerApp.timeToExpireBoostedEventInSeconds(playtimeAdvancePlusEvent));
    }

    /* -----------------------------
      ADVANCED INTEGRATION METHODS END
       ----------------------------- */


    /* -----------------------------
          UTILITY METHODS START
       ----------------------------- */

    @ReactMethod
    public void getVersion(Promise promise) {
        promise.resolve(Playtime.getVersion());
    }

    @ReactMethod
    public void getVersionName(Promise promise) {
        promise.resolve(Playtime.getVersionName());
    }

    @ReactMethod
    public void isInitialized(Promise promise) {
        promise.resolve(Playtime.isInitialized());
    }

    @ReactMethod
    public void hasAcceptedTOS(Promise promise) {
        promise.resolve(Playtime.hasAcceptedTOS(reactContext));
    }

    @ReactMethod
    public void hasAcceptedUsagePermission(Promise promise) {
        promise.resolve(Playtime.hasAcceptedUsagePermission(reactContext));
    }

    @ReactMethod
    public void getUserId(Promise promise) {
        promise.resolve(Playtime.getUserId(reactContext));
    }

    @ReactMethod
    public void getStatus(Promise promise) {
        PlaytimeStatus status = Playtime.getStatus();
        ReadableMap mapStatus = statusToReadableMap(status);
        promise.resolve(mapStatus);
    }

    /* -----------------------------
           UTILITY METHODS END
       ----------------------------- */

    /* -----------------------------
           FRAUD METHODS START
    ----------------------------- */

    @ReactMethod
    public void faceVerification(final Promise promise) {
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

    @ReactMethod
    public void faceVerificationStatus(final Promise promise) {
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

    /* -----------------------------
            FRAUD METHODS END
    ----------------------------- */

    // region helper methods

    private PlaytimeAdvancePlusEvent constructPlaytimeAdvancePlusEvent(ReadableMap event) {
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

    private WritableMap partnerAppToWritableMap(PlaytimePartnerApp app) {
        WritableMap appMap = Arguments.createMap();
        appMap.putString("campaignType", app.getCampaignType());
        appMap.putString("name", app.getName());
        appMap.putString("packageName", app.getPackageName());
        appMap.putString("description", app.getDescription());
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
            appMap.putNull("rewardingExpiresAfter");
        } else {
            appMap.putInt("rewardingExpiresAfter", rewardingExpiresAfter);
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
                eventMap.putString("eventName", playtimeRewardMultiplierEvent.getEventName());
                eventMap.putInt("multiplierFactorPercentage", playtimeRewardMultiplierEvent.getMultiplierFactorPercentage());
                eventMap.putInt("multiplierLevels", playtimeRewardMultiplierEvent.getMultiplierLevels());
                eventMap.putString("description", playtimeRewardMultiplierEvent.getDescription());


                TimedRewardMultiplierEvent.Status status = playtimeRewardMultiplierEvent.getStatus();
                eventMap.putString("status", status != null ? status.getValue() : null);

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

                eventMap.putInt("usedLevels", playtimeRewardMultiplierEvent.getUsedLevels());
                eventsArray.pushMap(eventMap);
            }

            playtimeRewardMultiplierConfigMap.putArray("events", eventsArray);

            eventConfigs.putMap("timeRewardMultiplierConfig", playtimeRewardMultiplierConfigMap);
        }
        if (playtimeInAppPurchaseRewardConfig != null) {
            playtimeInAppPurchaseRewardConfigMap.putString(
                "description", playtimeInAppPurchaseRewardConfig.getDescription()
            );

            if (playtimeInAppPurchaseRewardConfig.getExchangeRate() != null)  {
                playtimeInAppPurchaseRewardConfigMap.putDouble(
                    "exchangeRate", playtimeInAppPurchaseRewardConfig.getExchangeRate()
                );
            } else {
                playtimeInAppPurchaseRewardConfigMap.putNull("exchangeRate");
            }

            if (playtimeInAppPurchaseRewardConfig.getMaxLimitPerCampaignUSD() != null) {
                playtimeInAppPurchaseRewardConfigMap.putDouble(
                    "maxLimitPerCampaignUSD", playtimeInAppPurchaseRewardConfig.getMaxLimitPerCampaignUSD()
                );
            } else {
                playtimeInAppPurchaseRewardConfigMap.putNull("maxLimitPerCampaignUSD");
            }

            if (playtimeInAppPurchaseRewardConfig.getMaxLimitPerCampaignCoins() != null) {
                playtimeInAppPurchaseRewardConfigMap.putDouble(
                    "maxLimitPerCampaignCoins", playtimeInAppPurchaseRewardConfig.getMaxLimitPerCampaignCoins()
                );
            } else {
                playtimeInAppPurchaseRewardConfigMap.putNull("maxLimitPerCampaignCoins");
            }

            WritableMap completedRewardsMap = Arguments.createMap();
            WritableMap pendingRewardsMap = Arguments.createMap();
            PlaytimeInAppPurchaseReward completedRewardsConfig
                = playtimeInAppPurchaseRewardConfig.getCompletedRewards();
            PlaytimeInAppPurchaseReward pendingRewardsConfig
                = playtimeInAppPurchaseRewardConfig.getPendingRewards();

            if (completedRewardsConfig != null) {
                if (completedRewardsConfig.getTotalCoins() != null) {
                    completedRewardsMap.putInt("totalCoins", completedRewardsConfig.getTotalCoins());
                } else {
                    completedRewardsMap.putNull("totalCoins");
                }

                WritableArray completedRewardsEventsArray = createInAppPurchaseReward(completedRewardsConfig);
                completedRewardsMap.putArray("events", completedRewardsEventsArray);
                playtimeInAppPurchaseRewardConfigMap.putMap("completedRewards", completedRewardsMap);
            } else {
                playtimeInAppPurchaseRewardConfigMap.putNull("completedRewards");
            }

            if (pendingRewardsConfig != null) {
                if (pendingRewardsConfig.getTotalCoins() != null) {
                    pendingRewardsMap.putInt("totalCoins", pendingRewardsConfig.getTotalCoins());
                } else {
                    pendingRewardsMap.putNull("totalCoins");
                }
                WritableArray pendingRewardsEventsArray = createInAppPurchaseReward(pendingRewardsConfig);
                pendingRewardsMap.putArray("events", pendingRewardsEventsArray);
                playtimeInAppPurchaseRewardConfigMap.putMap("pendingRewards", pendingRewardsMap);
            } else {
                playtimeInAppPurchaseRewardConfigMap.putNull("pendingRewards");
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
                coinSettingMap.putInt("coins", coinSetting.getCoins());
                coinSettingsArray.pushMap(coinSettingMap);
            }
            streakInfoMap.putArray("coinSettings", coinSettingsArray);
            appMap.putMap("streakInfo", streakInfoMap);
        }

        PlaytimeAdvancePlusConfig advancePlusConfig = app.getAdvancePlusConfig();
        if (advancePlusConfig != null) {
            WritableMap advancePlusConfigMap = Arguments.createMap();
            advancePlusConfigMap.putInt("totalCoins", advancePlusConfig.getTotalCoins());
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

    private WritableArray createInAppPurchaseReward(PlaytimeInAppPurchaseReward rewardsConfig) {
        WritableArray rewardsEventsArray = Arguments.createArray();
        List<PlaytimeInAppPurchaseRewardEvent> rewardsEvents = rewardsConfig.getEvents();

        for (int i = 0; i < rewardsEvents.size(); i++) {
            PlaytimeInAppPurchaseRewardEvent rewardEvent = rewardsEvents.get(i);
            WritableMap eventMap = Arguments.createMap();
            if (rewardEvent.getCoins() != null) {
                eventMap.putInt("coins", rewardEvent.getCoins());
            } else {
                eventMap.putNull("coins");
            }

            if (rewardEvent.getProcessAt() != null) {
                long processAt = rewardEvent.getProcessAt().getTime();
                eventMap.putDouble("processAt", processAt);
            } else {
                eventMap.putNull("processAt");
            }

            if (rewardEvent.getReceivedAt() != null) {
                long receivedAt = rewardEvent.getReceivedAt().getTime();
                eventMap.putDouble("receivedAt", receivedAt);
            } else {
                eventMap.putNull("receivedAt");
            }

            rewardsEventsArray.pushMap(eventMap);
        }

        return rewardsEventsArray;
    }

    private WritableArray createEventsWritableArray(List<PlaytimeAdvancePlusEvent> events) {
        WritableArray eventsArray = Arguments.createArray();

        for (PlaytimeAdvancePlusEvent event : events) {
            WritableMap eventMap = Arguments.createMap();
            eventMap.putString("name", event.getName());
            eventMap.putString("description", event.getDescription());
            eventMap.putInt("coins", event.getCoins());
            eventMap.putInt("timedCoins", event.getTimedCoins());
            eventMap.putInt("timedCoinsDurationInMin", (int) event.getTimedCoinsDuration());
            eventMap.putInt("rewardsCount", event.getRewardsCount());
            eventMap.putInt("completedRewards", event.getCompletedRewards());

            String rewardedAt = event.getRewardedAt();
            if (rewardedAt != null && !rewardedAt.isEmpty()) {
                eventMap.putString("rewardedAt", rewardedAt);
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

        map.putInt("level", level.getLevel());
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
            categoryMap.putString("name", categoryTranslation.getName());
            categoryMap.putString("language", categoryTranslation.getLanguage());
            array.pushMap(categoryMap);
        }
        map.putArray("categoryTranslations", array);
        return map;
    }

    public interface WebViewSupplier {

        FrameLayout getLayoutForWebView();
    }

    public interface PhoneVerificationSupplier {

        FragmentActivity getFragmentActivity();

        Activity getActivity();
    }
}
