package com.mrkevinthomas.kcards;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mrkevinthomas.kcards.models.Deck;

public class FirebaseDb {

    public static final String DECK_KEY = "decks";

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void updateDeck(Deck deck) {
        if (deck.isSyncedWithFirebase()) {
            deck.setUpdatedTimeMs(System.currentTimeMillis()); // updated modified timestamp
            // update previous deck object using firebase key
            DatabaseReference databaseReference = database.getReference(DECK_KEY + "/" + deck.getFirebaseKey());
            databaseReference.setValue(deck);
        }
    }

    public static void createNewDeck(Deck deck) {
        // push new deck object
        DatabaseReference databaseReference = database.getReference(DECK_KEY).push();
        databaseReference.setValue(deck);

        String firebaseKey = databaseReference.getKey();
        deck.setFirebaseKey(firebaseKey);
        deck.save();
    }

    public static void deleteDeck(Deck deck) {
        // remove deck object using firebase key
        DatabaseReference databaseReference = database.getReference(DECK_KEY + "/" + deck.getFirebaseKey());
        databaseReference.removeValue();

        deck.setFirebaseKey(null);
        deck.save();
    }

}
