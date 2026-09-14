package io.adjoe.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = PlaytimeProtectionImpl.NAME)
class RNPlaytimeProtection(
    reactContext: ReactApplicationContext
) : NativePlaytimeProtectionSpec(reactContext) {

    private val implementation = PlaytimeProtectionImpl(reactContext)

    override fun getName(): String = PlaytimeProtectionImpl.NAME

    override fun faceVerification(promise: Promise) =
        implementation.faceVerification(promise)

    override fun faceVerificationStatus(promise: Promise) =
        implementation.faceVerificationStatus(promise)

    companion object {
        const val NAME = PlaytimeProtectionImpl.NAME
    }
}
