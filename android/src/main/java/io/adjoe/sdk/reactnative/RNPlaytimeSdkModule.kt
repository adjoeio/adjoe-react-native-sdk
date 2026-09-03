package io.adjoe.sdk.reactnative

import android.app.Activity

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule

import io.adjoe.sdk.Playtime
import io.adjoe.sdk.PlaytimeException
import io.adjoe.sdk.PlaytimeInitListener
import io.adjoe.sdk.PlaytimeCatalogLaunchListener

/**
 * Kotlin TurboModule implementation of `RNPlaytimeSdk`. Extends the
 * Codegen-generated [NativeRNPlaytimeSdkSpec] abstract class (which itself
 * extends `ReactContextBaseJavaModule` and implements `TurboModule`).
 */
@ReactModule(name = RNPlaytimeSdkModule.NAME)
class RNPlaytimeSdkModule(
    private val reactContext: ReactApplicationContext
) : NativeRNPlaytimeSdkSpec(reactContext) {

    override fun getName(): String = NAME

    // ---- Basic integration ----

    override fun initPlaytime(options: ReadableMap?, promise: Promise) {
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

    override fun showCatalog(catalogOptions: ReadableMap?, promise: Promise) {
        try {
            val options = BasicUtil.constructCatalogOptionsFrom(catalogOptions)
            val localActivity: Activity? = currentActivity
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

    override fun getStatus(promise: Promise) {
        try {
            val status = Playtime.getStatus()
            promise.resolve(BasicUtil.statusToReadableMap(status))
        } catch (e: Exception) {
            ErrorsUtil.reject(promise, e)
        }
    }

    override fun teardown(promise: Promise) {
        try {
            Playtime.teardown()
            promise.resolve(null)
        } catch (e: Exception) {
            ErrorsUtil.reject(promise, e)
        }
    }

    companion object {
        const val NAME = "RNPlaytimeSdk"
    }
}
