export type PlaytimeCampaignsState = 'READY' | 'BLOCKED' | 'VPN_DETECTED' | 'GEO_MISMATCH';

/**
 * The parameters related to User acquisition metadata.
 */
export interface PlaytimeParams {
    /**
     * User acquisition network where the user came from.
     */
    uaNetwork?: string | null;

    /**
     * User acquisition channel where the user came from; e.g. video, offerwall.
     */
    uaChannel?: string | null;

    /**
     * User acquisition sub ID in encrypted form where the user came from.
     */
    uaSubPublisherEncrypted?: string | null;

    /**
     * User acquisition sub ID in clear text where the user came from.
     */
    uaSubPublisherCleartext?: string | null;

    /**
     * The placement of the Playtime experience; e.g. “home screen”, “more options menu”.
     */
    placement?: string | null;

    /**
     * Promotion tag to trigger user-level promotions.
     */
    promotionTag?: string | null;
}

/**
 * This interface is used to pass additional identifiers of the user.
 */
export interface PlaytimeExtension {
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
 * The options passed to Playtime methods.
 */
export interface PlaytimeOptions {
    /**
     * A custom identifier you must assign to each user.
     */
    userId?: string | null;

    /**
     * The SDK hash for initialization.
     */
    sdkHash?: string | null;

    /**
     * User profile for targeting.
     */
    userProfile?: PlaytimeUserProfile | null;

    /**
     * User acquisition parameters.
     */
    params?: PlaytimeParams | null;

    /**
     * Extension IDs visible in S2S payouts.
     */
    extensions?: PlaytimeExtension | null;

    /**
     * A list of tokens.
     */
    tokens?: string[] | null;
}

/**
 * The model that represents the status for SDK
 */
export interface PlaytimeStatus {
    /**
     * A flag indicating if the SDK is successfully initialized.
     */
    isInitialized: boolean;

    /**
     * Additional information about the SDK status.
     */
    details: PlaytimeStatusDetails;
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
 * The gender of the user.
 */
export type PlaytimeGender = 'male' | 'female' | 'unknown';

/**
 * Additional user information.
 */
export interface PlaytimeUserProfile {
    /**
     * User gender. Valid options are male, female, unknown.
     */
    gender?: PlaytimeGender | null;

    /**
     * ISO8601 timestamp designating user’s birthday, e.g. 2025-06-26T14:45:30.123Z.
     */
    birthday?: string | null;
}

/**
 * The entry point of adjoe Playtime SDK.
 */
declare namespace _default {
    /**
     * The user can see the teaser, for example the button via which he can access the Playtime SDK from the SDK App.
     * Trigger this event when the teaser has been successfully rendered and would successfully
     * redirect the user to the Playtime SDK. It should be triggered regardless of whether the user
     * has actually clicked the teaser or not. This event is mostly appropriate for uses, in
     * which the functionality of the SDK App and SDK are kept separate to a relevant degree.
     */
    let EVENT_TEASER_SHOWN: number;
    
    /**
     * Initializes the Playtime SDK.
     * You must initialize the Playtime SDK before you can use any of its features.
     * The initialization will run asynchronously in the background
     * 
     * Supported only on Android.
     * @param apiKey Your playtime SDK hash.
     * @param options An object to pass additional options to the Playtime SDK when initializing.
     * @param uaNetwork The uaNetwork value
     * @param uaChannel The uaChannel value
     */
    function init(apiKey: string, options?: {
        userId?: string;
        playtimeParams?: PlaytimeParams;
        applicationProcessName?: string;
        playtimeExtension?: PlaytimeExtension;
        playtimeUserProfile?: PlaytimeUserProfile;
    }, uaNetwork?: string, uaChannel?: string): Promise<void>;

    /**
     * Opens a new activity that shows catalog.
     * 
     * Supported only on Android.
     * @param params The PlaytimeParams that holds the user acquisition (UA) paramaters and
     * placement (optional).
     * @param uaChannel The uaChannel value.
     */
    function showCatalog(params?: PlaytimeParams, uaChannel?: string): Promise<void>;

    /**
     * Opens a new activity that shows catalog.
     * 
     * Supported on both Android and iOS.
     * @param options An object to pass additional options.
     */
    function showCatalogWithOptions(options: PlaytimeOptions): Promise<void>;

    /**
     * Sets the Playtime options.
     * 
     * Supported on both Android and iOS.
     * @param options An object to pass additional options.
     */
    function setPlaytimeOptions(options: PlaytimeOptions): Promise<void>;

    /**
     * Sets the User-Acquisition (UA) parameters.
     * 
     * Supported only on Android.
     * @param params The PlaytimeParams that holds the user acquisition (UA) paramaters and
     * placement (optional).
     */
    function setUAParams(params: PlaytimeParams): Promise<void>;

    /**
     * Returns the version code of the Playtime SDK.
     * 
     * Supported only on Android.
     * @return The version code of the Playtime SDK.
     */
    function getVersion(): Promise<string>;

    /**
     * Returns the version name of the Playtime SDK.
     * 
     * Supported only on Android.
     * @return The version name of the Playtime SDK.
     */
    function getVersionName(): Promise<string>;

    /**
     * Checks whether the Playtime SDK is initialized.
     * 
     * Supported only on Android.
     * @return `true` when it is initialized, `false` otherwise.
     */
    function isInitialized(): Promise<boolean>;

    /**
     * Checks whether the user has accepted the Playtime Terms of Service (TOS).
     * 
     * Supported only on Android.
     * @return `true` when the user has accepted the TOS, `false` otherwise.
     */
    function hasAcceptedTOS(): Promise<boolean>;

    /**
     * Checks whether the user has given access to the usage statistics.
     * 
     * Supported only on Android.
     * @return `true` when the user has given access, `false` otherwise.
     */
    function hasAcceptedUsagePermission(): Promise<boolean>;

    /**
     * Returns the unique ID of the user by which he is identified within the Playtime services.
     * 
     * Supported only on Android.
     * @return The user's unique ID.
     */
    function getUserId(): Promise<string>;
    
    /**
     * Sends a user event to Playtime.
     * These events help to improve the accuracy of the app recommendations for the user.
     * 
     * Supported only on Android.
     * @param event The ID of the event.
     * @param extra This must be the application iD of the app to which the video belongs, otherwise `null`.
     * @param params The PlaytimeParams that holds the user acquisition (UA) paramaters and
     * placement (optional).
     * @param uaChannel The uaChannel value.
     */
    function sendEvent(event: number, extra?: string, params?: PlaytimeParams, uaChannel?: string): Promise<void>;

    /**
     * Recieve the status of SDK.
     * 
     * Supported on Android and iOS.
     * @returns The status of SDK.
     */
    function getStatus(): Promise<PlaytimeStatus>;

    /**
     * Deinitialize the SDK. This releases the resources taken up by internal components.
     * 
     * Supported on both Android and iOS.
     */
    function teardown(): Promise<void>;
}

export default _default;
//# sourceMappingURL=Playtime.d.ts.map
