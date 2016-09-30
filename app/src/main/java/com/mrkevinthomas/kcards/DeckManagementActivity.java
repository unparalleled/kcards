package com.mrkevinthomas.kcards;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeckManagementActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected static final String TAG = "DeckManagementActivity";

    private static final String EXAMPLES_FILE = "examples.json";

    protected DeckListAdapter deckListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeckDialog(null);
            }
        });

        deckListAdapter = new DeckListAdapter(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(deckListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupFirebaseAuth();
    }

    /**
     * This should always be done in the main activity.
     * If it's done in Application then it can conflict with Firebase crash reporting.
     */
    private void setupFirebaseAuth() {
        if (ThisApp.get().getFirebaseUser() != null) {
            return; // no need to re-authenticate
        }

        FirebaseAuth firebaseAuth;
        FirebaseAuth.AuthStateListener authStateListener;

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    ThisApp.get().setFirebaseUser(user);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInAnonymously", task.getException());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadDecks();
    }

    protected void loadDecks() {
        SQLite.select()
                .from(Deck.class)
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<Deck>() {
                    @Override
                    public void onQueryResult(QueryTransaction transaction, @NonNull CursorResult<Deck> tResult) {
                        // called when query returns on UI thread
                        List<Deck> decks = tResult.toListClose();
                        if (decks != null && !decks.isEmpty()) {
                            deckListAdapter.setDeckList(decks);
                        } else {
                            loadExampleDecksFromFile();
                        }
                    }
                }).execute();
    }

    private void loadExampleDecksFromFile() {
        try {
            Reader reader = new InputStreamReader(getAssets().open(EXAMPLES_FILE), "UTF-8");
            Deck[] exampleDecks = new Gson().fromJson(reader, Deck[].class);
            if (exampleDecks != null && exampleDecks.length > 0) {
                for (Deck deck : exampleDecks) {
                    deck.save(); // auto generate deck id
                    for (Card card : deck.getCards()) {
                        card.setDeckId(deck.getId());
                        card.save();
                    }
                }
                deckListAdapter.setDeckList(new ArrayList<>(Arrays.asList(exampleDecks)));
            }
        } catch (IOException e) {
            // this should be extremely rare, but not the end of the world if it happens
            Log.e(TAG, "failed to load examples json file", e);
            Analytics.logLoadFileFailed(EXAMPLES_FILE);
        }
    }

    protected void showDeckDialog(@Nullable final Deck deck) {
        View dialogView = getLayoutInflater().inflate(R.layout.deck_edit_dialog, null);
        final EditText nameInput = (EditText) dialogView.findViewById(R.id.deck_name_input);
        final EditText descriptionInput = (EditText) dialogView.findViewById(R.id.deck_description_input);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.deck_of_cards);
        builder.setView(dialogView);
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameInput.getText().toString();
                String description = descriptionInput.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    if (deck == null) {
                        Deck newDeck = new Deck(name, description);
                        newDeck.save();
                        deckListAdapter.addDeck(newDeck);

                        Analytics.logAddDeckEvent(newDeck);
                    } else {
                        deck.setName(name);
                        deck.setDescription(description);
                        deck.save();
                        deckListAdapter.notifyDataSetChanged();

                        Analytics.logEditDeckEvent(deck);
                    }
                    Toast.makeText(DeckManagementActivity.this, getString(R.string.deck_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DeckManagementActivity.this, getString(R.string.deck_must_have_name), Toast.LENGTH_LONG).show();
                }
            }
        });
        if (deck != null) {
            builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!deck.getCards().isEmpty()) {
                        showDeleteDialog(deck);
                    } else {
                        deckListAdapter.removeDeck(deck);
                        deck.delete();
                    }
                }
            });
            nameInput.setText(deck.getName());
            // move cursor to the end of the input text
            nameInput.setSelection(deck.getName() != null ? deck.getName().length() : 0);
            descriptionInput.setText(deck.getDescription());
        }
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setCancelable(true);

        // show keyboard by default
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    private void showDeleteDialog(@NonNull final Deck deck) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.are_you_sure);
        builder.setMessage(getResources().getQuantityString(R.plurals.you_have_x_many_cards, deck.size(), deck.size()));
        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deckListAdapter.removeDeck(deck);
                deck.delete();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setCancelable(true);
        builder.show();
    }

}
