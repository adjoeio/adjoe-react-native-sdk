package io.adjoe.sdk.reactnative

import android.os.Build
import android.text.TextUtils

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

import io.adjoe.sdk.PlaytimeStatus
import io.adjoe.sdk.PlaytimeStatusDetails
import io.adjoe.sdk.PlaytimeExtensions
import io.adjoe.sdk.PlaytimeGender
import io.adjoe.sdk.PlaytimeUserProfile
import io.adjoe.sdk.PlaytimeCampaignsState
import io.adjoe.sdk.PlaytimeMetadata
import io.adjoe.sdk.PlaytimeEvent
import io.adjoe.sdk.InitOptions
import io.adjoe.sdk.CatalogOptions
import io.adjoe.sdk.CatalogPlacement
import io.adjoe.sdk.AppCategory

/**
 * Helper for constructing SDK model objects from [ReadableMap]s and
 * converting SDK status objects back into RN maps. Migrated from the
 * original inner `BasicUtil` Java class.
 */
object BasicUtil {

    private fun genderToInt(gender: PlaytimeGender?): Int? {
        return when (gender) {
            PlaytimeGender.MALE -> 0
            PlaytimeGender.FEMALE -> 1
            PlaytimeGender.UNKNOWN -> 2
            else -> null
        }
    }

    private fun genderFromInt(value: Int?): PlaytimeGender? {
        return when (value) {
            0 -> PlaytimeGender.MALE
            1 -> PlaytimeGender.FEMALE
            2 -> PlaytimeGender.UNKNOWN
            else -> null
        }
    }

    /**
     * Reads an optional string for the given key, or null when absent.
     */
    private fun optString(map: ReadableMap?, key: String): String? {
        if (map == null || !map.hasKey(key) || map.isNull(key)) return null
        return map.getString(key)
    }

    /**
     * Reads an optional nested map for the given key, or null when absent.
     */
    private fun optMap(map: ReadableMap?, key: String): ReadableMap? {
        if (map == null || !map.hasKey(key) || map.isNull(key)) return null
        return map.getMap(key)
    }

    /**
     * Reads the `value` string of a sealed value map, or null when absent.
     */
    private fun sealedValueOf(map: ReadableMap?): String? {
        if (map == null || !map.hasKey(JsonKey.VALUE)) return null
        return map.getString(JsonKey.VALUE)
    }

    /**
     * Whether a sealed value map is explicitly flagged as a publisher-supplied value.
     */
    private fun isSealedValueCustom(map: ReadableMap?): Boolean {
        if (map == null || !map.hasKey(JsonKey.IS_CUSTOM)) return false
        return map.getBoolean(JsonKey.IS_CUSTOM)
    }

    /**
     * Reads a nested sealed value map (`{ value, isCustom }`) for the given key.
     */
    private fun sealedMap(map: ReadableMap?, key: String): ReadableMap? {
        if (map == null || !map.hasKey(key)) return null
        return map.getMap(key)
    }

    private fun catalogPlacementFrom(map: ReadableMap?): CatalogPlacement? {
        val value = sealedValueOf(map) ?: return null
        if (isSealedValueCustom(map)) return CatalogPlacement.Custom(value)
        return when (value) {
            "navbar" -> CatalogPlacement.Navbar
            "icon" -> CatalogPlacement.Icon
            "earn_tab" -> CatalogPlacement.EarnTab
            "game_tab" -> CatalogPlacement.GameTab
            "home_page" -> CatalogPlacement.HomePage
            "notification_inbox" -> CatalogPlacement.NotificationInbox
            "notification" -> CatalogPlacement.Notification
            "mini_game" -> CatalogPlacement.MiniGame
            "order_page" -> CatalogPlacement.OrderPage
            "redeem_section" -> CatalogPlacement.RedeemSection
            "scan_receipt" -> CatalogPlacement.ScanReceipt
            "step_converter" -> CatalogPlacement.StepConverter
            // A predefined value this wrapper version does not know yet: forward it
            // verbatim instead of dropping it.
            else -> CatalogPlacement.Custom(value)
        }
    }

    /**
     * Builds a [CatalogPlacement] from the raw sealed value map passed by JavaScript.
     */
    fun constructCatalogPlacementFrom(map: ReadableMap?): CatalogPlacement? {
        return catalogPlacementFrom(map)
    }

    private fun appCategoryFrom(map: ReadableMap?): AppCategory {
        // AppCategory supports no custom values, so an absent or unrecognized value
        // falls back to the schema default and `isCustom` is ignored entirely.
        return when (sealedValueOf(map)) {
            "gaming" -> AppCategory.GAMING
            "non-gaming" -> AppCategory.NON_GAMING
            "both" -> AppCategory.BOTH
            else -> AppCategory.GAMING
        }
    }

    fun constructPlaytimeExtensionsFrom(extensionMap: ReadableMap?): PlaytimeExtensions {
        val extensions = PlaytimeExtensions.Builder()
        if (extensionMap == null) return extensions.build()
        return extensions
            .setSubId1(optString(extensionMap, JsonKey.SUB_ID_1))
            .setSubId2(optString(extensionMap, JsonKey.SUB_ID_2))
            .setSubId3(optString(extensionMap, JsonKey.SUB_ID_3))
            .setSubId4(optString(extensionMap, JsonKey.SUB_ID_4))
            .setSubId5(optString(extensionMap, JsonKey.SUB_ID_5))
            .build()
    }

    fun constructPlaytimeMetadataFrom(metadataMap: ReadableMap?): PlaytimeMetadata? {
        if (metadataMap == null) return null
        return PlaytimeMetadata(
            metadata1 = optString(metadataMap, JsonKey.METADATA_1),
            metadata2 = optString(metadataMap, JsonKey.METADATA_2),
        )
    }

    /**
     * Parses the ISO 8601 `birthday` string sent by JavaScript into a [Date].
     *
     * JS has no date type on the bridge, so the value arrives as a string (typically from
     * `Date.prototype.toISOString()`) while the SDK models it as a [Date]. Accepts the
     * fractional-seconds form (`2002-12-02T00:00:00.000Z`), the plain form
     * (`2002-12-02T00:00:00Z`), an explicit UTC offset, and a bare calendar date
     * (`2002-12-02`). An unparseable value yields null rather than throwing, so a bad
     * birthday never fails initialization.
     */
    private fun parseBirthday(value: String?): Date? {
        if (value == null || TextUtils.isEmpty(value)) return null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Instant.parse only accepts an instant with a zone designator, so fall back to
            // the offset and local-date forms before giving up.
            try {
                return Date.from(Instant.parse(value))
            } catch (e: Exception) {
                // Not an instant; try the remaining representations below.
            }
            try {
                return Date.from(OffsetDateTime.parse(value).toInstant())
            } catch (e: Exception) {
                // Not an offset date time either.
            }
            try {
                return Date.from(LocalDate.parse(value).atStartOfDay(ZoneOffset.UTC).toInstant())
            } catch (e: Exception) {
                return null
            }
        }

        for (pattern in arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ssX",
            "yyyy-MM-dd",
        )) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                sdf.isLenient = false
                return sdf.parse(value)
            } catch (e: Exception) {
                // Try the next pattern.
            }
        }
        return null
    }

    /**
     * Formats a native [Date] as an ISO 8601 UTC string, the representation used for
     * date values crossing the TurboModule bridge.
     */
    private fun isoStringFrom(date: Date?): String? {
        if (date == null) return null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                return DateTimeFormatter.ISO_INSTANT.format(date.toInstant())
            } catch (e: Exception) {
                // Fall through to the SimpleDateFormat based formatting below.
            }
        }
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.format(date)
        } catch (e: Exception) {
            null
        }
    }

    fun constructPlaytimeUserProfileFrom(userProfileMap: ReadableMap?): PlaytimeUserProfile? {
        if (userProfileMap == null) return null

        val playtimeGender = if (userProfileMap.hasKey(JsonKey.GENDER) && !userProfileMap.isNull(JsonKey.GENDER)) {
            genderFromInt(userProfileMap.getInt(JsonKey.GENDER)) ?: PlaytimeGender.UNKNOWN
        } else {
            PlaytimeGender.UNKNOWN
        }

        val birthday = parseBirthday(optString(userProfileMap, JsonKey.BIRTHDAY))
        return PlaytimeUserProfile(playtimeGender, birthday)
    }

    fun constructInitOptionsFrom(optionsMap: ReadableMap?): InitOptions {
        val sdkHash = optString(optionsMap, JsonKey.SDK_HASH) ?: ""
        return InitOptions(
            sdkHash = sdkHash,
            userId = optString(optionsMap, JsonKey.USER_ID),
            userProfile = constructPlaytimeUserProfileFrom(optMap(optionsMap, JsonKey.USER_PROFILE)),
            extensions = constructPlaytimeExtensionsFrom(optMap(optionsMap, JsonKey.EXTENSIONS)),
            language = optString(optionsMap, JsonKey.LANGUAGE),
            uaNetwork = optString(optionsMap, JsonKey.UA_NETWORK),
            uaChannel = optString(optionsMap, JsonKey.UA_CHANNEL),
            uaCampaignName = optString(optionsMap, JsonKey.UA_CAMPAIGN_NAME),
            country = optString(optionsMap, JsonKey.COUNTRY),
        )
    }

    fun constructCatalogOptionsFrom(map: ReadableMap?): CatalogOptions {
        return CatalogOptions(
            placement = catalogPlacementFrom(sealedMap(map, JsonKey.PLACEMENT)),
            category = appCategoryFrom(sealedMap(map, JsonKey.CATEGORY)),
            promotionTag = optString(map, JsonKey.PROMOTION_TAG),
            metadata = constructPlaytimeMetadataFrom(optMap(map, JsonKey.METADATA)),
        )
    }

    fun playtimeEventToReadableMap(event: PlaytimeEvent?): ReadableMap? {
        if (event == null) return null
        val eventMap = Arguments.createMap()
        eventMap.putString(JsonKey.NAME, event.name)
        eventMap.putString(JsonKey.OCCURRED_AT, isoStringFrom(event.occurredAt))
        return eventMap
    }

    fun statusToReadableMap(status: PlaytimeStatus): ReadableMap {
        val statusMap = Arguments.createMap()
        statusMap.putString(JsonKey.VERSION, status.version)
        statusMap.putBoolean(JsonKey.IS_INITIALIZED, status.isInitialized)
        statusMap.putString(JsonKey.USER_ID, status.userId)
        statusMap.putBoolean(JsonKey.IS_TOS_ACCEPTED, status.isTosAccepted)
        statusMap.putMap(JsonKey.DETAILS, statusDetailsToReadableMap(status.details))
        statusMap.putBoolean(JsonKey.IS_USAGE_PERMISSION_ACCEPTED, status.isUsagePermissionAccepted)
        return statusMap
    }

    private fun campaignsStateToInt(state: PlaytimeCampaignsState): Int {
        return when (state) {
            PlaytimeCampaignsState.READY -> 0
            PlaytimeCampaignsState.BLOCKED -> 1
            PlaytimeCampaignsState.VPN_DETECTED -> 2
            PlaytimeCampaignsState.GEO_MISMATCH -> 3
            PlaytimeCampaignsState.NOT_INITIALIZED -> 4
        }
    }

    private fun campaignsStateToArray(campaignStates: List<PlaytimeCampaignsState>?): WritableArray {
        val array = Arguments.createArray()
        if (campaignStates != null) {
            for (state in campaignStates) {
                array.pushInt(campaignsStateToInt(state))
            }
        }
        return array
    }

    fun statusDetailsToReadableMap(details: PlaytimeStatusDetails): ReadableMap {
        val detailsMap = Arguments.createMap()
        detailsMap.putBoolean(JsonKey.IS_FRAUD, details.isFraud)
        detailsMap.putBoolean(JsonKey.CAMPAIGNS_AVAILABLE, details.campaignsAvailable)
        detailsMap.putArray(JsonKey.CAMPAIGNS_STATE, campaignsStateToArray(details.campaignsState))
        detailsMap.putIntOrNull(details.testGroup, JsonKey.TEST_GROUP)
        return detailsMap
    }

    object JsonKey {
        const val VERSION = "version"
        const val IS_INITIALIZED = "isInitialized"
        const val USER_ID = "userId"
        const val IS_TOS_ACCEPTED = "isTosAccepted"
        const val IS_USAGE_PERMISSION_ACCEPTED = "isUsagePermissionAccepted"
        const val DETAILS = "details"
        const val IS_FRAUD = "isFraud"
        const val CAMPAIGNS_AVAILABLE = "campaignsAvailable"
        const val CAMPAIGNS_STATE = "campaignsState"
        const val TEST_GROUP = "testGroup"

        const val SDK_HASH = "sdkHash"
        const val USER_PROFILE = "userProfile"
        const val EXTENSIONS = "extensions"
        const val LANGUAGE = "language"

        const val GENDER = "gender"
        const val BIRTHDAY = "birthday"

        const val NAME = "name"
        const val OCCURRED_AT = "occurredAt"

        const val SUB_ID_1 = "subId1"
        const val SUB_ID_2 = "subId2"
        const val SUB_ID_3 = "subId3"
        const val SUB_ID_4 = "subId4"
        const val SUB_ID_5 = "subId5"

        const val METADATA = "metadata"
        const val METADATA_1 = "metadata1"
        const val METADATA_2 = "metadata2"

        const val PLACEMENT = "placement"
        const val CATEGORY = "category"
        const val PROMOTION_TAG = "promotionTag"
        const val UA_NETWORK = "uaNetwork"
        const val UA_CHANNEL = "uaChannel"
        const val UA_CAMPAIGN_NAME = "uaCampaignName"
        const val COUNTRY = "country"

        // Keys of a sealed value map, e.g. `{ "value": "icon", "isCustom": false }`.
        const val VALUE = "value"
        const val IS_CUSTOM = "isCustom"
    }
}
