import Foundation
import React
import PlaytimeSDK

enum ErrorsUtil {

    /// Key names of the `userInfo` payload. Must stay in sync with the JS/Android side.
    enum UserInfoKey {
        static let code = "code"
        static let errorType = "errorType"
        static let isRetryable = "isRetryable"
    }

    /// The `NSError` domain of the rejection payload.
    ///
    /// `PlaytimeException` is a plain `NSObject`, so it carries no domain of its
    /// own and the wrapper supplies one.
    private static let errorDomain = "io.adjoe.sdk.reactnative.PlaytimeException"

    /// `PlaytimeException.code` is non-optional, so wrapper-created exceptions
    /// pass an empty code, which is serialized as `null` for the JS side.
    private static let unspecifiedCode = ""

    /// `PlaytimeException.isRetryable` is non-optional, so wrapper-created
    /// exceptions pass this default when the flag cannot be populated.
    private static let unspecifiedIsRetryable = false

    private static let unknownErrorMessage = "Unknown error occurred"

    // MARK: - Factories

    /// A feature that is not available on iOS was invoked.
    static func notImplementedError(_ message: String = "Function is not implemented") -> PlaytimeException {
        return PlaytimeException(
            code: unspecifiedCode,
            errorType: .sdkConfig,
            isRetryable: unspecifiedIsRetryable,
            message: message
        )
    }

    /// Builds a generic error for a failed wrapper function.
    static func getFunctionError(_ functionName: String, reason: String? = nil) -> PlaytimeException {
        guard let reason = reason, !reason.isEmpty else {
            return PlaytimeException(
                code: unspecifiedCode,
                errorType: .unknown,
                isRetryable: unspecifiedIsRetryable,
                message: "Error occurred during \(functionName)"
            )
        }

        return PlaytimeException(
            code: unspecifiedCode,
            errorType: .unknown,
            isRetryable: unspecifiedIsRetryable,
            message: "Error occurred during \(functionName): \(reason)"
        )
    }

    // MARK: - Coercion

    /// Coerces an arbitrary `Error` into a ``PlaytimeException``.
    ///
    /// If the error already is a ``PlaytimeException`` it is returned unchanged.
    static func toPlaytimeException(_ error: Error?) -> PlaytimeException {
        if let playtimeException = error as? PlaytimeException {
            return playtimeException
        }

        guard let error = error else {
            return PlaytimeException(
                code: unspecifiedCode,
                errorType: .unknown,
                isRetryable: unspecifiedIsRetryable,
                message: unknownErrorMessage
            )
        }

        return PlaytimeException(
            code: unspecifiedCode,
            errorType: .unknown,
            isRetryable: unspecifiedIsRetryable,
            message: error.localizedDescription
        )
    }

    // MARK: - Category

    /// Maps the SDK's `Int`-backed category onto the UPPER_SNAKE wire value
    /// shared with Android and JavaScript.
    ///
    /// The raw value must not be used: it is an ordinal, whereas the JS
    /// `PlaytimeErrorType` enum is deliberately string-based.
    private static func errorTypeName(_ errorType: PlaytimeErrorType) -> String {
        switch errorType {
        case .network:
            return "NETWORK"
        case .networkVPN:
            return "NETWORK_VPN"
        case .api:
            return "API"
        case .responseFormat:
            return "RESPONSE_FORMAT"
        case .sdkConfig:
            return "SDK_CONFIG"
        case .unknown:
            return "UNKNOWN"
        @unknown default:
            return "UNKNOWN"
        }
    }

    // MARK: - Rejection

    /// Rejects with the structured representation of `exception`.
    ///
    /// `underlyingError` is the error the exception was coerced from, if any. It
    /// is attached for debugging only and does not cross the bridge.
    static func reject(
        _ reject: RCTPromiseRejectBlock,
        _ exception: PlaytimeException,
        underlyingError: Error? = nil
    ) {
        var userInfo: [String: Any] = [:]

        userInfo[UserInfoKey.code] = exception.code.isEmpty ? NSNull() : exception.code
        userInfo[UserInfoKey.errorType] = errorTypeName(exception.errorType)
        userInfo[UserInfoKey.isRetryable] = exception.isRetryable

        let message = exception.message.isEmpty ? unknownErrorMessage : exception.message
        userInfo[NSLocalizedDescriptionKey] = message

        if let underlyingError = underlyingError {
            userInfo[NSUnderlyingErrorKey] = underlyingError as NSError
        }

        let error = NSError(
            domain: errorDomain,
            code: 0,
            userInfo: userInfo
        )

        reject(errorTypeName(exception.errorType), message, error)
    }

    /// Convenience: coerce any error and reject with it.
    static func reject(_ reject: RCTPromiseRejectBlock, _ error: Error?) {
        self.reject(reject, toPlaytimeException(error), underlyingError: error)
    }

    /// Coerce any error and reject with it, falling back to `context` as the
    /// message when the underlying error carries none, while preserving the
    /// classification of that error.
    static func reject(_ reject: RCTPromiseRejectBlock, context: String, _ error: Error?) {
        let exception = toPlaytimeException(error)
        let message = exception.message.isEmpty ? context : exception.message

        self.reject(
            reject,
            PlaytimeException(
                code: exception.code,
                errorType: exception.errorType,
                isRetryable: exception.isRetryable,
                message: message
            ),
            underlyingError: error
        )
    }

    /// Convenience: reject with a generic function error.
    static func reject(_ reject: RCTPromiseRejectBlock, function: String, reason: String? = nil) {
        self.reject(reject, getFunctionError(function, reason: reason))
    }

    /// Convenience: reject because the operation is unsupported on iOS.
    static func rejectUnsupported(_ name: String, _ reject: RCTPromiseRejectBlock) {
        self.reject(reject, notImplementedError("\(name) is not supported on iOS"))
    }
}
