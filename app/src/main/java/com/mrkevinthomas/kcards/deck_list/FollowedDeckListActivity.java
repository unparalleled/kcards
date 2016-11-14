package com.mrkevinthomas.kcards.deck_list;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mrkevinthomas.kcards.FirebaseDb;
import com.mrkevinthomas.kcards.Preferences;
import com.mrkevinthomas.kcards.R;
import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class FollowedDeckListActivity extends DeckListActivity {
    private static final String TAG = "FollowDeckListActivity";  // shortened to fit logging tag limit of 23 chars

    private AtomicInteger loadCount;
    private ArrayList<Deck> decks = new ArrayList<>();

    protected int getNavItemId() {
        return R.id.nav_followed;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fab.setVisibility(View.GONE);
        getSupportActionBar().setTitle(R.string.followed);
        deckListAdapter.setReadOnly(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void loadDecks() {
        Set<String> keySet = Preferences.getFollowedKeySet();
        if (keySet.isEmpty()) {
            return;
        }

        loadCount = new AtomicInteger(keySet.size());

        for (String key : keySet) {
            retrieveDeckFromFirebaseDb(key);
        }
    }

    private void retrieveDeckFromFirebaseDb(String key) {
        deckListAdapter.clear();
        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(FirebaseDb.DECK_KEY + "/" + key);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Deck> t = new GenericTypeIndicator<Deck>() {
                };

                Deck deck = dataSnapshot.getValue(t);
                if (deck != null) {
                    decks.add(deck);
                }
                onDeckLoadedOrFailed();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
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
