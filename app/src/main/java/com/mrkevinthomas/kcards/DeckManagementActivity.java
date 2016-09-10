package com.mrkevinthomas.kcards;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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
                showDeckDialog();
            }
        });

        deckListAdapter = new DeckListAdapter(this);
        recyclerView.setAdapter(deckListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    private void showDeckDialog() {
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
                    Deck deck = new Deck(name, description);
                    deckListAdapter.addDeck(deck);
                    deck.save();

                    Bundle bundle = new Bundle();
                    bundle.putString("deck_name", name);
                    bundle.putString("deck_description", description);
                    KcardsApp.logAnalyticsEvent("add_deck", bundle);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setCancelable(true);
        builder.show();
    }

}
