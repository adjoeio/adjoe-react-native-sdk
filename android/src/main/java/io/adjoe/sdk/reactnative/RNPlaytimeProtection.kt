package io.adjoe.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

import io.adjoe.sdk.protection.PlaytimeProtection

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Kotlin TurboModule implementation of `PlaytimeProtection`. Extends the
 * Codegen-generated [NativePlaytimeProtectionSpec] abstract class.
 *
 * The native API reports its outcome through a multi-method callback rather than
 * a success/failure pair, and most of those outcomes are not errors (the user
 * cancelled, the review is still pending, ...). Each callback method is
 * therefore mapped onto a string constant that the promise resolves with, and
 * only `onError` rejects. The constants are shared with the JS layer through
 * `FaceVerificationResult` / `FaceVerificationStatus`.
 */
@ReactModule(name = RNPlaytimeProtection.NAME)
class RNPlaytimeProtection(
    private val reactContext: ReactApplicationContext
) : NativePlaytimeProtectionSpec(reactContext) {

    override fun getName(): String = NAME

    @Suppress("DEPRECATION")
    override fun faceVerification(promise: Promise) {
        try {
            val activity = currentActivity
            if (activity == null) {
                ErrorsUtil.reject(promise, ErrorsUtil.getActivityIsNullError())
                return
            }

            // The SDK does not guarantee that exactly one callback method is
            // invoked, so the promise is settled at most once.
            val settled = AtomicBoolean(false)
            val resolveOnce = { result: String ->
                if (settled.compareAndSet(false, true)) {
                    promise.resolve(result)
                }
            }

            PlaytimeProtection.faceVerification(
                activity,
                object : PlaytimeProtection.FaceVerificationCallback {
                    override fun onSuccess() {
                        resolveOnce(FaceVerificationResult.SUCCESS)
                    }

                    override fun onAlreadyVerified() {
                        resolveOnce(FaceVerificationResult.ALREADY_VERIFIED)
                    }

                    override fun onCancel() {
                        resolveOnce(FaceVerificationResult.CANCELLED)
                    }

                    override fun onNotInitialized() {
                        resolveOnce(FaceVerificationResult.NOT_INITIALIZED)
                    }

                    override fun onTosIsNotAccepted() {
                        resolveOnce(FaceVerificationResult.TOS_NOT_ACCEPTED)
                    }

                    override fun onLivenessCheckFailed() {
                        resolveOnce(FaceVerificationResult.LIVENESS_CHECK_FAILED)
                    }

                    override fun onPendingReview() {
                        resolveOnce(FaceVerificationResult.PENDING_REVIEW)
                    }

                    override fun onMaxAttemptsReached() {
                        resolveOnce(FaceVerificationResult.MAX_ATTEMPTS_REACHED)
                    }

                    override fun onError(exception: Exception?) {
                        if (settled.compareAndSet(false, true)) {
                            ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(exception))
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            // Covers `PlaytimeNotInitializedException` as well as the
            // `NoClassDefFoundError` raised when the optional face verification
            // artifact is not on the publisher's classpath.
            ErrorsUtil.reject(promise, t)
        }
    }

    @Suppress("DEPRECATION")
    override fun faceVerificationStatus(promise: Promise) {
        try {
            val settled = AtomicBoolean(false)
            val resolveOnce = { status: String ->
                if (settled.compareAndSet(false, true)) {
                    promise.resolve(status)
                }
            }

            PlaytimeProtection.faceVerificationStatus(
                object : PlaytimeProtection.FaceVerificationStatusCallback {
                    override fun onVerified() {
                        resolveOnce(FaceVerificationStatus.VERIFIED)
                    }

                    override fun onNotVerified() {
                        resolveOnce(FaceVerificationStatus.NOT_VERIFIED)
                    }

                    override fun onNotInitialized() {
                        resolveOnce(FaceVerificationStatus.NOT_INITIALIZED)
                    }

                    override fun onTosIsNotAccepted() {
                        resolveOnce(FaceVerificationStatus.TOS_NOT_ACCEPTED)
                    }

                    override fun onPendingReview() {
                        resolveOnce(FaceVerificationStatus.PENDING_REVIEW)
                    }

                    override fun onMaxAttemptsReached() {
                        resolveOnce(FaceVerificationStatus.MAX_ATTEMPTS_REACHED)
                    }

                    override fun onError(exception: Exception?) {
                        if (settled.compareAndSet(false, true)) {
                            ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(exception))
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            ErrorsUtil.reject(promise, t)
        }
    }

    /** Wire values of the JS `FaceVerificationResult` enum. */
    private object FaceVerificationResult {
        const val SUCCESS = "SUCCESS"
        const val ALREADY_VERIFIED = "ALREADY_VERIFIED"
        const val CANCELLED = "CANCELLED"
        const val NOT_INITIALIZED = "NOT_INITIALIZED"
        const val TOS_NOT_ACCEPTED = "TOS_NOT_ACCEPTED"
        const val LIVENESS_CHECK_FAILED = "LIVENESS_CHECK_FAILED"
        const val PENDING_REVIEW = "PENDING_REVIEW"
        const val MAX_ATTEMPTS_REACHED = "MAX_ATTEMPTS_REACHED"
    }

    /** Wire values of the JS `FaceVerificationStatus` enum. */
    private object FaceVerificationStatus {
        const val VERIFIED = "VERIFIED"
        const val NOT_VERIFIED = "NOT_VERIFIED"
        const val NOT_INITIALIZED = "NOT_INITIALIZED"
        const val TOS_NOT_ACCEPTED = "TOS_NOT_ACCEPTED"
        const val PENDING_REVIEW = "PENDING_REVIEW"
        const val MAX_ATTEMPTS_REACHED = "MAX_ATTEMPTS_REACHED"
    }

    companion object {
        const val NAME = "PlaytimeProtection"
    }
}
