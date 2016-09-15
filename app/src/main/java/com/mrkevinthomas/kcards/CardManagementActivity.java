package com.mrkevinthomas.kcards;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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
                    if (card == null) {
                        Card newCard = new Card(deck.getId(), frontText, backText);
                        cardListAdapter.addCard(newCard);
                        newCard.save();

                        // show another dialog for continuing card creation
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showCardDialog(null);
                            }
                        }, 100);

                        Analytics.logAddCardEvent(newCard);
                    } else {
                        card.setFrontText(frontText);
                        card.setBackText(backText);
                        card.save();
                        cardListAdapter.notifyDataSetChanged();

                        Analytics.logEditCardEvent(card);
                    }
                    Toast.makeText(CardManagementActivity.this, getString(R.string.card_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CardManagementActivity.this, getString(R.string.card_must_have_front), Toast.LENGTH_LONG).show();
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
            // move cursor to the end of the input text
            frontInput.setSelection(card.getFrontText() != null ? card.getFrontText().length() : 0);
            backInput.setText(card.getBackText());
        }
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setCancelable(true);

        // show keyboard by default
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

}
