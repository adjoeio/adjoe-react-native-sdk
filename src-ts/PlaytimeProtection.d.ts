/**
 * The outcome of a face verification flow.
 */
export enum FaceVerificationResult {
    /**
     * The user completed the face verification flow successfully.
     */
    SUCCESS = 'SUCCESS',

    /**
     * The user had already been verified, so the flow was not shown again.
     */
    ALREADY_VERIFIED = 'ALREADY_VERIFIED',

    /**
     * The user dismissed the face verification flow.
     */
    CANCELLED = 'CANCELLED',

    /**
     * The SDK is not initialized. Call `Playtime.initPlaytime` first.
     */
    NOT_INITIALIZED = 'NOT_INITIALIZED',

    /**
     * The user has not accepted the Playtime terms of service.
     */
    TOS_NOT_ACCEPTED = 'TOS_NOT_ACCEPTED',

    /**
     * The liveness check did not pass.
     */
    LIVENESS_CHECK_FAILED = 'LIVENESS_CHECK_FAILED',

    /**
     * The submitted verification is awaiting a manual review.
     */
    PENDING_REVIEW = 'PENDING_REVIEW',

    /**
     * The user has used up all the available verification attempts.
     */
    MAX_ATTEMPTS_REACHED = 'MAX_ATTEMPTS_REACHED'
}

/**
 * The current face verification status of the user.
 */
export enum FaceVerificationStatus {
    /**
     * The user is verified.
     */
    VERIFIED = 'VERIFIED',

    /**
     * The user is not verified yet.
     */
    NOT_VERIFIED = 'NOT_VERIFIED',

    /**
     * The SDK is not initialized. Call `Playtime.initPlaytime` first.
     */
    NOT_INITIALIZED = 'NOT_INITIALIZED',

    /**
     * The user has not accepted the Playtime terms of service.
     */
    TOS_NOT_ACCEPTED = 'TOS_NOT_ACCEPTED',

    /**
     * The submitted verification is awaiting a manual review.
     */
    PENDING_REVIEW = 'PENDING_REVIEW',

    /**
     * The user has used up all the available verification attempts.
     */
    MAX_ATTEMPTS_REACHED = 'MAX_ATTEMPTS_REACHED'
}

/**
 * The entry point for the protection features of the Adjoe Playtime SDK.
 *
 * Supported on Android only. Calling any of these methods on iOS rejects as unsupported.
 */
declare namespace _default {
    /**
     * Start the face verification flow.
     *
     * Supported on Android only. Calling it on iOS rejects as unsupported.
     * @returns The outcome of the flow. Rejects with a `PlaytimeException` only when the flow failed with an error.
     * @deprecated faceVerification is deprecated.
     */
    function faceVerification(): Promise<FaceVerificationResult>;

    /**
     * Get the current face verification status of the user.
     *
     * Supported on Android only. Calling it on iOS rejects as unsupported.
     * @returns The current status. Rejects with a `PlaytimeException` only when the status could not be determined.
     * @deprecated faceVerificationStatus is deprecated.
     */
    function faceVerificationStatus(): Promise<FaceVerificationStatus>;
}

export default _default;
