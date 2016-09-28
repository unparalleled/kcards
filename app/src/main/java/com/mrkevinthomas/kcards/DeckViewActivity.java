package com.mrkevinthomas.kcards;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;
import java.util.HashMap;

public class DeckViewActivity extends DeckManagementActivity {

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
        retrieveDecksFromSharedFirebaseDb();
    }

    private void retrieveDecksFromSharedFirebaseDb() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("decks");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Deck>> t = new GenericTypeIndicator<HashMap<String, Deck>>() {
                };
                HashMap<String, Deck> decks = dataSnapshot.getValue(t);
                if (decks != null && !decks.isEmpty()) {
                    deckListAdapter.setDeckList(new ArrayList<>(decks.values()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

}