package io.adjoe.sdk.reactnative

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap

import io.adjoe.sdk.studio.PlaytimeCampaign
import io.adjoe.sdk.studio.PlaytimeCampaignsListener
import io.adjoe.sdk.studio.PlaytimeDeeplinkListener
import io.adjoe.sdk.studio.PlaytimeCampaignsResponse
import io.adjoe.sdk.studio.PlaytimeOpenInstalledCampaignListener
import io.adjoe.sdk.studio.PlaytimeOpenStoreListener
import io.adjoe.sdk.studio.PlaytimeImpressionType
import io.adjoe.sdk.studio.PlaytimeExecuteImpressionListener
import io.adjoe.sdk.studio.PlaytimePermissionsListener
import io.adjoe.sdk.studio.PlaytimeTosListener
import io.adjoe.sdk.studio.TosConsentAction
import io.adjoe.sdk.connect.RewardsConnectRegistrationListener
import io.adjoe.sdk.connect.RewardsConnectResetListener
import io.adjoe.sdk.PlaytimeException
import io.adjoe.sdk.studio.PlaytimeStudio

import io.adjoe.sdk.reactnative.BasicUtil.constructCampaignOptionsFrom
import io.adjoe.sdk.reactnative.BasicUtil.constructInstalledCampaignOptionsFrom

class PlaytimeStudioImpl(
    private val reactContext: ReactApplicationContext
) {

    private val cache: MutableMap<String?, PlaytimeCampaign> = HashMap()

    private fun cacheKeyOf(campaign: PlaytimeCampaign): String? =
        if (campaign.token.isNullOrEmpty()) campaign.campaignUUID else campaign.token

    private fun cacheKeyOf(campaignMap: ReadableMap?): String? {
        val token = campaignMap?.getString(StudioUtil.JsonKey.TOKEN)
        return if (token.isNullOrEmpty()) {
            campaignMap?.getString(StudioUtil.JsonKey.CAMPAIGN_UUID)
        } else {
            token
        }
    }

    fun updateTosConsent(action: Double, promise: Promise) {
        val consentAction =
            if (action.toInt() == 1) TosConsentAction.WITHDRAW else TosConsentAction.ACCEPT
        PlaytimeStudio.updateTosConsent(consentAction, object : PlaytimeTosListener {
            override fun onUpdated() {
                promise.resolve(null)
            }

            override fun onError(error: PlaytimeException) {
                ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(error))
            }
        })
    }

    fun getCampaigns(campaignOptionsMap: ReadableMap?, promise: Promise) {
        val options = constructCampaignOptionsFrom(campaignOptionsMap)
        PlaytimeStudio.getCampaigns(options, createCampaignsListener(promise))
    }

    fun getOfferDetails(tokens: ReadableArray?, campaignOptionsMap: ReadableMap?, promise: Promise) {
        val options = constructCampaignOptionsFrom(campaignOptionsMap)
        val tokenList = ArrayList<String>()
        if (tokens != null) {
            for (i in 0 until tokens.size()) {
                tokens.getString(i)?.let { tokenList.add(it) }
            }
        }
        PlaytimeStudio.getOfferDetails(tokenList, options, createCampaignsListener(promise))
    }

    fun getInstalledCampaigns(optionsMap: ReadableMap?, promise: Promise) {
        val options = constructInstalledCampaignOptionsFrom(optionsMap)
        PlaytimeStudio.getInstalledCampaigns(options, createCampaignsListener(promise))
    }

    fun openInStore(campaignMap: ReadableMap?, useInlineInstall: Boolean, promise: Promise) {
        val cacheKey = cacheKeyOf(campaignMap)
        val campaign = cacheKey?.let { cache[it] }

        if (cacheKey == null || campaign == null) {
            ErrorsUtil.reject(promise, ErrorsUtil.campaignIsNullError())
            return
        }

        PlaytimeStudio.openInStore(campaign, useInlineInstall, object : PlaytimeOpenStoreListener {
            override fun onFinished() {
                promise.resolve(null)
            }

            override fun onError(error: PlaytimeException) {
                ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(error))
            }

            override fun onAlreadyClicking() {
                ErrorsUtil.reject(
                    promise,
                    ErrorsUtil.getFunctionError("openInStore", "Request is already in progress")
                )
            }
        })
    }

    fun openInstalledCampaign(campaignMap: ReadableMap?, promise: Promise) {
        val cacheKey = cacheKeyOf(campaignMap)
        val campaign = cacheKey?.let { cache[it] }

        if (campaign == null) {
            ErrorsUtil.reject(promise, ErrorsUtil.campaignIsNullError())
            return
        }

        PlaytimeStudio.openInstalledCampaign(campaign, object : PlaytimeOpenInstalledCampaignListener {
            override fun onOpened() {
                promise.resolve(null)
            }

            override fun onError(error: PlaytimeException) {
                ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(error))
            }
        })
    }

    fun openChatbot(campaignMap: ReadableMap?, promise: Promise) {
        try {
            val cacheKey = cacheKeyOf(campaignMap)
            val campaign = cacheKey?.let { cache[it] }

            val activity = reactContext.getCurrentActivity()
            if (activity != null) {
                PlaytimeStudio.openChatbot(activity, campaign, createDeeplinkListener(promise))
            } else {
                ErrorsUtil.reject(promise, ErrorsUtil.getActivityIsNullError())
            }
        } catch (e: Exception) {
            ErrorsUtil.reject(promise, e)
        }
    }

    fun resetRewardsConnect(promise: Promise) {
        PlaytimeStudio.resetRewardsConnect(object : RewardsConnectResetListener {
            override fun onSuccess() {
                promise.resolve(null)
            }

            override fun onFailure(registrationError: PlaytimeException) {
                ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(registrationError))
            }
        })
    }

    fun executeCampaignImpression(campaignMap: ReadableMap?, impressionType: Double, promise: Promise) {
        val cacheKey = cacheKeyOf(campaignMap)
        val campaign = cacheKey?.let { cache[it] }

        val playtimeImpressionType =
            if (impressionType.toInt() == 1) PlaytimeImpressionType.ENGAGED else PlaytimeImpressionType.DEFAULT

        if (cacheKey == null || campaign == null) {
            ErrorsUtil.reject(promise, ErrorsUtil.campaignIsNullError())
            return
        }

        PlaytimeStudio.executeCampaignImpression(campaign, playtimeImpressionType,
            object : PlaytimeExecuteImpressionListener {
                override fun onFinished() {
                    promise.resolve(null)
                }

                override fun onError(error: PlaytimeException) {
                    ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(error))
                }

                override fun onAlreadyEngaging() {
                    ErrorsUtil.reject(
                        promise,
                        ErrorsUtil.getFunctionError("executeCampaignImpression", "Request is already in progress")
                    )
                }
            })
    }

    fun executeOfferImpression(token: String, appID: String, promise: Promise) {
        PlaytimeStudio.executeOfferImpression(token, appID, object : PlaytimeExecuteImpressionListener {
            override fun onFinished() {
                promise.resolve(null)
            }

            override fun onError(error: PlaytimeException) {
                ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(error))
            }

            override fun onAlreadyEngaging() {
                ErrorsUtil.reject(
                    promise,
                    ErrorsUtil.getFunctionError("executeOfferImpression", "Request is already in progress")
                )
            }
        })
    }

    fun registerRewardsConnect(promise: Promise) {
        val activity = reactContext.getCurrentActivity()
        if (activity != null) {
            PlaytimeStudio.registerRewardsConnect(activity, object : RewardsConnectRegistrationListener {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onFailure(registrationError: PlaytimeException) {
                    ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(registrationError))
                }
            })
        } else {
            ErrorsUtil.reject(promise, ErrorsUtil.getActivityIsNullError())
        }
    }

    fun showCampaignDetails(campaignMap: ReadableMap?, promise: Promise) {
        val cacheKey = cacheKeyOf(campaignMap)
        val campaign = cacheKey?.let { cache[it] }

        if (campaign == null) {
            ErrorsUtil.reject(promise, ErrorsUtil.campaignIsNullError())
            return
        }

        val activity = reactContext.getCurrentActivity()
        if (activity != null) {
            PlaytimeStudio.showCampaignDetails(activity, campaign, createDeeplinkListener(promise))
        } else {
            ErrorsUtil.reject(promise, ErrorsUtil.getActivityIsNullError())
        }
    }

    fun showOfferDetails(token: String, appID: String, promise: Promise) {
        val activity = reactContext.getCurrentActivity()
        if (activity != null) {
            PlaytimeStudio.showOfferDetails(activity, token, appID, createDeeplinkListener(promise))
        } else {
            ErrorsUtil.reject(promise, ErrorsUtil.getActivityIsNullError())
        }
    }

    fun showInstalledCampaigns(promise: Promise) {
        val activity = reactContext.getCurrentActivity()
        if (activity != null) {
            PlaytimeStudio.showInstalledCampaigns(activity, createDeeplinkListener(promise))
        } else {
            ErrorsUtil.reject(promise, ErrorsUtil.getActivityIsNullError())
        }
    }

    fun showPermissionsPrompt(promise: Promise) {
        try {
            val activity = reactContext.getCurrentActivity()
            if (activity != null) {
                PlaytimeStudio.showPermissionsPrompt(activity, object : PlaytimePermissionsListener {
                    override fun onReceived() {
                        promise.resolve(null)
                    }

                    override fun onError(error: PlaytimeException) {
                        ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(error))
                    }
                })
            } else {
                ErrorsUtil.reject(promise, ErrorsUtil.getActivityIsNullError())
            }
        } catch (e: Exception) {
            ErrorsUtil.reject(promise, e)
        }
    }

    private fun createCampaignsListener(promise: Promise): PlaytimeCampaignsListener {
        return object : PlaytimeCampaignsListener {
            override fun onReceived(response: PlaytimeCampaignsResponse) {
                val campaigns = Arguments.createArray()
                for (campaign in response.campaigns) {
                    cache[cacheKeyOf(campaign)] = campaign
                    campaigns.pushMap(StudioUtil.campaignToReadableMap(campaign))
                }
                val result = Arguments.createMap()
                result.putArray(StudioUtil.JsonKey.CAMPAIGNS, campaigns)
                promise.resolve(result)
            }

            override fun onError(error: PlaytimeException) {
                ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(error))
            }
        }
    }

    private fun createDeeplinkListener(promise: Promise): PlaytimeDeeplinkListener {
        return object : PlaytimeDeeplinkListener {
            override fun onOpened() {
                promise.resolve(null)
            }

            override fun onError(error: PlaytimeException) {
                ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(error))
            }
        }
    }

    companion object {
        const val NAME = "PlaytimeStudio"
    }
}
