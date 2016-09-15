package com.mrkevinthomas.kcards;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;

public class Analytics {

    private static FirebaseAnalytics firebaseAnalytics;

    private static final String EVENT_LOAD_EXAMPLE_FAILED = "load_example_failed";

    private static final String EVENT_ADD_DECK = "add_deck";
    private static final String EVENT_EDIT_DECK = "edit_deck";
    private static final String ARG_DECK_NAME = "deck_name";
    private static final String ARG_DECK_DESCRIPTION = "deck_description";

    private static final String EVENT_ADD_CARD = "add_card";
    private static final String EVENT_EDIT_CARD = "edit_card";
    private static final String ARG_CARD_FRONT_TEXT = "front_text";
    private static final String ARG_CARD_BACK_TEXT = "back_text";

    public static void init(Context context) {
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static void logExampleLoadFailed() {
        firebaseAnalytics.logEvent(EVENT_LOAD_EXAMPLE_FAILED, null);
    }

    public static void logAddDeckEvent(Deck deck) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DECK_NAME, deck.getName());
        bundle.putString(ARG_DECK_DESCRIPTION, deck.getDescription());
        firebaseAnalytics.logEvent(EVENT_ADD_DECK, null);
    }

    public static void logEditDeckEvent(Deck deck) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DECK_NAME, deck.getName());
        bundle.putString(ARG_DECK_DESCRIPTION, deck.getDescription());
        firebaseAnalytics.logEvent(EVENT_EDIT_DECK, null);
    }

    public static void logAddCardEvent(Card card) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CARD_FRONT_TEXT, card.getFrontText());
        bundle.putString(ARG_CARD_BACK_TEXT, card.getBackText());
        firebaseAnalytics.logEvent(EVENT_ADD_CARD, bundle);
    }

    public static void logEditCardEvent(Card card) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CARD_FRONT_TEXT, card.getFrontText());
        bundle.putString(ARG_CARD_BACK_TEXT, card.getBackText());
        firebaseAnalytics.logEvent(EVENT_EDIT_CARD, bundle);
    }

}
