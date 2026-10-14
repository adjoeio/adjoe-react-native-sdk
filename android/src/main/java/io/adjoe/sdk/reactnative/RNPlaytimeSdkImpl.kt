package io.adjoe.sdk.reactnative

import android.app.Activity

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

import io.adjoe.sdk.Playtime
import io.adjoe.sdk.PlaytimeEvent
import io.adjoe.sdk.PlaytimeEventListener
import io.adjoe.sdk.PlaytimeException
import io.adjoe.sdk.PlaytimeInitListener
import io.adjoe.sdk.PlaytimeCatalogLaunchListener
import io.adjoe.sdk.PlaytimeImpressionEventListener

class RNPlaytimeSdkImpl(
    private val reactContext: ReactApplicationContext
) {

    /**
     * The listener currently registered with the SDK. It is retained so that it stays
     * registered and can be released when the publisher removes it again.
     */
    private var eventListener: PlaytimeEventListener? = null

    fun initPlaytime(options: ReadableMap?, promise: Promise) {
        try {
            val initOptions = BasicUtil.constructInitOptionsFrom(options)
            Playtime.initPlaytime(initOptions, object : PlaytimeInitListener {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: PlaytimeException) {
                    ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(error))
                }
            })
        } catch (e: Exception) {
            ErrorsUtil.reject(promise, e)
        }
    }

    fun showCatalog(catalogOptions: ReadableMap?, promise: Promise) {
        try {
            val options = BasicUtil.constructCatalogOptionsFrom(catalogOptions)
            val localActivity: Activity? = reactContext.getCurrentActivity()
            if (localActivity == null) {
                ErrorsUtil.reject(promise, ErrorsUtil.getActivityIsNullError())
                return
            }
            Playtime.showCatalog(localActivity, options, object : PlaytimeCatalogLaunchListener {
                override fun onCatalogLaunched() {
                    promise.resolve(null)
                }

                override fun onError(error: PlaytimeException) {
                    ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(error))
                }
            })
        } catch (e: Exception) {
            ErrorsUtil.reject(promise, e)
        }
    }

    fun getStatus(promise: Promise) {
        try {
            val status = Playtime.getStatus()
            promise.resolve(BasicUtil.statusToReadableMap(status))
        } catch (e: Exception) {
            ErrorsUtil.reject(promise, e)
        }
    }

    fun teardown(promise: Promise) {
        try {
            Playtime.teardown()
            promise.resolve(null)
        } catch (e: Exception) {
            ErrorsUtil.reject(promise, e)
        }
    }

    /**
     * Registers a listener which forwards every Playtime SDK event to JavaScript as a
     * device event, since a publisher's JS listener object cannot cross the TurboModule
     * bridge. The JS side bridges the device event back to the listener's `didReceive`
     * callback via a NativeEventEmitter.
     */
    fun addEventListener(promise: Promise) {
        try {
            val listener = object : PlaytimeEventListener {
                override fun didReceive(event: PlaytimeEvent) {
                    emitPlaytimeEvent(event)
                }
            }
            Playtime.addEventListener(listener)
            this.eventListener = listener
            promise.resolve(null)
        } catch (e: Exception) {
            ErrorsUtil.reject(promise, e)
        }
    }

    fun removeEventListener(promise: Promise) {
        try {
            Playtime.removeEventListener()
            eventListener = null
            promise.resolve(null)
        } catch (e: Exception) {
            ErrorsUtil.reject(promise, e)
        }
    }

    fun executeImpressionEvent(catalogPlacement: ReadableMap?, promise: Promise) {
        try {
            val placement = BasicUtil.constructCatalogPlacementFrom(catalogPlacement)
            if (placement == null) {
                ErrorsUtil.reject(
                    promise,
                    ErrorsUtil.getFunctionError(
                        "executeImpressionEvent",
                        "The catalog placement is missing or invalid."
                    )
                )
                return
            }
            Playtime.executeImpressionEvent(placement, object : PlaytimeImpressionEventListener {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: PlaytimeException) {
                    ErrorsUtil.reject(promise, ErrorsUtil.toPlaytimeException(error))
                }
            })
        } catch (e: Exception) {
            ErrorsUtil.reject(promise, e)
        }
    }

    /**
     * Forwards a Playtime SDK event to JavaScript as a device event under
     * [EVENT_PLAYTIME_EVENT]. The JS NativeEventEmitter subscription relays it to the
     * publisher's registered listener.
     */
    private fun emitPlaytimeEvent(event: PlaytimeEvent) {
        try {
            reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(EVENT_PLAYTIME_EVENT, BasicUtil.playtimeEventToReadableMap(event))
        } catch (e: Exception) {
            // The JS context may already be gone; dropping the event is the only option here.
        }
    }

    companion object {
        const val NAME = "RNPlaytimeSdk"

        /** The device event name under which SDK events are delivered to JavaScript. */
        const val EVENT_PLAYTIME_EVENT = "onPlaytimeEvent"
    }
}
