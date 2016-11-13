package com.mrkevinthomas.kcards;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mrkevinthomas.kcards.models.Language;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class Preferences {
    private static final String TAG = "kcards";

    public static final String KEY_MAIN_LANGUAGE = "key_main_language";
    public static final String KEY_SECONDARY_LANGUAGE = "key_secondary_language";

    public static final String KEY_FOLLOWING_DECKS = "key_following_decks";

    private static final SharedPreferences preferences = ThisApp.get().getSharedPreferences(TAG, MODE_PRIVATE);

    @NonNull
    public static String getMainLanguage(Context context) {
        return context.getSharedPreferences(TAG, MODE_PRIVATE).getString(KEY_MAIN_LANGUAGE, Language.DEFAULT.getGoogleTranslateCode());
    }

    @NonNull
    public static String getSecondaryLanguage(Context context) {
        return context.getSharedPreferences(TAG, MODE_PRIVATE).getString(KEY_SECONDARY_LANGUAGE, Language.DEFAULT.getGoogleTranslateCode());
    }

    public static void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    @Nullable
    public static String getString(String key) {
        return preferences.getString(key, null);
    }

    public static boolean isFollowingDeck(String deckKey) {
        Set<String> deckKeys = preferences.getStringSet(KEY_FOLLOWING_DECKS, new HashSet<String>());
        return deckKeys.contains(deckKey);
    }

    public static void followDeck(String deckKey) {
        Set<String> deckKeys = preferences.getStringSet(KEY_FOLLOWING_DECKS, new HashSet<String>());
        deckKeys = new HashSet<>(deckKeys); // create copy to ensure consistency, see `getStringSet`
        deckKeys.add(deckKey);
        preferences.edit().putStringSet(KEY_FOLLOWING_DECKS, deckKeys).apply();
    }

    public static void unfollowDeck(String deckKey) {
        Set<String> deckKeys = preferences.getStringSet(KEY_FOLLOWING_DECKS, new HashSet<String>());
        deckKeys = new HashSet<>(deckKeys); // create copy to ensure consistency, see `getStringSet`
        deckKeys.remove(deckKey);
        preferences.edit().putStringSet(KEY_FOLLOWING_DECKS, deckKeys).apply();
    }

}
