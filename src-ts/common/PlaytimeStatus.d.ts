/**
 * The model that represents the status for SDK
 */
interface PlaytimeStatusDetails {
    /**
     * A flag indicating if the current user is marked as a fraud user.
     */
    isFraud: boolean;
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
