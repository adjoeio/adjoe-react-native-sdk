/**
 * The actionable category of a `PlaytimeException`.
 *
 * These are the values sent by the native layer. Any value the JS layer does
 * not recognize is normalized to `UNKNOWN`.
 */
export const PlaytimeErrorType = Object.freeze({
  /** Transport-layer failure (timeout, DNS, SSL, connect). */
  NETWORK: 'NETWORK',
  /** Network failure attributed to an active VPN. */
  NETWORK_VPN: 'NETWORK_VPN',
  /** The server responded with a non-2xx status. */
  API: 'API',
  /** The response body could not be parsed or had an unexpected shape. */
  RESPONSE_FORMAT: 'RESPONSE_FORMAT',
  /** SDK misconfiguration / integration error. */
  SDK_CONFIG: 'SDK_CONFIG',
  /** Fallback category for uncategorized errors. */
  UNKNOWN: 'UNKNOWN',
});

/**
 * The unified error type for all Playtime rejected promises.
 *
 * Every promise-returning Playtime method rejects with an instance of this
 * class, so publishers can branch on `errorType` / `isRetryable` instead of
 * parsing error strings:
 *
 * ```js
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
  constructor(message, { code, errorType, isRetryable, cause } = {}) {
    super(message);

    this.name = 'PlaytimeException';

    /**
     * A stable, publisher-parsable error code.
     * @type {string|null}
     */
    this.code = code != null ? code : null;

    /**
     * The actionable category of the error.
     * @type {string}
     */
    this.errorType = normalizeErrorType(errorType);

    /**
     * Flag indicating whether the failed operation is worth retrying. Defaults to false.
     * @type {boolean}
     */
    this.isRetryable = isRetryable === true;

    /**
     * The original error raised by the bridge, kept for debugging.
     * @type {Error|undefined}
     */
    if (cause !== undefined) {
      this.cause = cause;
    }

    // Preserve the bridge's native stack information when available.
    if (cause != null) {
      if (cause.stack) {
        this.stack = cause.stack;
      }
      if (cause.nativeStackAndroid) {
        this.nativeStackAndroid = cause.nativeStackAndroid;
      }
      if (cause.nativeStackIOS) {
        this.nativeStackIOS = cause.nativeStackIOS;
      }
    }

    // Required so `instanceof` works when this file is transpiled down to ES5.
    if (Object.setPrototypeOf) {
      Object.setPrototypeOf(this, PlaytimeException.prototype);
    }
  }

  /**
   * Normalizes an error raised by the native bridge into a `PlaytimeException`.
   *
   * The native layer sends the structured fields in `userInfo`. Anything
   * missing or unrecognized falls back to `UNKNOWN` / `false` / `null` so a
   * malformed rejection can never produce an undefined `errorType`.
   *
   * @param {any} error the raw error from a rejected bridge promise.
   * @returns {PlaytimeException}
   */
  static from(error) {
    if (error instanceof PlaytimeException) {
      return error;
    }

    const userInfo = (error && error.userInfo) || {};
    const message =
      (error && error.message) || 'Unknown error occurred';

    return new PlaytimeException(message, {
      code: userInfo.code,
      errorType: userInfo.errorType,
      isRetryable: userInfo.isRetryable,
      cause: error,
    });
  }
}

/**
 * Type guard for `PlaytimeException`.
 *
 * @param {any} error
 * @returns {boolean}
 */
export const isPlaytimeException = (error) => error instanceof PlaytimeException;

const normalizeErrorType = (errorType) =>
  typeof errorType === 'string' && Object.prototype.hasOwnProperty.call(PlaytimeErrorType, errorType)
    ? errorType
    : PlaytimeErrorType.UNKNOWN;

/**
 * Wraps a bridge promise so it rejects with a `PlaytimeException`.
 *
 * @param {Promise<any>} promise
 * @returns {Promise<any>}
 */
export const wrapPlaytimePromise = (promise) =>
  Promise.resolve(promise).catch((error) => {
    throw PlaytimeException.from(error);
  });
