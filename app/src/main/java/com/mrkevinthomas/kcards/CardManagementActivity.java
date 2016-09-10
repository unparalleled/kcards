package com.mrkevinthomas.kcards;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;

public class CardManagementActivity extends BaseActivity {

    public static final String ARG_DECK = "deck";

    private CardListAdapter cardListAdapter;

    private Deck deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deck = getIntent().getParcelableExtra(ARG_DECK);

        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // must be set after configuring the toggle
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(deck.getName());
        getSupportActionBar().setSubtitle(deck.getDescription());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCardDialog();
            }
        });

        cardListAdapter = new CardListAdapter(this, deck.getCards());
        recyclerView.setAdapter(cardListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showCardDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.card_edit_dialog, null);
        final EditText frontInput = (EditText) dialogView.findViewById(R.id.card_front_input);
        final EditText backInput = (EditText) dialogView.findViewById(R.id.card_back_input);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.card));
        builder.setView(dialogView);
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String frontText = frontInput.getText().toString();
                String backText = backInput.getText().toString();
                if (!TextUtils.isEmpty(frontText)) {
                    Card card = new Card(deck.getId(), frontText, backText);
                    cardListAdapter.addCard(card);
                    card.save();

                    Bundle bundle = new Bundle();
                    bundle.putString("front_text", frontText);
                    bundle.putString("back_text", frontText);
                    KcardsApp.logAnalyticsEvent("add_card", bundle);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setCancelable(true);
        builder.show();
    }

}
