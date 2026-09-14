package io.adjoe.sdk.reactnative

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableMap
import io.adjoe.sdk.PlaytimeErrorType
import io.adjoe.sdk.PlaytimeException

object ErrorsUtil {

    /** Keys used in the rejection `userInfo` payload. */
    object UserInfoKey {
        const val CODE = "code"
        const val ERROR_TYPE = "errorType"
        const val IS_RETRYABLE = "isRetryable"
    }

    /**
     * Builds a generic error for a failed wrapper function.
     *
     * @param functionName the name of the bridge function that failed.
     * @param reason an optional detail appended to the message.
     */
    fun getFunctionError(functionName: String, reason: String? = null): PlaytimeException {
        if (reason.isNullOrEmpty()) {
            return PlaytimeException(
                message = "Error occurred during $functionName",
                cause = null,
                errorType = PlaytimeErrorType.UNKNOWN,
                isRetryable = false
            )
        }

        return PlaytimeException(
            message = "Error occurred during $functionName: $reason",
            cause = null,
            errorType = PlaytimeErrorType.UNKNOWN,
            isRetryable = false
        )
    }

    /** The current activity was required but unavailable. */
    fun getActivityIsNullError(): PlaytimeException {
        return PlaytimeException(
            message = "activity is null",
            cause = null,
            errorType = PlaytimeErrorType.UNKNOWN,
            isRetryable = false
        )
    }

    /** The campaign referenced by JS could not be resolved from the cache. */
    fun campaignIsNullError(): PlaytimeException {
        return PlaytimeException(
            message = "campaign is null",
            cause = null,
            errorType = PlaytimeErrorType.UNKNOWN,
            isRetryable = false
        )
    }

    /**
     * Coerces an arbitrary [Throwable] into a [PlaytimeException].
     *
     * If the throwable already is a [PlaytimeException] it is returned
     * unchanged, which preserves the classification (`errorType`,
     * `isRetryable`, `code`) produced by the native SDK. This matters because
     * `PlaytimeResponseError.getError()` is statically typed as
     * `java.lang.Exception`, so the concrete type is only known at runtime.
     */
    fun toPlaytimeException(throwable: Throwable?): PlaytimeException {
        if (throwable is PlaytimeException) {
            return throwable
        }

        return PlaytimeException(
            message = throwable?.message ?: "Unknown error occurred",
            cause = throwable,
            errorType = PlaytimeErrorType.UNKNOWN,
            isRetryable = false
        )
    }

    /** Serializes the structured error fields into the rejection `userInfo`. */
    fun toUserInfo(exception: PlaytimeException): WritableMap {
        val userInfo = Arguments.createMap()

        if (exception.code.isNullOrEmpty()) {
            userInfo.putNull(UserInfoKey.CODE)
        } else {
            userInfo.putString(UserInfoKey.CODE, exception.code)
        }
        userInfo.putString(UserInfoKey.ERROR_TYPE, exception.errorType.name)
        userInfo.putBoolean(UserInfoKey.IS_RETRYABLE, exception.isRetryable)

        return userInfo
    }

    /** Rejects [promise] with the structured representation of [exception]. */
    fun reject(promise: Promise, exception: PlaytimeException) {
        promise.reject(
            exception.errorType.name,
            exception.message,
            exception,
            toUserInfo(exception)
        )
    }

    /** Convenience: coerce any throwable and reject with it. */
    fun reject(promise: Promise, throwable: Throwable?) {
        reject(promise, toPlaytimeException(throwable))
    }
}
