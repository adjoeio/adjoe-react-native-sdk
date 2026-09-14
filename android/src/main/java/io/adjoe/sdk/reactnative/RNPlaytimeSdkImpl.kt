package io.adjoe.sdk.reactnative

import android.app.Activity

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap

import io.adjoe.sdk.Playtime
import io.adjoe.sdk.PlaytimeException
import io.adjoe.sdk.PlaytimeInitListener
import io.adjoe.sdk.PlaytimeCatalogLaunchListener
import io.adjoe.sdk.PlaytimeImpressionEventListener

class RNPlaytimeSdkImpl(
    private val reactContext: ReactApplicationContext
) {

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

    companion object {
        const val NAME = "RNPlaytimeSdk"
    }
}
