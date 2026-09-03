package io.adjoe.sdk.reactnative;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;
import java.util.List;

public class Util {

    // General
    static void putIntOrNull(WritableMap map, Integer value, String key) {
        if (value == null) {
            map.putNull(key);
        } else {
            map.putInt(key, value);
        }
    }

    static void putFloatOrNull(WritableMap map, Float value, String key) {
        if (value == null) {
            map.putNull(key);
        } else {
            map.putDouble(key, value);
        }
    }

    static void putBooleanOrNull(WritableMap map, Boolean value, String key) {
        if (value == null) {
            map.putNull(key);
        } else {
            map.putBoolean(key, value);
        }
    }

    static List<String> extractTokens(ReadableMap map) {
        ArrayList<String> tokens = new ArrayList<>();

        try {
            ReadableArray tokensArray = map.getArray("tokens");
            for (int i = 0; i < tokensArray.size(); i++) {
                String token = tokensArray.getString(i);
                tokens.add(token);
            }
        } catch (Exception ignore) {}

        return tokens;
    }
}

