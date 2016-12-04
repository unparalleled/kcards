package com.mrkevinthomas.kcards.deck_list;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mrkevinthomas.kcards.FirebaseDb;
import com.mrkevinthomas.kcards.Logger;
import com.mrkevinthomas.kcards.R;
import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;
import java.util.Collections;

public class SharedDeckListActivity extends DeckListActivity {
    private static final String TAG = "SharedDeckListActivity";

    private static final int MAX_DECKS = 50;

    private static final String SORT_BY_CREATED = "createdTimeMs";
    private static final String SORT_BY_UPDATED = "updatedTimeMs";
    private static final String SORT_BY_NAME = "name";
    private static final String SORT_BY_DESCRIPTION = "description";

    protected int getNavItemId() {
        return R.id.nav_shared;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fab.setVisibility(View.GONE);
        getSupportActionBar().setTitle(R.string.shared);
        deckListAdapter.setReadOnly(true);
    }

    @Override
    protected void loadDecks() {
        retrieveDecksFromFirebaseDb(SORT_BY_UPDATED, true);
    }

    private void retrieveDecksFromFirebaseDb(final String SORT_BY, final boolean reverseOrder) {
        deckListAdapter.clear();
        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(FirebaseDb.DECK_KEY);
        Query query = databaseReference.orderByChild(SORT_BY);
        if (reverseOrder) {
            query.limitToLast(MAX_DECKS);
        } else {
            query.limitToFirst(MAX_DECKS);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Deck> t = new GenericTypeIndicator<Deck>() {
                };

                ArrayList<Deck> decks = new ArrayList<>();
                // iterate through the children to preserve order
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Deck deck = snapshot.getValue(t);
                    deck.setFirebaseKey(snapshot.getKey()); // firebase reference for tracking following status
                    decks.add(deck);
                }

                // for ordering by timestamps, order by most recent
                if (reverseOrder) {
                    Collections.reverse(decks);
                }

                deckListAdapter.setDeckList(decks);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logger.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    protected void handleSortActionClicked(MenuItem item) {
        if (item.getItemId() == R.id.sort_updated) {
            retrieveDecksFromFirebaseDb(SORT_BY_UPDATED, true);
        } else if (item.getItemId() == R.id.sort_created) {
            retrieveDecksFromFirebaseDb(SORT_BY_CREATED, true);
        } else if (item.getItemId() == R.id.sort_name) {
            retrieveDecksFromFirebaseDb(SORT_BY_NAME, false);
        } else if (item.getItemId() == R.id.sort_description) {
            retrieveDecksFromFirebaseDb(SORT_BY_DESCRIPTION, false);
        }
    }

}
