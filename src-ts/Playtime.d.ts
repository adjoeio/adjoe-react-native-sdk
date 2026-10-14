/**
 * Options for the Playtime catalog request. Provides context (placement, category, promotion tag, and per-request UA context) to the backend for campaign targeting.
 */
export interface CatalogOptions {
    /**
     * The placement where the catalog will be shown.
     */
    placement?: CatalogPlacement | null;

    /**
     * The app category filter. Defaults to GAMING.
     */
    category: AppCategory;

    /**
     * An optional promotion tag to trigger user-level promotions.
     */
    promotionTag?: string | null;

    /**
     * Metadata visible in S2S payouts.
     */
    metadata?: PlaytimeMetadata | null;
}

/**
 * Use this class to pass additional options like the user ID to [io.adjoe.sdk.Playtime.initPlaytime].
 */
export interface InitOptions {
    /**
     * The SDK hash used to initialize the SDK. Used for all subsequent requests.
     */
    sdkHash: string;

    /**
     * A custom identifier you must assign to each user.
     */
    userId?: string | null;

    /**
     * User profile for targeting.
     */
    userProfile?: PlaytimeUserProfile | null;

    /**
     * Extension IDs visible in S2S payouts.
     */
    extensions?: PlaytimeExtensions | null;

    /**
     * The language override for the SDK. When null, the device locale language is applied.
     */
    language?: string | null;

    /**
     * User acquisition network where the user came from.
     */
    uaNetwork?: string | null;

    /**
     * User acquisition channel where the user came from; e.g. video, offerwall.
     */
    uaChannel?: string | null;

    /**
     * User acquisition campaign name. Sent to campaign distribution as the UASubPublisherEncrypted query param.
     */
    uaCampaignName?: string | null;

    /**
     * Only use this field if you use multiple types of cash currencies for rewarding (e.g, USD, EUR). Setting the country tells adjoe which currency to use for the user. Country can only be set once per user. Requires a valid ISO 3166-1 alpha-2 code (e.g., US).
     */
    country?: string | null;
}

export interface PlaytimeUserProfile {
    /**
     * User gender.
     */
    gender?: PlaytimeGender | null;

    /**
     * The user's birthday. Provided as an ISO 8601 string; the value is parsed natively into a date type.
     */
    birthday?: string | null;
}

/**
 * An SDK event shared with the publisher using the configured event map.
 */
export interface PlaytimeEvent {
    /**
     * The publisher event name configured for the emitted SDK event.
     */
    name: string;

    /**
     * The time at which the SDK delivered the event to the listener. Provided as an ISO 8601 string; the value is parsed natively into a date type.
     */
    occurredAt: string;
}

/**
 * Represents the gender of the user.
 */
export enum PlaytimeGender {
    /**
     * Male gender.
     */
    MALE = 0,
    /**
     * Female gender.
     */
    FEMALE = 1,
    /**
     * Unknown gender.
     */
    UNKNOWN = 2
}

/**
 * Adjoe Extension class holds additional optional subids to be used by publisher to pass different
 * values
 */
export interface PlaytimeExtensions {
    /**
     * An optional identifier that is provided back in the S2S payout URL.
     */
    subId1?: string | null;

    /**
     * An optional identifier that is provided back in the S2S payout URL.
     */
    subId2?: string | null;

    /**
     * An optional identifier that is provided back in the S2S payout URL.
     */
    subId3?: string | null;

    /**
     * An optional identifier that is provided back in the S2S payout URL.
     */
    subId4?: string | null;

    /**
     * An optional identifier that is provided back in the S2S payout URL.
     */
    subId5?: string | null;
}

/**
 * This class is used to pass additional metadata of the user.
 */
export interface PlaytimeMetadata {
    /**
     * An optional identifier that is provided back in the S2S payout URL.
     */
    metadata1?: string | null;

    /**
     * An optional identifier that is provided back in the S2S payout URL.
     */
    metadata2?: string | null;
}

/**
 * Represents the placement where the catalog will be shown.
 * Use one of the predefined placements or create a custom placement via the custom case.
 */
export type CatalogPlacement = string;

/**
 * The predefined catalog placements.
 * Any other string is treated as a custom placement.
 */
export const CatalogPlacement: {
    /**
     * Catalog placed in the navigation bar.
     */
    readonly Navbar: 'navbar';
    /**
     * Catalog placed as an icon.
     */
    readonly Icon: 'icon';
    /**
     * Catalog placed in the earn tab.
     */
    readonly EarnTab: 'earn_tab';
    /**
     * Catalog placed in the game tab.
     */
    readonly GameTab: 'game_tab';
    /**
     * Catalog placed on the home page.
     */
    readonly HomePage: 'home_page';
    /**
     * Catalog placed in the notification inbox.
     */
    readonly NotificationInbox: 'notification_inbox';
    /**
     * Catalog placed in a notification.
     */
    readonly Notification: 'notification';
    /**
     * Catalog placed in a mini game.
     */
    readonly MiniGame: 'mini_game';
    /**
     * Catalog placed on the order page.
     */
    readonly OrderPage: 'order_page';
    /**
     * Catalog placed in the redeem section.
     */
    readonly RedeemSection: 'redeem_section';
    /**
     * Catalog placed in the scan receipt flow.
     */
    readonly ScanReceipt: 'scan_receipt';
    /**
     * Catalog placed in the step converter.
     */
    readonly StepConverter: 'step_converter';
};

/**
 * Represents the app category for campaign targeting.
 */
export type AppCategory = 'gaming' | 'non-gaming' | 'both';

/**
 * The app categories.
 */
export const AppCategory: {
    /**
     * Gaming apps.
     */
    readonly GAMING: 'gaming';
    /**
     * Non-gaming apps.
     */
    readonly NON_GAMING: 'non-gaming';
    /**
     * Both gaming and non-gaming apps.
     */
    readonly BOTH: 'both';
};

/**
 * The model that represents the status for SDK
 */
export interface PlaytimeStatus {
    /**
     * The Playtime SDK version. On wrappers this is exposed as a codegen constant to satisfy the non-async bridge-layer rule.
     */
    version: string;

    /**
     * A flag indicating if the SDK is successfully initialized.
     */
    isInitialized: boolean;

    /**
     * The unique ID of the user by which they are identified within the Playtime services.
     */
    userId?: string | null;

    /**
     * A flag indicating whether the terms of service have been accepted.
     */
    isTosAccepted: boolean;

    /**
     * Additional information about the SDK status.
     */
    details: PlaytimeStatusDetails;

    /**
     * A flag indicating whether the usage permission has been accepted.
     *
     * Provided on Android only; the key is omitted on iOS.
     */
    isUsagePermissionAccepted?: boolean | null;
}

/**
 * The model that represents the status for SDK
 */
export interface PlaytimeStatusDetails {
    /**
     * A flag indicating if the current user is marked as a fraud user.
     */
    isFraud: boolean;

    /**
     * Indicates whether the user is eligible to request campaigns.
     */
    campaignsAvailable: boolean;

    /**
     * Provides optional context explaining the eligibility state.
     */
    campaignsState: PlaytimeCampaignsState[];

    /**
     * The test group assigned to the user by the backend, if any.
     */
    testGroup?: number | null;
}

/**
 * The model that represents the campaigns state for SDK
 */
export enum PlaytimeCampaignsState {
    /**
     * Fully eligible
     */
    READY = 0,
    /**
     * No campaigns available
     */
    BLOCKED = 1,
    /**
     * Campaigns conditionally blocked
     */
    VPN_DETECTED = 2,
    /**
     * No available campaigns for user region
     */
    GEO_MISMATCH = 3,
    /**
     * The SDK is not initialized
     */
    NOT_INITIALIZED = 4
}

/**
 * The entry point of adjoe Playtime SDK.
 */
declare namespace _default {
    /**
     * Initialize the Playtime SDK with the given options.
     *
     * Supported on Android, iOS and wrappers.
     * @param options The options used to initialize the SDK.
     */
    function initPlaytime(options: InitOptions): Promise<void>;

    /**
     * Shows the Playtime catalog.
     *
     * Supported on Android, iOS and wrappers.
     * @param catalogOptions Options for the catalog request.
     */
    function showCatalog(catalogOptions?: CatalogOptions): Promise<void>;

    /**
     * Get the status of the SDK.
     *
     * Supported on Android, iOS and wrappers.
     */
    function getStatus(): Promise<PlaytimeStatus>;

    /**
     * Deinitialize the SDK. This releases the resources taken up by internal components.
     *
     * Supported on Android, iOS and wrappers.
     */
    function teardown(): Promise<void>;

    /**
     * Register a listener to receive Playtime SDK events.
     *
     * The Promise resolves once the listener is registered; events are delivered
     * through the listener's `didReceive` callback.
     *
     * Supported on Android, iOS and wrappers.
     * @param eventListener The listener to be notified about Playtime SDK events.
     */
    function addEventListener(eventListener: {
        /**
         * Called when the SDK emits an event configured for publisher delivery.
         * @param event The event emitted by the SDK.
         */
        didReceive: (event: PlaytimeEvent) => void;
    }): Promise<void>;

    /**
     * Remove the currently registered Playtime SDK event listener.
     *
     * Supported on Android, iOS and wrappers.
     */
    function removeEventListener(): Promise<void>;

    /**
     * Records that a catalog entry point became visible. This mandatory event is used for analytics to understand conversion rates from catalog visibility to catalog opens. Pass the placement for the entry point that is visible; use different placements when the app has multiple catalog entry points.
     *
     * Supported on Android, iOS and wrappers.
     * @param catalogPlacement The placement for the visible catalog entry point. Use a different placement for each catalog entry point in the app.
     */
    function executeImpressionEvent(catalogPlacement: CatalogPlacement): Promise<void>;
}

export default _default;
//# sourceMappingURL=Playtime.d.ts.map
