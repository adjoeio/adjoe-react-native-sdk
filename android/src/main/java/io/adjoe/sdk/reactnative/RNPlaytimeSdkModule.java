package io.adjoe.sdk.reactnative;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentActivity;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.adjoe.sdk.Playtime;
import io.adjoe.sdk.PlaytimeException;
import io.adjoe.sdk.PlaytimeStatus;
import io.adjoe.sdk.PlaytimeInitialisationListener;
import io.adjoe.sdk.PlaytimeOptions;
import io.adjoe.sdk.PlaytimeOptionsListener;
import io.adjoe.sdk.PlaytimeParams;
import io.adjoe.sdk.custom.PlaytimeCustom;
import io.adjoe.sdk.internal.PlaytimePartnerApp;
import io.adjoe.sdk.PlaytimeStatusDetails;
import io.adjoe.sdk.PlaytimeExtensions;
import io.adjoe.sdk.PlaytimeGender;
import io.adjoe.sdk.PlaytimeUserProfile;
import io.adjoe.sdk.studio.PlaytimePermissionsResponse;
import io.adjoe.sdk.PlaytimeCampaignsState;

@SuppressWarnings("unused")
public class RNPlaytimeSdkModule extends ReactContextBaseJavaModule {

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
            PlaytimeOptions options = BasicUtil.constructOptionsFrom(optionsMap);

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
            PlaytimeParams params = BasicUtil.constructPlaytimeParams(configMap);
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
            PlaytimeOptions options = BasicUtil.constructOptionsFrom(configMap);
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
        PlaytimeParams params = BasicUtil.constructPlaytimeParams(paramsMap);
        Playtime.setUAParams(reactContext, params);
        promise.resolve(null);
    }

    @ReactMethod
    public void setPlaytimeOptions(ReadableMap paramsMap, final Promise promise) {
        PlaytimeOptions options = BasicUtil.constructOptionsFrom(paramsMap);
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

    @ReactMethod
    public void teardown(Promise promise) {
        try {
            Playtime.teardown(reactContext);
            promise.resolve(null);
        } catch (Exception e) {
            promise.reject("Teardown Error", e.getMessage());
        }
    }

    /* -----------------------------
       BASIC INTEGRATION METHODS END
       ----------------------------- */

    /* -----------------------------
     ADVANCED INTEGRATION METHODS START
       ----------------------------- */

    @ReactMethod
    public void requestPartnerApps(ReadableMap paramsMap, final Promise promise) {
        AdvancedIntegrationUtil.requestPartnerApps(reactContext, webViewSupplier, PARTNER_APPS,
                paramsMap, promise);
    }

    @ReactMethod
    public void getTestGroup(final Promise promise) {
        AdvancedIntegrationUtil.getTestGroup(reactContext, promise);
    }

    @ReactMethod
    public void requestPartnerAppsWithOptions(ReadableMap optionsMap, final Promise promise) {
        AdvancedIntegrationUtil.requestPartnerAppsWithOptions(reactContext, webViewSupplier,
                PARTNER_APPS, optionsMap, promise);
    }

    @ReactMethod
    public void executePartnerAppClick(final String packageName, ReadableMap map,
            final Promise promise) {
        AdvancedIntegrationUtil.executePartnerAppClick(reactContext, webViewSupplier, PARTNER_APPS,
                packageName, map, promise);
    }

    @ReactMethod
    public void executeShowCampaignDetailClick(final String packageName, final Promise promise) {
        AdvancedIntegrationUtil.executeShowCampaignDetailClick(reactContext, PARTNER_APPS,
                packageName, promise);
    }

    @ReactMethod
    public void launchPartnerApp(final String packageName, final Promise promise) {
        AdvancedIntegrationUtil.launchPartnerApp(reactContext, PARTNER_APPS, packageName, promise);
    }

    @ReactMethod
    public void executePartnerAppView(final String packageName, ReadableMap map,
            final Promise promise) {
        AdvancedIntegrationUtil.executePartnerAppView(reactContext, webViewSupplier, PARTNER_APPS,
                packageName, map, promise);
    }

    @ReactMethod
    public void getRemainingTimeForPartnerApp(String packageName, Promise promise) {
        AdvancedIntegrationUtil.getRemainingTimeForPartnerApp(reactContext, PARTNER_APPS,
                packageName, promise);
    }

    @ReactMethod
    public void getNextRewardLevelForPartnerApp(String packageName, Promise promise) {
        AdvancedIntegrationUtil.getNextRewardLevelForPartnerApp(reactContext, PARTNER_APPS,
                packageName, promise);
    }

    @ReactMethod
    public void requestInstalledPartnerApps(ReadableMap map, final Promise promise) {
        AdvancedIntegrationUtil.requestInstalledPartnerApps(reactContext, PARTNER_APPS,
                map, promise);
    }

    @ReactMethod
    public void getInstallDate(String packageName, Promise promise) {
        AdvancedIntegrationUtil.getInstallDate(PARTNER_APPS, packageName, promise);
    }

    @ReactMethod
    public void _a(boolean a, final Promise promise) {
        AdvancedIntegrationUtil._a(a, promise);
    }

    @ReactMethod
    public void setTosAccepted(final Promise promise) {
        AdvancedIntegrationUtil.setTosAccepted(reactContext, promise);
    }

    @ReactMethod
    public void setUsagePermissionAccepted(final Promise promise) {
        AdvancedIntegrationUtil.setUsagePermissionAccepted(reactContext, promise);
    }

    @ReactMethod
    public void showUsagePermissionScreen(final Promise promise) {
        AdvancedIntegrationUtil.showUsagePermissionScreen(reactContext, promise);
    }

    @ReactMethod
    public void sendEvent(int event, String extra, ReadableMap map) {
        AdvancedIntegrationUtil.sendEvent(reactContext, event, extra, map);
    }

    @ReactMethod
    public void isBoostedEvent(String packageName, ReadableMap event, final Promise promise) {
        AdvancedIntegrationUtil.isBoostedEvent(PARTNER_APPS, packageName, event, promise);
    }

    @ReactMethod
    public void isBoostedEventExpired(String packageName, ReadableMap event, final Promise promise) {
        AdvancedIntegrationUtil.isBoostedEventExpired(PARTNER_APPS, packageName, event, promise);
    }

    @ReactMethod
    public void timeToExpireBoostedEventInSeconds(String packageName, ReadableMap event, final Promise promise) {
        AdvancedIntegrationUtil.timeToExpireBoostedEventInSeconds(PARTNER_APPS, packageName,
                event, promise);
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
        ReadableMap mapStatus = BasicUtil.statusToReadableMap(status);
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
        AdvancedIntegrationUtil.faceVerification(reactContext, promise);
    }

    @ReactMethod
    public void faceVerificationStatus(final Promise promise) {
        AdvancedIntegrationUtil.faceVerificationStatus(reactContext, promise);
    }

    /* -----------------------------
            FRAUD METHODS END
    ----------------------------- */

    public interface WebViewSupplier {

        FrameLayout getLayoutForWebView();
    }

    public interface PhoneVerificationSupplier {

        FragmentActivity getFragmentActivity();

        Activity getActivity();
    }


    static class BasicUtil {

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

        static ReadableMap statusToReadableMap(PlaytimeStatus status) {
            WritableMap statusMap = Arguments.createMap();
            statusMap.putBoolean(BasicUtil.JsonKey.IS_INITIALIZED, status.isInitialized());
            statusMap.putMap(BasicUtil.JsonKey.DETAILS, statusDetailsToReadableMap(status.getDetails()));
            return statusMap;
        }

        private static WritableArray campaignsStateToArray(List<PlaytimeCampaignsState> campaignStates) {
            WritableArray array = Arguments.createArray();
            if (campaignStates != null) {
                for (PlaytimeCampaignsState state : campaignStates) {
                    array.pushString(campaignStateToString(state));
                }
            }
            return array;
        }

        private static String campaignStateToString(PlaytimeCampaignsState state) {
            switch (state) {
                case BLOCKED:
                    return "BLOCKED";
                case VPN_DETECTED:
                    return "VPN_DETECTED";
                case GEO_MISMATCH:
                    return "GEO_MISMATCH";
                default:
                    return "READY";
            }
        }

        static ReadableMap statusDetailsToReadableMap(PlaytimeStatusDetails details) {
            WritableMap detailsMap = Arguments.createMap();
            detailsMap.putBoolean(BasicUtil.JsonKey.IS_FRAUD, details.isFraud());
            detailsMap.putBoolean(BasicUtil.JsonKey.CAMPAIGNS_AVAILABLE, details.getCampaignsAvailable());
            detailsMap.putArray(BasicUtil.JsonKey.CAMPAIGNS_STATE, campaignsStateToArray(details.getCampaignsState()));
            Integer testGroup = details.getTestGroup();
            if (testGroup != null) {
                detailsMap.putInt(BasicUtil.JsonKey.TEST_GROUP, testGroup);
            } else {
                detailsMap.putNull(BasicUtil.JsonKey.TEST_GROUP);
            }
            return detailsMap;
        }

        static ReadableMap permissionToReadableMap(PlaytimePermissionsResponse response) {
            WritableMap permissions = Arguments.createMap();

            WritableMap result = Arguments.createMap();
            result.putBoolean("isUsagePermissionAccepted", response.getPermissions().isUsagePermissionAccepted());
            result.putBoolean("isTOSAccepted",  response.getPermissions().isTOSAccepted());

            permissions.putMap("permissions", result);

            return permissions;
        }

        static class JsonKey {
            static final String IS_INITIALIZED = "isInitialized";
            static final String DETAILS = "details";
            static final String IS_FRAUD = "isFraud";

            static final String CAMPAIGNS_AVAILABLE = "campaignsAvailable";
            static final String CAMPAIGNS_STATE = "campaignsState";
            static final String TEST_GROUP = "testGroup";
        }
    }
}
