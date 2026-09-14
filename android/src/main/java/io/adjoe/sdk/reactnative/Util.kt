package io.adjoe.sdk.reactnative

import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap

fun WritableMap.putIntOrNull(value: Int?, key: String) {
    if (value == null) {
        putNull(key)
    } else {
        putInt(key, value)
    }
}

fun WritableMap.putFloatOrNull(value: Float?, key: String) {
    if (value == null) {
        putNull(key)
    } else {
        putDouble(key, value.toDouble())
    }
}

fun WritableMap.putBooleanOrNull(value: Boolean?, key: String) {
    if (value == null) {
        putNull(key)
    } else {
        putBoolean(key, value)
    }
}

fun ReadableMap.extractTokens(): List<String> {
    val tokens = ArrayList<String>()
    try {
        val tokensArray = getArray("tokens") ?: return tokens
        for (i in 0 until tokensArray.size()) {
            tokensArray.getString(i)?.let { tokens.add(it) }
        }
    } catch (ignore: Exception) {
    }
    return tokens
}
