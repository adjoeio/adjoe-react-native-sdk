/**
 * Options for campaign distribution requests. Provides context (placement, category, display format, promotion tag, and per-request UA context) to the backend for campaign targeting.
 */
export interface CampaignOptions {
    /**
     * The placement where the campaigns will be shown.
     */
    placement?: StudioPlacement | null;

    /**
     * The app category filter. Defaults to GAMING.
     */
    category: AppCategory;

    /**
     * The UI display format.
     */
    displayFormat?: DisplayFormat | null;

    /**
     * An optional promotion tag to trigger user-level promotions.
     */
    promotionTag?: string | null;
}

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
}

/**
 * Options for installed-campaign requests. Exposes only the app category filter for campaign targeting.
 */
export interface InstalledCampaignOptions {
    /**
     * The app category filter. Defaults to GAMING.
     */
    category: AppCategory;
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
 * Represents the placement where Studio campaigns will be shown.
 * Use one of the predefined placements or create a custom placement via the custom case.
 */
export type StudioPlacement = string;

/**
 * The predefined Studio placements.
 * Any other string is treated as a custom placement.
 */
export const StudioPlacement: {
    /**
     * Campaigns placed in the navigation bar.
     */
    readonly Navbar: 'navbar';
    /**
     * Campaigns placed as an icon.
     */
    readonly Icon: 'icon';
    /**
     * Campaigns placed in the earn tab.
     */
    readonly EarnTab: 'earn_tab';
    /**
     * Campaigns placed in the game tab.
     */
    readonly GameTab: 'game_tab';
    /**
     * Campaigns placed on the home page.
     */
    readonly HomePage: 'home_page';
    /**
     * Campaigns placed in the notification inbox.
     */
    readonly NotificationInbox: 'notification_inbox';
    /**
     * Campaigns placed in a notification.
     */
    readonly Notification: 'notification';
    /**
     * Campaigns placed in a mini game.
     */
    readonly MiniGame: 'mini_game';
    /**
     * Campaigns placed on the order page.
     */
    readonly OrderPage: 'order_page';
    /**
     * Campaigns placed in the redeem section.
     */
    readonly RedeemSection: 'redeem_section';
    /**
     * Campaigns placed in the scan receipt flow.
     */
    readonly ScanReceipt: 'scan_receipt';
    /**
     * Campaigns placed in the step converter.
     */
    readonly StepConverter: 'step_converter';
};

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
 * Represents the display format for campaign rendering.
 * Use one of the predefined formats or create a custom format via the custom case.
 */
export type DisplayFormat = string;

/**
 * The predefined display formats.
 * Any other string is treated as a custom display format.
 */
export const DisplayFormat: {
    /**
     * Renders campaigns as icons.
     */
    readonly Icon: 'icon';
    /**
     * Renders campaigns as notifications.
     */
    readonly Notification: 'notification';
    /**
     * Renders campaigns in a notification inbox.
     */
    readonly NotificationInbox: 'notification_inbox';
    /**
     * Renders campaigns in a horizontally scrolling carousel.
     */
    readonly Carousel: 'carousel';
    /**
     * Renders campaigns in a custom catalog layout.
     */
    readonly CustomCatalog: 'custom_catalog';
    /**
     * Renders campaigns as a banner.
     */
    readonly Banner: 'banner';
    /**
     * Renders campaigns in a popup.
     */
    readonly Popup: 'popup';
    /**
     * Renders campaigns as an interstitial.
     */
    readonly Interstitial: 'interstitial';
};

/**
 * Represents the app category for campaign targeting.
 */
export type AppCategory = 'gaming' | 'non_gaming' | 'both';

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
    readonly NON_GAMING: 'non_gaming';
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
}

export default _default;
//# sourceMappingURL=Playtime.d.ts.map
