package io.adjoe.sdk.reactnative

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap

import io.adjoe.sdk.studio.PlaytimeCampaign
import io.adjoe.sdk.studio.PlaytimeCampaignsResponse
import io.adjoe.sdk.studio.PlaytimeMedia
import io.adjoe.sdk.studio.PlaytimePromotion
import io.adjoe.sdk.studio.PlaytimeEventConfig
import io.adjoe.sdk.studio.PlaytimeRewardAction
import io.adjoe.sdk.studio.PlaytimeRewardActionMultiplier
import io.adjoe.sdk.studio.PlaytimeCashbackConfig
import io.adjoe.sdk.studio.PlaytimeCashbackReward
import io.adjoe.sdk.studio.PlaytimeCashbackRewardEvent

/**
 * Converts Playtime campaign model objects into RN maps/arrays. Migrated from
 * the original Java `StudioUtil` class.
 */
object StudioUtil {

    fun campaignsResponseToReadableMap(response: PlaytimeCampaignsResponse?): ReadableMap? {
        if (response == null) return null
        val responseMap = Arguments.createMap()
        responseMap.putArray(JsonKey.CAMPAIGNS, campaignsToArrayMap(response.campaigns))
        return responseMap
    }

    fun campaignsToArrayMap(campaigns: List<PlaytimeCampaign>?): ReadableArray {
        val campaignsArray = Arguments.createArray()
        if (campaigns == null) return campaignsArray
        for (campaign in campaigns) {
            campaignsArray.pushMap(campaignToReadableMap(campaign))
        }
        return campaignsArray
    }

    fun campaignToReadableMap(campaign: PlaytimeCampaign): ReadableMap {
        val appMap = Arguments.createMap()

        appMap.putString(JsonKey.APP_NAME, campaign.appName)
        appMap.putString(JsonKey.APP_DESCRIPTION, campaign.appDescription)
        appMap.putString(JsonKey.APP_ID, campaign.appID)
        appMap.putString(JsonKey.INSTALLED_AT, campaign.installedAt)
        appMap.putString(JsonKey.UNINSTALLED_AT, campaign.uninstalledAt)
        appMap.putIntOrNull(campaign.rewardingExpiresAfter, JsonKey.REWARDING_EXPIRES_AFTER)
        appMap.putString(JsonKey.REWARDING_EXPIRES_AT, campaign.rewardingExpiresAt)
        appMap.putString(JsonKey.CAMPAIGN_EXPIRES_AT, campaign.campaignExpiresAt)
        appMap.putMap(JsonKey.EVENT_CONFIG, eventConfigToReadableMap(campaign.eventConfig))
        appMap.putString(JsonKey.APP_CATEGORY, campaign.appCategory)
        appMap.putString(JsonKey.CAMPAIGN_TYPE, campaign.campaignType)
        appMap.putIntOrNull(campaign.featuredPosition, JsonKey.FEATURED_POSITION)
        appMap.putFloatOrNull(campaign.score, JsonKey.SCORE)
        appMap.putMap(JsonKey.IMAGE, mediaToReadableMap(campaign.image))
        appMap.putMap(JsonKey.VIDEO, mediaToReadableMap(campaign.video))
        appMap.putString(JsonKey.ICON_IMAGE, campaign.iconImage)
        appMap.putMap(JsonKey.PROMOTION, promotionToReadableMap(campaign.promotion))
        appMap.putBoolean(JsonKey.IS_COMPLETED, campaign.isCompleted)
        appMap.putString(JsonKey.CAMPAIGN_UUID, campaign.campaignUUID)
        appMap.putInt(JsonKey.STATUS, campaign.status.ordinal)
        appMap.putBoolean(JsonKey.IS_CPA, campaign.isCpa)
        appMap.putDouble(JsonKey.CPA, campaign.cpa.toDouble())
        appMap.putString(JsonKey.TOKEN, campaign.token)

        return appMap
    }

    fun mediaToReadableMap(media: PlaytimeMedia?): ReadableMap? {
        if (media == null) return null
        val mediaMap = Arguments.createMap()
        mediaMap.putString(JsonKey.PORTRAIT, media.portrait)
        mediaMap.putString(JsonKey.LANDSCAPE, media.landscape)
        return mediaMap
    }

    fun promotionToReadableMap(promotion: PlaytimePromotion?): ReadableMap? {
        if (promotion == null) return null
        val promotionMap = Arguments.createMap()
        promotionMap.putString(JsonKey.NAME, promotion.name)
        promotionMap.putString(JsonKey.PROMOTION_DESCRIPTION, promotion.promotionDescription)
        promotionMap.putFloatOrNull(promotion.boostFactor, JsonKey.BOOST_FACTOR)
        promotionMap.putString(JsonKey.START_TIME, promotion.startTime)
        promotionMap.putString(JsonKey.END_TIME, promotion.endTime)
        promotionMap.putString(JsonKey.TARGETING_TYPE, promotion.targetingType)
        return promotionMap
    }

    fun eventConfigToReadableMap(eventConfig: PlaytimeEventConfig?): ReadableMap? {
        if (eventConfig == null) return null
        val eventConfigMap = Arguments.createMap()
        eventConfigMap.putArray(JsonKey.SEQUENTIAL_ACTIONS, rewardActionsToArrayMap(eventConfig.sequentialActions))
        eventConfigMap.putArray(JsonKey.BONUS_ACTIONS, rewardActionsToArrayMap(eventConfig.bonusActions))
        eventConfigMap.putArray(JsonKey.TIME_BASED_ACTIONS, rewardActionsToArrayMap(eventConfig.timeBasedActions))
        eventConfigMap.putArray(JsonKey.CPA_ACTIONS, rewardActionsToArrayMap(eventConfig.cpaActions))
        eventConfigMap.putIntOrNull(eventConfig.totalCoinsCollected, JsonKey.TOTAL_COINS_COLLECTED)
        eventConfigMap.putIntOrNull(eventConfig.totalCoinsPossible, JsonKey.TOTAL_COINS_POSSIBLE)
        eventConfigMap.putIntOrNull(eventConfig.secondsToNextLevel, JsonKey.SECONDS_TO_NEXT_LEVEL)
        eventConfigMap.putIntOrNull(eventConfig.totalOriginalCoinsPossible, JsonKey.TOTAL_ORIGINAL_COINS_POSSIBLE)
        eventConfigMap.putMap(JsonKey.CASH_BACK_REWARD, cashBackRewardConfigToReadableMap(eventConfig.cashbackReward))
        eventConfigMap.putArray(JsonKey.MULTIPLIERS_ACTIONS, multipliersActionsToArrayMap(eventConfig.multipliersActions))
        eventConfigMap.putIntOrNull(eventConfig.totalSequentialCoins, JsonKey.TOTAL_SEQUENTIAL_COINS)
        eventConfigMap.putIntOrNull(eventConfig.totalOriginalSequentialCoins, JsonKey.TOTAL_ORIGINAL_SEQUENTIAL_COINS)
        eventConfigMap.putIntOrNull(eventConfig.totalBonusCoins, JsonKey.TOTAL_BONUS_COINS)
        eventConfigMap.putIntOrNull(eventConfig.totalOriginalBonusCoins, JsonKey.TOTAL_ORIGINAL_BONUS_COINS)
        return eventConfigMap
    }

    fun rewardActionsToArrayMap(actions: List<PlaytimeRewardAction>?): ReadableArray {
        val actionsArray = Arguments.createArray()
        if (actions == null) return actionsArray
        for (i in actions.indices) {
            val actionMap = Arguments.createMap()
            val action = actions[i]
            actionMap.putString(JsonKey.NAME, action.name)
            actionMap.putString(JsonKey.TASK_DESCRIPTION, action.taskDescription)
            actionMap.putIntOrNull(action.level, JsonKey.LEVEL)
            actionMap.putString(JsonKey.TASK_TYPE, action.taskType)
            actionMap.putIntOrNull(action.playDuration, JsonKey.PLAY_DURATION)
            actionMap.putInt(JsonKey.AMOUNT, action.amount)
            actionMap.putString(JsonKey.REWARDED_AT, action.rewardedAt)
            actionMap.putIntOrNull(action.rewardsCount, JsonKey.REWARDS_COUNT)
            actionMap.putIntOrNull(action.completedRewards, JsonKey.COMPLETED_REWARDS)
            actionMap.putIntOrNull(action.timedCoinsDuration, JsonKey.TIMED_COINS_DURATION)
            actionMap.putIntOrNull(action.timedCoins, JsonKey.TIMED_COINS)
            actionMap.putIntOrNull(action.originalCoins, JsonKey.ORIGINAL_COINS)
            actionMap.putBooleanOrNull(action.isTimed, JsonKey.IS_TIMED)
            actionMap.putBooleanOrNull(action.isRewardedForPromotion, JsonKey.IS_REWARDED_FOR_PROMOTION)
            actionMap.putString(JsonKey.BOOSTER_EXPIRES_AT, action.boosterExpiresAt)
            actionsArray.pushMap(actionMap)
        }
        return actionsArray
    }

    fun multipliersActionsToArrayMap(actions: List<PlaytimeRewardActionMultiplier>?): ReadableArray {
        val actionsArray = Arguments.createArray()
        if (actions == null) return actionsArray
        for (i in actions.indices) {
            val actionMap = Arguments.createMap()
            val action = actions[i]
            actionMap.putString(JsonKey.EVENT_NAME, action.eventName)
            actionMap.putString(JsonKey.EVENT_DESCRIPTION, action.eventDescription)
            actionMap.putIntOrNull(action.multiplierFactorPercentage, JsonKey.MULTIPLIER_FACTOR_PERCENTAGE)
            actionMap.putIntOrNull(action.multiplierLevels, JsonKey.MULTIPLIER_LEVELS)
            actionMap.putString(JsonKey.STATUS, action.status)
            actionMap.putIntOrNull(action.usedLevels, JsonKey.USED_LEVELS)
            actionsArray.pushMap(actionMap)
        }
        return actionsArray
    }

    fun cashBackRewardConfigToReadableMap(cashbackRewardConfig: PlaytimeCashbackConfig?): ReadableMap? {
        if (cashbackRewardConfig == null) return null
        val cashbackRewardMap = Arguments.createMap()
        cashbackRewardMap.putFloatOrNull(cashbackRewardConfig.exchangeRate, JsonKey.EXCHANGE_RATE)
        cashbackRewardMap.putString(JsonKey.CASHBACK_DESCRIPTION, cashbackRewardConfig.cashbackDescription)
        cashbackRewardMap.putFloatOrNull(cashbackRewardConfig.maxLimitPerCampaignUSD, JsonKey.MAX_LIMIT_PER_CAMPAIGN_USD)
        cashbackRewardMap.putFloatOrNull(cashbackRewardConfig.maxLimitPerCampaignCoins, JsonKey.MAX_LIMIT_PER_CAMPAIGN_COINS)
        cashbackRewardMap.putMap(JsonKey.COMPLETED_REWARDS, cashBackRewardToReadableMap(cashbackRewardConfig.completedRewards))
        cashbackRewardMap.putMap(JsonKey.PENDING_REWARDS, cashBackRewardToReadableMap(cashbackRewardConfig.pendingRewards))
        return cashbackRewardMap
    }

    fun cashBackRewardToReadableMap(rewards: PlaytimeCashbackReward?): ReadableMap? {
        if (rewards == null) return null
        val cashbackRewardMap = Arguments.createMap()
        cashbackRewardMap.putIntOrNull(rewards.totalCoins, JsonKey.TOTAL_COINS)
        cashbackRewardMap.putArray(JsonKey.EVENTS, cashbackRewardEventToArrayMap(rewards.events))
        return cashbackRewardMap
    }

    fun cashbackRewardEventToArrayMap(events: List<PlaytimeCashbackRewardEvent>?): ReadableArray {
        val eventsArray = Arguments.createArray()
        if (events == null) return eventsArray
        for (i in events.indices) {
            val eventMap = Arguments.createMap()
            val event = events[i]
            eventMap.putIntOrNull(event.coins, JsonKey.COINS)
            eventMap.putString(JsonKey.PROCESS_AT, event.processAt)
            eventMap.putString(JsonKey.RECEIVED_AT, event.receivedAt)
            eventsArray.pushMap(eventMap)
        }
        return eventsArray
    }

    object JsonKey {
        const val CAMPAIGNS = "campaigns"

        const val APP_NAME = "appName"
        const val APP_DESCRIPTION = "appDescription"
        const val APP_ID = "appID"
        const val INSTALLED_AT = "installedAt"
        const val UNINSTALLED_AT = "uninstalledAt"
        const val REWARDING_EXPIRES_AFTER = "rewardingExpiresAfter"
        const val REWARDING_EXPIRES_AT = "rewardingExpiresAt"
        const val CAMPAIGN_EXPIRES_AT = "campaignExpiresAt"
        const val EVENT_CONFIG = "eventConfig"
        const val APP_CATEGORY = "appCategory"
        const val CAMPAIGN_TYPE = "campaignType"
        const val FEATURED_POSITION = "featuredPosition"
        const val SCORE = "score"
        const val IMAGE = "image"
        const val VIDEO = "video"
        const val ICON_IMAGE = "iconImage"
        const val PROMOTION = "promotion"
        const val IS_COMPLETED = "isCompleted"
        const val CAMPAIGN_UUID = "campaignUUID"
        const val STATUS = "status"
        const val IS_CPA = "isCpa"
        const val CPA = "cpa"
        const val TOKEN = "token"

        const val PORTRAIT = "portrait"
        const val LANDSCAPE = "landscape"

        const val NAME = "name"
        const val PROMOTION_DESCRIPTION = "promotionDescription"
        const val BOOST_FACTOR = "boostFactor"
        const val START_TIME = "startTime"
        const val END_TIME = "endTime"
        const val TARGETING_TYPE = "targetingType"

        const val SEQUENTIAL_ACTIONS = "sequentialActions"
        const val BONUS_ACTIONS = "bonusActions"
        const val TIME_BASED_ACTIONS = "timeBasedActions"
        const val CPA_ACTIONS = "cpaActions"
        const val TOTAL_COINS_COLLECTED = "totalCoinsCollected"
        const val TOTAL_COINS_POSSIBLE = "totalCoinsPossible"
        const val SECONDS_TO_NEXT_LEVEL = "secondsToNextLevel"
        const val TOTAL_ORIGINAL_COINS_POSSIBLE = "totalOriginalCoinsPossible"
        const val CASH_BACK_REWARD = "cashbackReward"
        const val MULTIPLIERS_ACTIONS = "multipliersActions"
        const val TOTAL_SEQUENTIAL_COINS = "totalSequentialCoins"
        const val TOTAL_ORIGINAL_SEQUENTIAL_COINS = "totalOriginalSequentialCoins"
        const val TOTAL_BONUS_COINS = "totalBonusCoins"
        const val TOTAL_ORIGINAL_BONUS_COINS = "totalOriginalBonusCoins"

        const val TASK_DESCRIPTION = "taskDescription"
        const val LEVEL = "level"
        const val TASK_TYPE = "taskType"
        const val PLAY_DURATION = "playDuration"
        const val AMOUNT = "amount"
        const val REWARDED_AT = "rewardedAt"
        const val REWARDS_COUNT = "rewardsCount"
        const val COMPLETED_REWARDS = "completedRewards"
        const val TIMED_COINS_DURATION = "timedCoinsDuration"
        const val TIMED_COINS = "timedCoins"
        const val ORIGINAL_COINS = "originalCoins"
        const val IS_TIMED = "isTimed"
        const val IS_REWARDED_FOR_PROMOTION = "isRewardedForPromotion"
        const val BOOSTER_EXPIRES_AT = "boosterExpiresAt"

        const val EVENT_NAME = "eventName"
        const val EVENT_DESCRIPTION = "eventDescription"
        const val MULTIPLIER_FACTOR_PERCENTAGE = "multiplierFactorPercentage"
        const val MULTIPLIER_LEVELS = "multiplierLevels"
        const val USED_LEVELS = "usedLevels"

        const val EXCHANGE_RATE = "exchangeRate"
        const val CASHBACK_DESCRIPTION = "cashbackDescription"
        const val MAX_LIMIT_PER_CAMPAIGN_USD = "maxLimitPerCampaignUSD"
        const val MAX_LIMIT_PER_CAMPAIGN_COINS = "maxLimitPerCampaignCoins"
        const val PENDING_REWARDS = "pendingRewards"

        const val TOTAL_COINS = "totalCoins"
        const val EVENTS = "events"
        const val COINS = "coins"
        const val PROCESS_AT = "processAt"
        const val RECEIVED_AT = "receivedAt"
    }
}
