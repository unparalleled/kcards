package com.mrkevinthomas.kcards.deck_list;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mrkevinthomas.kcards.FirebaseDb;
import com.mrkevinthomas.kcards.Logger;
import com.mrkevinthomas.kcards.Preferences;
import com.mrkevinthomas.kcards.R;
import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class FavoritesDeckListActivity extends DeckListActivity {
    private static final String TAG = "FavoritesDeckListActivity";

    private AtomicInteger loadCount;
    private ArrayList<Deck> decks = new ArrayList<>();

    protected int getNavItemId() {
        return R.id.nav_favorites;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fab.setVisibility(View.GONE);
        getSupportActionBar().setTitle(R.string.favorites);
        deckListAdapter.setReadOnly(true);
    }

    @Override
    protected void loadDecks() {
        Set<String> keySet = Preferences.getFollowedKeySet();
        if (keySet.isEmpty()) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        loadCount = new AtomicInteger(keySet.size());

        for (String key : keySet) {
            retrieveDeckFromFirebaseDb(key);
        }
    }

    private void retrieveDeckFromFirebaseDb(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(FirebaseDb.DECK_KEY + "/" + key);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Deck> t = new GenericTypeIndicator<Deck>() {
                };

                Deck deck = dataSnapshot.getValue(t);
                if (deck != null) {
                    deck.setFirebaseKey(dataSnapshot.getKey()); // firebase reference for tracking following status
                    decks.add(deck);
                }
                onDeckLoadedOrFailed();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logger.w(TAG, "loadPost:onCancelled", databaseError.toException());
                onDeckLoadedOrFailed();
            }
        });
    }

    private void onDeckLoadedOrFailed() {
        if (loadCount.decrementAndGet() == 0) {
            progressBar.setVisibility(View.GONE);
            deckListAdapter.setDeckList(decks);
        }
    }

}
