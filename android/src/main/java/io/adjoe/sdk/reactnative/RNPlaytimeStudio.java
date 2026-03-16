package io.adjoe.sdk.reactnative;

import static io.adjoe.sdk.reactnative.Util.campaignToReadableMap;
import static io.adjoe.sdk.reactnative.Util.constructOptionsFrom;
import static io.adjoe.sdk.reactnative.Util.extractTokens;
import static io.adjoe.sdk.reactnative.Util.permissionToReadableMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
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
import io.adjoe.sdk.studio.PlaytimeCampaignsResponse;
import io.adjoe.sdk.studio.PlaytimeOpenInstalledCampaignListener;
import io.adjoe.sdk.studio.PlaytimeOpenStoreListener;
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
        String campaignUUID = campaignMap.getString(Constants.JsonKey.CAMPAIGN_UUID);

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
        String campaignUUID = campaignMap.getString(Constants.JsonKey.CAMPAIGN_UUID);

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
                result.putArray(Constants.JsonKey.CAMPAIGNS ,campaigns);
                promise.resolve(result);
            }

            @Override
            public void onError(@NonNull PlaytimeResponseError playtimeResponseError) {
                promise.reject("Get Campaigns Error", playtimeResponseError.getError().getMessage());
            }
        };
    }
}
