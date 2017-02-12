package com.mrkevinthomas.kcards;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mrkevinthomas.kcards.models.Deck;

public class FirebaseDb {

    public static final String DECK_KEY = "decks";

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void updateDeck(Deck deck) {
        if (deck.isSyncedWithFirebase()) {
            // update previous deck object using firebase key
            DatabaseReference databaseReference = database.getReference(DECK_KEY + "/" + deck.getFirebaseKey());
            databaseReference.setValue(deck);
        }
    }

    public static void addNewDeck(Deck deck) {
        // push new deck object
        DatabaseReference databaseReference = database.getReference(DECK_KEY).push();
        databaseReference.setValue(deck);

        String firebaseKey = databaseReference.getKey();
        deck.setFirebaseKey(firebaseKey);
        deck.save();
    }

    public static void removeDeck(Deck deck) {
        if (deck.isSyncedWithFirebase()) {
            // remove deck object using firebase key
            DatabaseReference databaseReference = database.getReference(DECK_KEY + "/" + deck.getFirebaseKey());
            databaseReference.removeValue();
        }
    }

}
