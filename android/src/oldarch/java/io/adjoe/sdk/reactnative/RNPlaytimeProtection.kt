package io.adjoe.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = PlaytimeProtectionImpl.NAME)
class RNPlaytimeProtection(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    private val implementation = PlaytimeProtectionImpl(reactContext)

    override fun getName(): String = PlaytimeProtectionImpl.NAME

    @ReactMethod
    fun faceVerification(promise: Promise) =
        implementation.faceVerification(promise)

    @ReactMethod
    fun faceVerificationStatus(promise: Promise) =
        implementation.faceVerificationStatus(promise)

    companion object {
        const val NAME = PlaytimeProtectionImpl.NAME
    }
}
