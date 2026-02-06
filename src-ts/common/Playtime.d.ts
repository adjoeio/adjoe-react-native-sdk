import { PlaytimeParams } from './PlaytimeParams';
import { PlaytimeExtension } from './PlaytimeExtension';
import { PlaytimeUserProfile } from './PlaytimeUserProfile';
import { PlaytimeOptions } from './PlaytimeOptions';
import { PlaytimeStatus } from './PlaytimeStatus';

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
}

export { PlaytimeParams } from './PlaytimeParams';
export { PlaytimeExtension } from './PlaytimeExtension';
export { PlaytimeUserProfile } from './PlaytimeUserProfile';
export { PlaytimeReward } from './PlaytimeReward';
export { PlaytimeOptions } from './PlaytimeOptions';
export { PlaytimeStatus } from './PlaytimeStatus';

export default _default;
//# sourceMappingURL=Playtime.d.ts.map
