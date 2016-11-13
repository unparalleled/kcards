package com.mrkevinthomas.kcards;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;

public class Preferences {
    private static final String TAG = "kcards";

    public static final String KEY_MAIN_LANGUAGE = "main_language";
    public static final String KEY_SECONDARY_LANGUAGE = "secondary_language";

    private static final SharedPreferences preferences = ThisApp.get().getSharedPreferences(TAG, MODE_PRIVATE);

    public static String getMainLanguage(Context context) {
        return context.getSharedPreferences(TAG, MODE_PRIVATE).getString(KEY_MAIN_LANGUAGE, null);
    }

    public static String getSecondaryLanguage(Context context) {
        return context.getSharedPreferences(TAG, MODE_PRIVATE).getString(KEY_SECONDARY_LANGUAGE, null);
    }

    public static void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    @Nullable
    public static String getString(String key) {
        return preferences.getString(key, null);
    }

}
