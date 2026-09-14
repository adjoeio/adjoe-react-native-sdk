/**
 * The actionable category of a [PlaytimeException].
 *
 * Any value not recognized by the wrapper is normalized to `UNKNOWN`.
 */
export enum PlaytimeErrorType {
    /** Transport-layer failure (timeout, DNS, SSL, connect). */
    NETWORK = 'NETWORK',
    /** Network failure attributed to an active VPN. */
    NETWORK_VPN = 'NETWORK_VPN',
    /** The server responded with a non-2xx status. */
    API = 'API',
    /** The response body could not be parsed or had an unexpected shape. */
    RESPONSE_FORMAT = 'RESPONSE_FORMAT',
    /** SDK misconfiguration / integration error. */
    SDK_CONFIG = 'SDK_CONFIG',
    /** Fallback category for uncategorized errors. */
    UNKNOWN = 'UNKNOWN'
}

/**
 * Optional fields used to construct a [PlaytimeException].
 */
export interface PlaytimeExceptionOptions {
    /** A stable, publisher-parsable error code. */
    code?: string | null;
    /** The actionable category of the error. */
    errorType?: PlaytimeErrorType | string | null;
    /** Flag indicating whether the failed operation is worth retrying. Defaults to false. */
    isRetryable?: boolean | null;
    /** The original error raised by the bridge. */
    cause?: unknown;
}

/**
 * The unified error type for all Playtime rejected promises.
 *
 * Every promise-returning Playtime method rejects with an instance of this
 * class, so you can branch on the category instead of parsing error strings:
 *
 * ```ts
 * try {
 *   await PlaytimeStudio.getCampaigns(options);
 * } catch (e) {
 *   if (isPlaytimeException(e) && e.errorType === PlaytimeErrorType.NETWORK && e.isRetryable) {
 *     // retry
 *   }
 * }
 * ```
 */
export class PlaytimeException extends Error {
    constructor(message?: string, options?: PlaytimeExceptionOptions);

    /** Always `'PlaytimeException'`. */
    name: string;

    /** A stable, publisher-parsable error code. */
    code: string | null;

    /** The actionable category of the error. */
    errorType: PlaytimeErrorType;

    /** Flag indicating whether the failed operation is worth retrying. Defaults to false. */
    isRetryable: boolean;

    /** The native Android stack frames, when available. */
    nativeStackAndroid?: unknown;

    /** The native iOS stack symbols, when available. */
    nativeStackIOS?: unknown;

    /**
     * Normalizes an error raised by the native bridge into a [PlaytimeException].
     * Missing or unrecognized fields fall back to `UNKNOWN` / `false` / `null`.
     */
    static from(error: unknown): PlaytimeException;
}

/**
 * Type guard for [PlaytimeException].
 */
export function isPlaytimeException(error: unknown): error is PlaytimeException;

/**
 * Wraps a bridge promise so that it rejects with a [PlaytimeException].
 */
export function wrapPlaytimePromise<T>(promise: Promise<T>): Promise<T>;
