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

    override fun addEventListener(promise: Promise) {
        implementation.addEventListener(promise)
    }

    override fun removeEventListener(promise: Promise) {
        implementation.removeEventListener(promise)
    }

    override fun addListener(eventName: String) {
        // Required by the generated spec so the NativeEventEmitter has a native counterpart.
        // Event delivery is driven by the SDK listener, so no per-subscription work is needed.
    }

    override fun removeListeners(count: Double) {
        // Required by the generated spec; see addListener.
    }

    override fun executeImpressionEvent(catalogPlacement: ReadableMap?, promise: Promise) {
        implementation.executeImpressionEvent(catalogPlacement, promise)
    }

    companion object {
        const val NAME = RNPlaytimeSdkImpl.NAME
    }
}
