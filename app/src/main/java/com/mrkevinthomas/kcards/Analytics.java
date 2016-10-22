package com.mrkevinthomas.kcards;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;

public class Analytics {

    private static FirebaseAnalytics firebaseAnalytics;

    private static final String EVENT_LOAD_FILE_FAILED = "load_file_failed";
    private static final String ARG_FILE_NAME = "file_name";

    private static final String EVENT_ADD_DECK = "add_deck";
    private static final String EVENT_EDIT_DECK = "edit_deck";
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

    private static final String EVENT_PROGRESS_CORRECT = "progress_correct";
    private static final String EVENT_PROGRESS_INCORRECT = "progress_incorrect";

    public static void init(Context context) {
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static void logLoadFileFailed(String fileName) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_FILE_NAME, fileName);
        firebaseAnalytics.logEvent(EVENT_LOAD_FILE_FAILED, bundle);
    }

    public static void logAddDeckEvent(Deck deck) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DECK_NAME, deck.getName());
        bundle.putString(ARG_DECK_DESCRIPTION, deck.getDescription());
        firebaseAnalytics.logEvent(EVENT_ADD_DECK, bundle);
    }

    public static void logEditDeckEvent(Deck deck) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DECK_NAME, deck.getName());
        bundle.putString(ARG_DECK_DESCRIPTION, deck.getDescription());
        firebaseAnalytics.logEvent(EVENT_EDIT_DECK, bundle);
    }

    public static void logAddCardEvent(Card card) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CARD_FRONT_TEXT, card.getFrontText());
        bundle.putString(ARG_CARD_BACK_TEXT, card.getBackText());
        bundle.putString(ARG_CARD_FRONT_LANGUAGE, card.getFrontLanguageCode());
        bundle.putString(ARG_CARD_BACK_LANGUAGE, card.getBackLanguageCode());
        firebaseAnalytics.logEvent(EVENT_ADD_CARD, bundle);
    }

    public static void logEditCardEvent(Card card) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CARD_FRONT_TEXT, card.getFrontText());
        bundle.putString(ARG_CARD_BACK_TEXT, card.getBackText());
        bundle.putString(ARG_CARD_FRONT_LANGUAGE, card.getFrontLanguageCode());
        bundle.putString(ARG_CARD_BACK_LANGUAGE, card.getBackLanguageCode());
        firebaseAnalytics.logEvent(EVENT_EDIT_CARD, bundle);
    }

    public static void logOptionsItemSelectedEvent(MenuItem item) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_OPTIONS_ITEM_ID, String.valueOf(item.getItemId()));
        bundle.putString(ARG_OPTIONS_ITEM_TITLE, String.valueOf(item.getTitle()));
        firebaseAnalytics.logEvent(EVENT_OPTIONS_ITEM_SELECTED, bundle);
    }

    public static void logEventProgressCorrect() {
        firebaseAnalytics.logEvent(EVENT_PROGRESS_CORRECT, null);
    }

    public static void logEventProgressIncorrect() {
        firebaseAnalytics.logEvent(EVENT_PROGRESS_INCORRECT, null);
    }

}
