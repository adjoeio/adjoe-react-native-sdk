import { PlaytimeOptions } from './PlaytimeOptions';

/**
 * Rewarded action representation.
 */
export interface PlaytimeRewardAction {
    /**
     * Reward action name.
     */
    name: string;
    
    /**
     * Description of the task.
     */
    taskDescription: string;
    
    /**
     * Type of task to complete. Possible values: sequential, bonus, playtime.
     */
    taskType: string;
    
    /**
     * Duration (in seconds) of play time required to reward a time-based event. Currently not supported on iOS.
     */
    playDuration?: number | null;
    
    /**
     * The level number of the time-based event. Currently not supported on iOS.
     */
    level?: number | null;
    
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
     * Amount of coins that would be rewarded without any bonuses applied.
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
     * The timestamp (ISO 8601) indicating when the booster reward expires. 
     * Use it to determine user eligibility for booster rewards and support features such as booster countdown.
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
 * A interface representing a media item.
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
 * The interface provides detailed information about events and rewards.
 */
export interface PlaytimeEventConfig {
    /**
     * Array of rewarded events that must be completed in the given order.
     * These will usually be progress events a user can reach in a game or app.
     */
    sequentialActions: PlaytimeRewardAction[];

    /**
     * Array of rewarded events that can be completed in any order.
     * These will be bonus rewards a user should get on top of the other rewards.
     */
    bonusActions: PlaytimeRewardAction[];

    /**
     * Array of events that are rewarded based on the time played.
     */
    timeBasedActions: PlaytimeRewardAction[];

    /**
     * Total coins collected by the user.
     */
    totalCoinsCollected: number;

    /**
     * Maximum possible coins for this config.
     */
    totalCoinsPossible?: number | null;

    /**
     * Cashback reward configuration for in-app purchases. A missing value means that the feature 
     * is not supported for the campaign or the SDK.
     */
    cashbackReward?: PlaytimeCashbackConfig | null;

    /**
     * Array of events that multiply the rewards.
     */
    multipliersActions: PlaytimeRewardActionMultiplier[];
}

export interface PlaytimeCashbackConfig {
    /**
     * Amount of coins that is to be rewarded per 1 USD of IAP.
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

export interface PlaytimeCashbackReward {
    /**
     * Total amount of coins for given rewarded events group.
     */
    totalCoins?: number | null;

    /**
     * Events in the reward group.
     */
    events?: PlaytimeCashbackRewardEvent[] | null;
}

export interface PlaytimeCashbackRewardEvent {
    /**
     * Amount of coins granted for the event.
     */
    coins?: number | null;

    /**
     * Timestamp (ISO 8601).
     */
    processAt?: string | null;
    
    /**
     * Timestamp (ISO 8601).
     */
    receivedAt?: string | null
}

export interface PlaytimeRewardActionMultiplier {
    /**
     * Return the event's name
     */
    eventName?: string | null;

    /**
     * Returns the event's description
     */
    eventDescription?: string | null;

    /**
     * Return the multiplication factor of the coin for the multiplier event. value: 0-100 
     */
    multiplierFactorPercentage?: number | null;

    /**
     * Returns the maximum number of levels that can be multiplied by the event
     */
    multiplierLevels?: number | null;

    /**
     * Return the status of the multiplied event:
     * "Pending": multiplier events not used yet
     * "Active": multiplier event is currently active
     * "Finished": multiplier event has been used and finished 
     */
    status?: string | null;

    /**
     * Returns the number of levels that has been multiplied by the event
     */
    usedLevels?: number | null;
}

/**
 * The model that represents a campaign and/or installed application.
 */
export interface PlaytimeCampaign {
    /**
     * Campaign UUID
     */
    campaignUUID?: string | null;
    
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
     * On Android, it’s represented by a string in the format com.example.myapp.
     * On iOS, it’s App Store ID.
     */
    appID: string;

    /**
     * On iOS, it’s the ID in the format com.example.myapp.
     */
    appBundleID?: string | null;
    
    /**
     * On iOS, it’s application's store ID number that is part of the App Store link.
     */
    appStoreID?: string | null;

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
     * Expiration timestamp (ISO 8601) denoting how long players can get
     * rewards after installing the app.
     */
    rewardingExpiresAt?: string | null;

    /**
     * Timestamp (ISO 8601) denoting the maximum amount of time after
     * fetching the campaign that it can be installed.
     */
    campaignExpiresAt?: string | null;

    /**
     * App category.
     */
    appCategory?: string | null;

    /**
     * Campaign type.
     */
    campaignType?: string | null;

    /**
     * The ordered position for specific featured campaigns.
     */
    featuredPosition?: number | null;

    /**
     * The eCPM of the campaign.
     */
    score?: number | null;

    /**
     * URL to the campaign icon image.
     */
    iconImage?: string | null;

    /**
     * Campaign media assets (portrait & landscape).
     */
    image?: PlaytimeMedia | null;

    /**
     * Campaign video media assets.
     */
    video?: PlaytimeMedia | null;

    /**
     * Active promotion details, if any.
     */
    promotion?: PlaytimePromotion | null;

    /**
     * Event and rewards configuration.
     */
    eventConfig?: PlaytimeEventConfig | null;

    /**
     * Flag indicating whether all rewards have been collected.
     */
    isCompleted?: boolean | null;
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
 * Permissions granted by the user.
 */
export interface PlaytimePermissions {
    /**
     * Flag indicating whether the terms of service are accepted.
     */
    isTOSAccepted: boolean;

    /**
     * Flag indicating whether the usage permission is accepted.
     */
    isUsagePermissionAccepted: boolean;
}

/**
 * SDK response containing permissions.
 */
export interface PlaytimePermissionsResponse {
    /**
     * Permissions granted by the user.
     */
    permissions: PlaytimePermissions
}

/**
 * The entry point of Adjoe Playtime SDK in Studio version. This namespace provides methods to fetch campaigns and installed apps to show them in your custom UI.
 */
declare namespace _default {
    /**
     * Get the list of offers that a user can install.
     * 
     * Supported on both Android and iOS.
     * @param options An object to pass additional options.
     * @returns The campaigns response with list of campaigns.
     */
    function getCampaigns(options: PlaytimeOptions): Promise<PlaytimeCampaignsResponse>;

    /**
     * Get the list of apps the user has already installed and that will contain the progress the user has made already.
     * 
     * Supported on both Android and iOS.
     * @param options An object to pass additional options.
     * @returns The campaigns response with list of installed campaigns.
     */
    function getInstalledCampaigns(options: PlaytimeOptions): Promise<PlaytimeCampaignsResponse>;

    /**
     * Use this method to forward the user to the store.
     * 
     * Supported on both Android and iOS.
     * @param campaign The campaign you want to open
     */
    function openInStore(campaign: PlaytimeCampaign): Promise<void>;

    /**
     * Use this method to forward the user to the app.
     * 
     * Supported on both Android and iOS.
     * @param campaign The campaign you want to open
     */
    function openInstalledCampaign(campaign: PlaytimeCampaign): Promise<void>;

    /**
     * Get user’s permissions.
     * 
     * Supported only on Android.
     * @returns The permissions response.
     */
    function getPermissions(): Promise<PlaytimePermissionsResponse>;

    /**
     * Show the prompt requesting user’s permissions.
     * 
     * Supported only on Android.
     * @returns The permissions response.
     */
    function showPermissionsPrompt(): Promise<PlaytimePermissionsResponse>;

    /**
     * Opens the register popup for rewards connect
     * Supported for both android and iOS.
     */
    function registerRewardsConnect(): Promise<void>;

    /**
     * Resets the rewards connect registration
     * Supported for both android and iOS.
     */
    function resetRewardsConnect(): Promise<void>;
}

export default _default;
