package com.mrkevinthomas.kcards;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;
import java.util.Collections;

public class DeckViewActivity extends DeckListActivity {

    private static final int MAX_DECKS = 50;

    private static final String SORT_BY_CREATED = "createdTimeMs";
    private static final String SORT_BY_UPDATED = "updatedTimeMs";
    private static final String SORT_BY_NAME = "name";
    private static final String SORT_BY_DESCRIPTION = "description";

    protected int getNavItemId() {
        return R.id.nav_trending;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fab.setVisibility(View.GONE);
        getSupportActionBar().setTitle(R.string.trending);
        deckListAdapter.setReadOnly(true);
    }

    @Override
    protected void loadDecks() {
        retrieveDecksFromSharedFirebaseDb(SORT_BY_CREATED, true);
    }

    private void retrieveDecksFromSharedFirebaseDb(final String SORT_BY, final boolean reverseOrder) {
        deckListAdapter.clear();
        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("decks");
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
                    decks.add(snapshot.getValue(t));
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
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    protected void handleSortActionClicked(MenuItem item) {
        if (item.getItemId() == R.id.sort_name) {
            retrieveDecksFromSharedFirebaseDb(SORT_BY_NAME, false);
        } else if (item.getItemId() == R.id.sort_description) {
            retrieveDecksFromSharedFirebaseDb(SORT_BY_DESCRIPTION, false);
        } else if (item.getItemId() == R.id.sort_created) {
            retrieveDecksFromSharedFirebaseDb(SORT_BY_CREATED, true);
        } else if (item.getItemId() == R.id.sort_updated) {
            retrieveDecksFromSharedFirebaseDb(SORT_BY_UPDATED, true);
        }
    }

}
