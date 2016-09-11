package com.mrkevinthomas.kcards;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;

public class CardManagementActivity extends BaseActivity {

    private CardListAdapter cardListAdapter;
    private Deck deck;

    @Override
    protected boolean shouldShowUpButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deck = getIntent().getParcelableExtra(ARG_DECK);

        getSupportActionBar().setTitle(deck.getName());
        getSupportActionBar().setSubtitle(deck.getDescription());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCardDialog(null);
            }
        });

        cardListAdapter = new CardListAdapter(this, deck);
        recyclerView.setAdapter(cardListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void showCardDialog(@Nullable final Card card) {
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
        if (card != null) {
            builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cardListAdapter.removeCard(card);
                    card.delete();
                }
            });
            frontInput.setText(card.getFrontText());
            backInput.setText(card.getBackText());
        }
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setCancelable(true);
        builder.show();
    }

}
