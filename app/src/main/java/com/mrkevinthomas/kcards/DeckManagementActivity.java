package com.mrkevinthomas.kcards;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.mrkevinthomas.kcards.models.Deck;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.List;

public class DeckManagementActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DeckListAdapter deckListAdapter;

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
        recyclerView.setAdapter(deckListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadDecks();
    }

    private void loadDecks() {
        SQLite.select()
                .from(Deck.class)
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<Deck>() {
                    @Override
                    public void onQueryResult(QueryTransaction transaction, @NonNull CursorResult<Deck> tResult) {
                        // called when query returns on UI thread
                        List<Deck> decks = tResult.toListClose();
                        deckListAdapter.setDeckList(decks);
                    }
                }).execute();
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
                        deckListAdapter.addDeck(newDeck);
                        newDeck.save();

                        Bundle bundle = new Bundle();
                        bundle.putString("deck_name", name);
                        bundle.putString("deck_description", description);
                        KcardsApp.logAnalyticsEvent("add_deck", bundle);
                    } else {
                        deck.setName(name);
                        deck.setDescription(description);
                        deck.save();
                        deckListAdapter.notifyDataSetChanged();

                        Bundle bundle = new Bundle();
                        bundle.putString("deck_name", name);
                        bundle.putString("deck_description", description);
                        KcardsApp.logAnalyticsEvent("edit_deck", bundle);
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
                    showDeleteDialog(deck);
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
