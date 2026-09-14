package io.adjoe.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = PlaytimeStudioImpl.NAME)
class RNPlaytimeStudio(
    reactContext: ReactApplicationContext
) : NativePlaytimeStudioSpec(reactContext) {

    private val implementation = PlaytimeStudioImpl(reactContext)

    override fun getName(): String = PlaytimeStudioImpl.NAME

    override fun updateTosConsent(action: Double, promise: Promise) =
        implementation.updateTosConsent(action, promise)

    override fun getCampaigns(campaignOptionsMap: ReadableMap?, promise: Promise) =
        implementation.getCampaigns(campaignOptionsMap, promise)

    override fun getOfferDetails(tokens: ReadableArray?, campaignOptionsMap: ReadableMap?, promise: Promise) =
        implementation.getOfferDetails(tokens, campaignOptionsMap, promise)

    override fun getInstalledCampaigns(optionsMap: ReadableMap?, promise: Promise) =
        implementation.getInstalledCampaigns(optionsMap, promise)

    override fun openInStore(campaignMap: ReadableMap?, useInlineInstall: Boolean, promise: Promise) =
        implementation.openInStore(campaignMap, useInlineInstall, promise)

    override fun openInstalledCampaign(campaignMap: ReadableMap?, promise: Promise) =
        implementation.openInstalledCampaign(campaignMap, promise)

    override fun openChatbot(campaignMap: ReadableMap?, promise: Promise) =
        implementation.openChatbot(campaignMap, promise)

    override fun resetRewardsConnect(promise: Promise) =
        implementation.resetRewardsConnect(promise)

    override fun executeCampaignImpression(campaignMap: ReadableMap?, impressionType: Double, promise: Promise) =
        implementation.executeCampaignImpression(campaignMap, impressionType, promise)

    override fun executeOfferImpression(token: String, appID: String, promise: Promise) =
        implementation.executeOfferImpression(token, appID, promise)

    override fun registerRewardsConnect(promise: Promise) =
        implementation.registerRewardsConnect(promise)

    override fun showCampaignDetails(campaignMap: ReadableMap?, promise: Promise) =
        implementation.showCampaignDetails(campaignMap, promise)

    override fun showOfferDetails(token: String, appID: String, promise: Promise) =
        implementation.showOfferDetails(token, appID, promise)

    override fun showInstalledCampaigns(promise: Promise) =
        implementation.showInstalledCampaigns(promise)

    override fun showPermissionsPrompt(promise: Promise) =
        implementation.showPermissionsPrompt(promise)

    companion object {
        const val NAME = PlaytimeStudioImpl.NAME
    }
}
