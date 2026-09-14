import { CampaignOptions, InstalledCampaignOptions } from "./Playtime";

/**
 * Rewarded action representation.
 */
export interface PlaytimeRewardAction {
    /**
     * Reward action name.
     */
    name?: string | null;

    /**
     * Description of the task.
     */
    taskDescription?: string | null;

    /**
     * The level number of the time-based event.
     */
    level?: number | null;

    /**
     * Type of task to complete. Possible values: sequential, bonus, playtime, cpa.
     */
    taskType: string;

    /**
     * Duration (in seconds) of play time required to reward a time-based event. Currently not supported on iOS.
     */
    playDuration?: number | null;

    /**
     * The amount of coins or rewards the user will receive upon completing the event.
     */
    amount: number;

    /**
     * Timestamp when a reward was granted (ISO 8601).
     */
    rewardedAt?: string | null;

    /**
     * Number of times that the action can be rewarded, applicable to repetitive events.
     */
    rewardsCount?: number | null;

    /**
     * Number of times that the action has been rewarded, applicable to repetitive events.
     */
    completedRewards?: number | null;

    /**
     * Time window (in minutes) during which the Booster reward is applicable.
     */
    timedCoinsDuration?: number | null;

    /**
     * Amount of coins rewarded during the booster period.
     */
    timedCoins?: number | null;

    /**
     * The amount of coins or rewards the user would receive if there was no promotion.
     */
    originalCoins?: number | null;

    /**
     * Flag indicating whether the event is a booster event.
     */
    isTimed?: boolean | null;

    /**
     * Flag indicating whether the event has been rewarded with promotion.
     */
    isRewardedForPromotion?: boolean | null;

    /**
     * Timestamp (ISO 8601) indicating when the booster reward expires.
     */
    boosterExpiresAt?: string | null;
}

/**
 * Promotion representation.
 */
export interface PlaytimePromotion {
    /**
     * Promotion name.
     */
    name?: string | null;

    /**
     * Description of the promotion.
     */
    promotionDescription?: string | null;

    /**
     * Boost multiplier for the promotion.
     */
    boostFactor?: number | null;

    /**
     * Promotion start timestamp (ISO 8601).
     */
    startTime?: string | null;

    /**
     * Promotion end timestamp (ISO 8601).
     */
    endTime?: string | null;

    /**
     * Targeting type for promotion.
     */
    targetingType?: string | null;
}

/**
 * A class representing a media item.
 */
export interface PlaytimeMedia {
    /**
     * URL to portrait-oriented media.
     */
    portrait?: string | null;

    /**
     * URL to landscape-oriented media.
     */
    landscape?: string | null;
}

/**
 * Configuration for multi-event rewards.
 */
export interface PlaytimeEventConfig {
    /**
     * Array of rewarded events that must be completed in the given order.
     */
    sequentialActions: PlaytimeRewardAction[];

    /**
     * Array of rewarded events that can be completed in any order.
     */
    bonusActions: PlaytimeRewardAction[];

    /**
     * Array of events that are rewarded based on the time played.
     */
    timeBasedActions: PlaytimeRewardAction[];

    /**
     * Array of CPA (Cost Per Action) events that reward the user for completing specific in-app actions.
     */
    cpaActions: PlaytimeRewardAction[];

    /**
     * Total coins collected by the user.
     */
    totalCoinsCollected?: number | null;

    /**
     * Maximum possible coins for this config.
     */
    totalCoinsPossible?: number | null;

    /**
     * The amount of time in seconds to the next level.
     *
     * Provided on Android only.
     */
    secondsToNextLevel?: number | null;

    /**
     * Maximum possible coins for this config if there was no promotion.
     */
    totalOriginalCoinsPossible?: number | null;

    /**
     * Cashback reward configuration for in-app purchases.
     */
    cashbackReward?: PlaytimeCashbackConfig | null;

    /**
     * Array of events that multiply the rewards.
     */
    multipliersActions: PlaytimeRewardActionMultiplier[];

    /**
     * Total amount of coins for all sequential events with promotion multiplier.
     */
    totalSequentialCoins?: number | null;

    /**
     * Total amount of coins for all sequential events without promotion multiplier.
     */
    totalOriginalSequentialCoins?: number | null;

    /**
     * Total amount of coins for all bonus events with promotion multiplier.
     */
    totalBonusCoins?: number | null;

    /**
     * Total amount of coins for all bonus events without promotion multiplier.
     */
    totalOriginalBonusCoins?: number | null;
}

/**
 * Configuration for a multiplier event that multiplies rewards.
 */
export interface PlaytimeRewardActionMultiplier {
    /**
     * The event's name.
     */
    eventName?: string | null;

    /**
     * The event's description.
     */
    eventDescription?: string | null;

    /**
     * Multiplication factor of the coin for the multiplier event (0-100).
     */
    multiplierFactorPercentage?: number | null;

    /**
     * Maximum number of levels that can be multiplied by the event.
     */
    multiplierLevels?: number | null;

    /**
     * Status of the multiplied event: Pending, Active, or Finished.
     */
    status?: string | null;

    /**
     * Number of levels that has been multiplied by the event.
     */
    usedLevels?: number | null;
}

/**
 * Configuration for the Playtime Cashback feature.
 */
export interface PlaytimeCashbackConfig {
    /**
     * Amount of coins rewarded per 1 USD of IAP.
     */
    exchangeRate?: number | null;

    /**
     * Description of the cashback reward.
     */
    cashbackDescription?: string | null;

    /**
     * Limit of USD available as cashback reward.
     */
    maxLimitPerCampaignUSD?: number | null;

    /**
     * Limit of coins available as cashback reward.
     */
    maxLimitPerCampaignCoins?: number | null;

    /**
     * Info on completed cashback rewards.
     */
    completedRewards?: PlaytimeCashbackReward | null;

    /**
     * Info on pending cashback rewards.
     */
    pendingRewards?: PlaytimeCashbackReward | null;
}

/**
 * Cashback reward info.
 */
export interface PlaytimeCashbackReward {
    /**
     * Total amount of coins for given rewarded events group.
     */
    totalCoins?: number | null;

    /**
     * Events in the reward group.
     */
    events: PlaytimeCashbackRewardEvent[];
}

/**
 * Cashback event.
 */
export interface PlaytimeCashbackRewardEvent {
    /**
     * Amount of coins granted for the event.
     */
    coins?: number | null;

    /**
     * Timestamp (ISO 8601) when the event is processed.
     */
    processAt?: string | null;

    /**
     * Timestamp (ISO 8601) when the event is received.
     */
    receivedAt?: string | null;
}

/**
 * The model that represents a campaign and/or installed application.
 */
export interface PlaytimeCampaign {
    /**
     * App name.
     */
    appName: string;

    /**
     * Short description of the app.
     */
    appDescription: string;

    /**
     * Unique identifier for the app.
     */
    appID: string;

    /**
     * ID in the format com.example.myapp.
     *
     * Provided on iOS only.
     */
    appBundleID?: string | null;

    /**
     * App installation timestamp (ISO 8601).
     */
    installedAt?: string | null;

    /**
     * App uninstallation timestamp (ISO 8601).
     */
    uninstalledAt?: string | null;

    /**
     * Time (in days) after which rewards expire after installation.
     */
    rewardingExpiresAfter?: number | null;

    /**
     * Expiration timestamp (ISO 8601) denoting how long players can get rewards after installing the app.
     */
    rewardingExpiresAt?: string | null;

    /**
     * Timestamp (ISO 8601) denoting the maximum amount of time after fetching the campaign that it can be installed.
     */
    campaignExpiresAt?: string | null;

    /**
     * Event and rewards configuration.
     */
    eventConfig?: PlaytimeEventConfig | null;

    /**
     * App category.
     */
    appCategory?: string | null;

    /**
     * Campaign type.
     */
    campaignType?: string | null;

    /**
     * Ordered position for specific featured campaigns.
     */
    featuredPosition?: number | null;

    /**
     * eCPM of the campaign.
     */
    score?: number | null;

    /**
     * Campaign media assets (portrait & landscape).
     */
    image?: PlaytimeMedia | null;

    /**
     * Campaign video media assets.
     */
    video?: PlaytimeMedia | null;

    /**
     * URL to the campaign icon image.
     */
    iconImage?: string | null;

    /**
     * Active promotion details, if any.
     */
    promotion?: PlaytimePromotion | null;

    /**
     * Flag indicating whether all rewards have been collected.
     */
    isCompleted: boolean;

    /**
     * Campaign UUID.
     */
    campaignUUID?: string | null;

    /**
     * Current status of the campaign.
     */
    status: PlaytimeCampaignStatus;

    /**
     * Flag indicating whether CPA is enabled.
     */
    isCpa: boolean;

    /**
     * CPA of the campaign.
     */
    cpa: number;

    /**
     * The campaign token.
     */
    token: string;
}

/**
 * The response of the SDK containing campaigns.
 */
export interface PlaytimeCampaignsResponse {
    /**
     * The requested selection of campaigns.
     */
    campaigns: PlaytimeCampaign[];
}

/**
 * Impression type for Playtime interaction.
 */
export enum PlaytimeImpressionType {
    /**
     * The default interaction which should be invoked when the user sees the campaign view.
     */
    DEFAULT = 0,

    /**
     * Engaged interaction which signifies that the user interacted with the campaign.
     */
    ENGAGED = 1
}

/**
 * The consent action to apply when updating the user's Playtime terms of service consent.
 */
export enum TosConsentAction {
    /**
     * Accept the terms of service on behalf of the user.
     */
    ACCEPT = 0,

    /**
     * Withdraw the user's previously granted consent to the terms of service.
     */
    WITHDRAW = 1
}

/**
 * Represents the current status of a campaign.
 */
export enum PlaytimeCampaignStatus {
    /**
     * The campaign is available for installation.
     */
    AVAILABLE = 0,

    /**
     * Waiting for the app installation.
     */
    PENDING = 1,

    /**
     * The app associated with the campaign has been installed.
     */
    INSTALLED = 2,

    /**
     * The campaign installation or processing has failed.
     */
    FAILED = 3
}

/**
 * The entry point of Adjoe Playtime SDK in Studio version. This namespace provides methods to fetch campaigns and installed apps to show them in your custom UI.
 */
declare namespace _default {
    /**
     * Update the user's consent to the Playtime terms of service, either accepting or withdrawing it.
     *
     * Supported on both Android and iOS.
     * @param action The consent action to apply: accept or withdraw the terms of service.
     */
    function updateTosConsent(action: TosConsentAction): Promise<void>;

    /**
     * Get the list of offers that a user can install.
     *
     * Supported on both Android and iOS.
     * @param campaignOptions Options for campaign distribution requests.
     * @returns The response to the request.
     */
    function getCampaigns(campaignOptions: CampaignOptions): Promise<PlaytimeCampaignsResponse>;

    /**
     * Get offer details for the specified tokens.
     *
     * Supported on both Android and iOS.
     * @param tokens The tokens of the offers to get details for.
     * @param campaignOptions Options for campaign distribution requests.
     * @returns The response to the request.
     */
    function getOfferDetails(tokens: string[], campaignOptions: CampaignOptions): Promise<PlaytimeCampaignsResponse>;

    /**
     * Get the list of apps the user has already installed and that will contain the progress the user has made already.
     *
     * Supported on both Android and iOS.
     * @param options Options for installed-campaign requests.
     * @returns The response to the request.
     */
    function getInstalledCampaigns(options: InstalledCampaignOptions): Promise<PlaytimeCampaignsResponse>;

    /**
     * Use this method to forward the user to the store.
     *
     * Supported on both Android and iOS.
     * @param campaign The campaign that user want to install
     * @param useInlineInstall set it to true if you want to use inline install
     */
    function openInStore(campaign: PlaytimeCampaign, useInlineInstall: boolean): Promise<void>;

    /**
     * Use this method to open installed application.
     *
     * Supported on both Android and iOS.
     * @param campaign The campaign you want to open
     */
    function openInstalledCampaign(campaign: PlaytimeCampaign): Promise<void>;

    /**
     * Use this method to open chatbot.
     *
     * Supported on both Android and iOS.
     * @param [campaign] The campaign you want to open
     */
    function openChatbot(campaign?: PlaytimeCampaign): Promise<void>;

    /**
     * Use this method to reset rewards connect
     *
     * Supported on both Android and iOS.
     */
    function resetRewardsConnect(): Promise<void>;

    /**
     * Execute an impression request for the given campaign.
     * This method tracks view execution locally and ensures only one view tracking request
     * is sent to the backend per campaign within a 30-minute window.
     *
     * Supported on both Android and iOS.
     * @param campaign The campaign which view should be executed.
     * @param impressionType The type of impression you want to execute.
     */
    function executeCampaignImpression(campaign: PlaytimeCampaign, impressionType: PlaytimeImpressionType): Promise<void>;

    /**
     * Execute an impression request for the given offer.
     * This method tracks view execution locally and ensures only one view tracking request
     * is sent to the backend per campaign within a 30-minute window.
     *
     * Supported on both Android and iOS.
     * @param token Token of the campaign.
     * @param appID App ID of the campaign.
     */
    function executeOfferImpression(token: string, appID: string): Promise<void>;

    /**
     * Register the user with Playtime. The RewardsConnect sheet will be displayed to the user and, on pressing Connect, the following
     * scenarios are possible:
     * 1. If the user is not registered with Playtime, the user will be prompted to register a new passkey. This passkey will be stored on
     * the user’s device (and may be synced to iCloud) and associated with the user’s Adjoe account.
     * 2. If the user is already registered with Playtime, but the SDK is not aware of it (e.g. when the app was deleted and reinstalled),
     * the user will be prompted to log in with the passkey stored on the device. If the passkey is not present, the will be prompted to
     * register a new passkey.
     * 3. The user has either registered the passkey or logged in successfully. Any subsequent call will return without error.
     *
     * Supported on both Android and iOS.
     */
    function registerRewardsConnect(): Promise<void>;

    /**
     * Use this method to deeplink campaign details.
     *
     * Supported on both Android and iOS.
     * @param campaign The campaign to show details for.
     */
    function showCampaignDetails(campaign: PlaytimeCampaign): Promise<void>;

    /**
     * Use this method to deeplink offer details using an access token and app ID.
     *
     * Supported on both Android and iOS.
     * @param token The token of the offer to show details for.
     * @param appID The app ID of the offer.
     */
    function showOfferDetails(token: string, appID: string): Promise<void>;

    /**
     * Use this method to deeplink installed campaigns.
     *
     * Supported on both Android and iOS.
     */
    function showInstalledCampaigns(): Promise<void>;

    /**
     * Show the prompt requesting user's permissions.
     *
     * Supported on Android only. Calling it on iOS rejects as unsupported.
     */
    function showPermissionsPrompt(): Promise<void>;
}

export default _default;
