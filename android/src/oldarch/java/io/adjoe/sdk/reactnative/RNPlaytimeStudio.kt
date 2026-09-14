package io.adjoe.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = PlaytimeStudioImpl.NAME)
class RNPlaytimeStudio(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    private val implementation = PlaytimeStudioImpl(reactContext)

    override fun getName(): String = PlaytimeStudioImpl.NAME

    @ReactMethod
    fun updateTosConsent(action: Double, promise: Promise) =
        implementation.updateTosConsent(action, promise)

    @ReactMethod
    fun getCampaigns(campaignOptionsMap: ReadableMap?, promise: Promise) =
        implementation.getCampaigns(campaignOptionsMap, promise)

    @ReactMethod
    fun getOfferDetails(tokens: ReadableArray?, campaignOptionsMap: ReadableMap?, promise: Promise) =
        implementation.getOfferDetails(tokens, campaignOptionsMap, promise)

    @ReactMethod
    fun getInstalledCampaigns(optionsMap: ReadableMap?, promise: Promise) =
        implementation.getInstalledCampaigns(optionsMap, promise)

    @ReactMethod
    fun openInStore(campaignMap: ReadableMap?, useInlineInstall: Boolean, promise: Promise) =
        implementation.openInStore(campaignMap, useInlineInstall, promise)

    @ReactMethod
    fun openInstalledCampaign(campaignMap: ReadableMap?, promise: Promise) =
        implementation.openInstalledCampaign(campaignMap, promise)

    @ReactMethod
    fun openChatbot(campaignMap: ReadableMap?, promise: Promise) =
        implementation.openChatbot(campaignMap, promise)

    @ReactMethod
    fun resetRewardsConnect(promise: Promise) =
        implementation.resetRewardsConnect(promise)

    @ReactMethod
    fun executeCampaignImpression(campaignMap: ReadableMap?, impressionType: Double, promise: Promise) =
        implementation.executeCampaignImpression(campaignMap, impressionType, promise)

    @ReactMethod
    fun executeOfferImpression(token: String, appID: String, promise: Promise) =
        implementation.executeOfferImpression(token, appID, promise)

    @ReactMethod
    fun registerRewardsConnect(promise: Promise) =
        implementation.registerRewardsConnect(promise)

    @ReactMethod
    fun showCampaignDetails(campaignMap: ReadableMap?, promise: Promise) =
        implementation.showCampaignDetails(campaignMap, promise)

    @ReactMethod
    fun showOfferDetails(token: String, appID: String, promise: Promise) =
        implementation.showOfferDetails(token, appID, promise)

    @ReactMethod
    fun showInstalledCampaigns(promise: Promise) =
        implementation.showInstalledCampaigns(promise)

    @ReactMethod
    fun showPermissionsPrompt(promise: Promise) =
        implementation.showPermissionsPrompt(promise)

    companion object {
        const val NAME = PlaytimeStudioImpl.NAME
    }
}
