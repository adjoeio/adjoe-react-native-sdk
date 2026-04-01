/**
 * The model that represents the status for SDK
 */
interface PlaytimeStatusDetails {
    /**
     * A flag indicating if the current user is marked as a fraud user.
     */
    isFraud: boolean;

    /**
     * Indicates whether the user is eligible to request campaigns.
     */
    campaignsAvailable: Boolean;

    /**
     * Provides optional context explaining the eligibility state.
     */
    campaignsState: PlaytimeCampaignsState[];
}

type PlaytimeCampaignsState = 'READY' | 'BLOCKED' | 'VPN_DETECTED' | 'GEO_MISMATCH';

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
