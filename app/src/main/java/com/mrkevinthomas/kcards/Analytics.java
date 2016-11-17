package com.mrkevinthomas.kcards;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;

public class Analytics {
    private static final String TAG = "Analytics";

    private static FirebaseAnalytics firebaseAnalytics;

    private static final String EVENT_LOAD_FILE_FAILED = "load_file_failed";
    private static final String ARG_FILE_NAME = "file_name";

    private static final String EVENT_ADD_DECK = "add_deck";
    private static final String EVENT_EDIT_DECK = "edit_deck";
    private static final String ARG_DECK_KEY = "deck_key";
    private static final String ARG_DECK_NAME = "deck_name";
    private static final String ARG_DECK_DESCRIPTION = "deck_description";

    private static final String EVENT_ADD_CARD = "add_card";
    private static final String EVENT_EDIT_CARD = "edit_card";
    private static final String ARG_CARD_FRONT_TEXT = "front_text";
    private static final String ARG_CARD_BACK_TEXT = "back_text";
    private static final String ARG_CARD_FRONT_LANGUAGE = "front_language";
    private static final String ARG_CARD_BACK_LANGUAGE = "back_language";

    private static final String EVENT_OPTIONS_ITEM_SELECTED = "options_item_selected";
    private static final String ARG_OPTIONS_ITEM_ID = "options_item_id";
    private static final String ARG_OPTIONS_ITEM_TITLE = "options_item_title";

    private static final String EVENT_SORT_ITEM_SELECTED = "sort_item_selected";
    private static final String ARG_SORT_ITEM_ID = "sort_item_id";
    private static final String ARG_SORT_ITEM_TITLE = "sort_item_title";

    private static final String EVENT_PROGRESS_CORRECT = "progress_correct";
    private static final String EVENT_PROGRESS_INCORRECT = "progress_incorrect";

    private static final String EVENT_DECK_FOLLOWED = "deck_followed";
    private static final String EVENT_DECK_UNFOLLOWED = "deck_unfollowed";

    public static void init(Context context) {
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    private static void logEvent(@NonNull String name, @Nullable Bundle params) {
        firebaseAnalytics.logEvent(name, params);
        Log.v(TAG, "event: " + name + "; params: " + params);
    }

    public static void logLoadFileFailed(String fileName) {
        Bundle params = new Bundle();
        params.putString(ARG_FILE_NAME, fileName);
        logEvent(EVENT_LOAD_FILE_FAILED, params);
    }

    public static void logAddDeckEvent(Deck deck) {
        Bundle params = new Bundle();
        params.putString(ARG_DECK_NAME, deck.getName());
        params.putString(ARG_DECK_DESCRIPTION, deck.getDescription());
        logEvent(EVENT_ADD_DECK, params);
    }

    public static void logEditDeckEvent(Deck deck) {
        Bundle params = new Bundle();
        params.putString(ARG_DECK_NAME, deck.getName());
        params.putString(ARG_DECK_DESCRIPTION, deck.getDescription());
        logEvent(EVENT_EDIT_DECK, params);
    }

    public static void logAddCardEvent(Card card) {
        Bundle params = new Bundle();
        params.putString(ARG_CARD_FRONT_TEXT, card.getFrontText());
        params.putString(ARG_CARD_BACK_TEXT, card.getBackText());
        params.putString(ARG_CARD_FRONT_LANGUAGE, card.getFrontLanguageCode());
        params.putString(ARG_CARD_BACK_LANGUAGE, card.getBackLanguageCode());
        logEvent(EVENT_ADD_CARD, params);
    }

    public static void logEditCardEvent(Card card) {
        Bundle params = new Bundle();
        params.putString(ARG_CARD_FRONT_TEXT, card.getFrontText());
        params.putString(ARG_CARD_BACK_TEXT, card.getBackText());
        params.putString(ARG_CARD_FRONT_LANGUAGE, card.getFrontLanguageCode());
        params.putString(ARG_CARD_BACK_LANGUAGE, card.getBackLanguageCode());
        logEvent(EVENT_EDIT_CARD, params);
    }

    public static void logOptionsItemSelectedEvent(MenuItem item) {
        Bundle params = new Bundle();
        params.putString(ARG_OPTIONS_ITEM_ID, String.valueOf(item.getItemId()));
        params.putString(ARG_OPTIONS_ITEM_TITLE, String.valueOf(item.getTitle()));
        logEvent(EVENT_OPTIONS_ITEM_SELECTED, params);
    }

    public static void logSortItemSelectedEvent(MenuItem item) {
        Bundle params = new Bundle();
        params.putString(ARG_SORT_ITEM_ID, String.valueOf(item.getItemId()));
        params.putString(ARG_SORT_ITEM_TITLE, String.valueOf(item.getTitle()));
        logEvent(EVENT_SORT_ITEM_SELECTED, params);
    }

    public static void logEventProgressCorrect() {
        logEvent(EVENT_PROGRESS_CORRECT, null);
    }

    public static void logEventProgressIncorrect() {
        logEvent(EVENT_PROGRESS_INCORRECT, null);
    }

    public static void logDeckFollowed(Deck deck) {
        Bundle params = new Bundle();
        params.putString(ARG_DECK_KEY, deck.getFirebaseKey());
        params.putString(ARG_DECK_NAME, deck.getName());
        params.putString(ARG_DECK_DESCRIPTION, deck.getDescription());
        logEvent(EVENT_DECK_FOLLOWED, params);
    }

    public static void logDeckUnfollowed(Deck deck) {
        Bundle params = new Bundle();
        params.putString(ARG_DECK_KEY, deck.getFirebaseKey());
        params.putString(ARG_DECK_NAME, deck.getName());
        params.putString(ARG_DECK_DESCRIPTION, deck.getDescription());
        logEvent(EVENT_DECK_UNFOLLOWED, params);
    }

}
