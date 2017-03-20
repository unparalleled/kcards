package com.mrkevinthomas.kcards;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.mrkevinthomas.kcards.models.Language;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * This class manages persistent preferences for this app.
 *
 * The SharedPreferences implementation is encapsulated in this class and hidden behind helper methods.
 * The helper methods provide simplicity and ensure consistent usage of keys and types.
 */
public class Preferences {
    private static final String TAG = "kcards";

    private static final String KEY_MAIN_LANGUAGE = "main_language";
    private static final String KEY_SECONDARY_LANGUAGE = "secondary_language";

    private static final String KEY_FOLLOWING_DECKS = "following_decks";

    private static final String KEY_DEBUG_MODE = "debug_mode";

    private static final SharedPreferences preferences = ThisApp.get().getSharedPreferences(TAG, MODE_PRIVATE);

    public static void putMainLanguageCode(String languageCode) {
        preferences.edit().putString(KEY_MAIN_LANGUAGE, languageCode).apply();
    }

    public static void putSecondaryLanguageCode(String languageCode) {
        preferences.edit().putString(KEY_SECONDARY_LANGUAGE, languageCode).apply();
    }

    @NonNull
    public static String getMainLanguageCode(Context context) {
        return context.getSharedPreferences(TAG, MODE_PRIVATE).getString(KEY_MAIN_LANGUAGE, Language.DEFAULT.getGoogleTranslateCode());
    }

    @NonNull
    public static String getSecondaryLanguageCode(Context context) {
        return context.getSharedPreferences(TAG, MODE_PRIVATE).getString(KEY_SECONDARY_LANGUAGE, Language.DEFAULT.getGoogleTranslateCode());
    }

    public static boolean isFollowingDeck(String deckKey) {
        Set<String> deckKeys = preferences.getStringSet(KEY_FOLLOWING_DECKS, new HashSet<String>());
        return deckKeys.contains(deckKey);
    }

    public static void followDeck(String deckKey) {
        Set<String> deckKeys = getFollowedKeySet();
        deckKeys.add(deckKey);
        preferences.edit().putStringSet(KEY_FOLLOWING_DECKS, deckKeys).apply();
    }

    public static void unfollowDeck(String deckKey) {
        Set<String> deckKeys = getFollowedKeySet();
        deckKeys.remove(deckKey);
        preferences.edit().putStringSet(KEY_FOLLOWING_DECKS, deckKeys).apply();
    }

    @NonNull
    public static Set<String> getFollowedKeySet() {
        // create copy to ensure consistency, see `getStringSet`
        return new HashSet<>(preferences.getStringSet(KEY_FOLLOWING_DECKS, new HashSet<String>()));
    }

    public static boolean isDebugMode() {
        return preferences.getBoolean(KEY_DEBUG_MODE, false);
    }

    public static void setDebugMode() {
        preferences.edit().putBoolean(KEY_DEBUG_MODE, true).apply();
    }

}
