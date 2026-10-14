package io.adjoe.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = RNPlaytimeSdkImpl.NAME)
class RNPlaytimeSdkModule(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    private val implementation = RNPlaytimeSdkImpl(reactContext)

    override fun getName(): String = RNPlaytimeSdkImpl.NAME

    @ReactMethod
    fun initPlaytime(options: ReadableMap?, promise: Promise) {
        implementation.initPlaytime(options, promise)
    }

    @ReactMethod
    fun showCatalog(catalogOptions: ReadableMap?, promise: Promise) {
        implementation.showCatalog(catalogOptions, promise)
    }

    @ReactMethod
    fun getStatus(promise: Promise) {
        implementation.getStatus(promise)
    }

    @ReactMethod
    fun teardown(promise: Promise) {
        implementation.teardown(promise)
    }

    @ReactMethod
    fun addEventListener(promise: Promise) {
        implementation.addEventListener(promise)
    }

    @ReactMethod
    fun removeEventListener(promise: Promise) {
        implementation.removeEventListener(promise)
    }

    @ReactMethod
    fun addListener(eventName: String) {
        // Required so the JS NativeEventEmitter has a native counterpart on the old
        // architecture. Event delivery is driven by the SDK listener.
    }

    @ReactMethod
    fun removeListeners(count: Double) {
        // Required by the NativeEventEmitter contract; see addListener.
    }

    @ReactMethod
    fun executeImpressionEvent(catalogPlacement: ReadableMap?, promise: Promise) {
        implementation.executeImpressionEvent(catalogPlacement, promise)
    }

    companion object {
        const val NAME = RNPlaytimeSdkImpl.NAME
    }
}
