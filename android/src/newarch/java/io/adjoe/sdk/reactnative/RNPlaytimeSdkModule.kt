package io.adjoe.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = RNPlaytimeSdkImpl.NAME)
class RNPlaytimeSdkModule(
    reactContext: ReactApplicationContext
) : NativeRNPlaytimeSdkSpec(reactContext) {

    private val implementation = RNPlaytimeSdkImpl(reactContext)

    override fun getName(): String = RNPlaytimeSdkImpl.NAME

    override fun initPlaytime(options: ReadableMap?, promise: Promise) {
        implementation.initPlaytime(options, promise)
    }

    override fun showCatalog(catalogOptions: ReadableMap?, promise: Promise) {
        implementation.showCatalog(catalogOptions, promise)
    }

    override fun getStatus(promise: Promise) {
        implementation.getStatus(promise)
    }

    override fun teardown(promise: Promise) {
        implementation.teardown(promise)
    }

    override fun executeImpressionEvent(catalogPlacement: ReadableMap?, promise: Promise) {
        implementation.executeImpressionEvent(catalogPlacement, promise)
    }

    companion object {
        const val NAME = RNPlaytimeSdkImpl.NAME
    }
}
