package io.adjoe.sdk.reactnative

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider

/**
 * TurboModule-aware package. Extends [BaseReactPackage] and reports all
 * modules as TurboModules through [getReactModuleInfoProvider].
 */
class RNPlaytimeSdkPackage : BaseReactPackage() {

    override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
        return when (name) {
            RNPlaytimeSdkModule.NAME -> RNPlaytimeSdkModule(reactContext)
            RNPlaytimeStudio.NAME -> RNPlaytimeStudio(reactContext)
            RNPlaytimeProtection.NAME -> RNPlaytimeProtection(reactContext)
            else -> null
        }
    }

    override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
        return ReactModuleInfoProvider {
            val isTurboModule = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
            mapOf(
                RNPlaytimeSdkModule.NAME to ReactModuleInfo(
                    RNPlaytimeSdkModule.NAME,
                    RNPlaytimeSdkModule.NAME,
                    false, // canOverrideExistingModule
                    false, // needsEagerInit
                    false, // isCxxModule
                    isTurboModule
                ),
                RNPlaytimeStudio.NAME to ReactModuleInfo(
                    RNPlaytimeStudio.NAME,
                    RNPlaytimeStudio.NAME,
                    false,
                    false,
                    false,
                    isTurboModule
                ),
                RNPlaytimeProtection.NAME to ReactModuleInfo(
                    RNPlaytimeProtection.NAME,
                    RNPlaytimeProtection.NAME,
                    false,
                    false,
                    false,
                    isTurboModule
                )
            )
        }
    }
}
