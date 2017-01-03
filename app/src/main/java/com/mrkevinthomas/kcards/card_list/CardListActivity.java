package com.mrkevinthomas.kcards.card_list;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.mrkevinthomas.kcards.Analytics;
import com.mrkevinthomas.kcards.BaseActivity;
import com.mrkevinthomas.kcards.FirebaseDb;
import com.mrkevinthomas.kcards.Preferences;
import com.mrkevinthomas.kcards.R;
import com.mrkevinthomas.kcards.card_swipe.CardSwipeActivity;
import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;
import com.mrkevinthomas.kcards.views.LanguageSpinner;

public class CardListActivity extends BaseActivity {

    private CardListAdapter cardListAdapter;
    private Deck deck;

    private boolean isReadOnly;

    private MenuItem practiceMenuItem;
    private MenuItem shareUnshareMenuItem;
    private MenuItem saveMenuItem;

    @Override
    protected boolean shouldShowUpButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deck = getIntent().getParcelableExtra(ARG_DECK);
        isReadOnly = getIntent().getBooleanExtra(ARG_READ_ONLY, false);

        getSupportActionBar().setTitle(deck.getName());
        getSupportActionBar().setSubtitle(deck.getDescription());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCardDialog(null);
            }
        });
        if (isReadOnly) {
            fab.setVisibility(View.GONE);
        }

        cardListAdapter = new CardListAdapter(this, deck, isReadOnly);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(cardListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DECK && resultCode == RESULT_OK) {
            deck = data.getParcelableExtra(ARG_DECK);
            cardListAdapter.setDeck(deck);
        }
    }

    protected void showCardDialog(@Nullable final Card card) {
        View dialogView = getLayoutInflater().inflate(R.layout.card_edit_dialog, null);
        final EditText frontInput = (EditText) dialogView.findViewById(R.id.card_front_input);
        final EditText backInput = (EditText) dialogView.findViewById(R.id.card_back_input);
        final LanguageSpinner frontLanguage = (LanguageSpinner) dialogView.findViewById(R.id.card_front_language);
        final LanguageSpinner backLanguage = (LanguageSpinner) dialogView.findViewById(R.id.card_back_language);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.card);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String frontText = frontInput.getText().toString();
                String backText = backInput.getText().toString();
                String frontLanguageCode = frontLanguage.getSelectedLanguage().getGoogleTranslateCode();
                String backLanguageCode = backLanguage.getSelectedLanguage().getGoogleTranslateCode();

                if (!TextUtils.isEmpty(frontText)) {
                    if (card == null) {
                        Card newCard = new Card(deck.getId(), frontText, backText, frontLanguageCode, backLanguageCode);
                        cardListAdapter.addCard(newCard);
                        newCard.save();
                        FirebaseDb.updateDeck(deck);

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
                        card.setFrontLanguageCode(frontLanguageCode);
                        card.setBackLanguageCode(backLanguageCode);
                        card.save();
                        cardListAdapter.notifyDataSetChanged();
                        FirebaseDb.updateDeck(deck);

                        Analytics.logEditCardEvent(card);
                    }
                    Toast.makeText(CardListActivity.this, R.string.card_saved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CardListActivity.this, R.string.card_must_have_front, Toast.LENGTH_LONG).show();
                }
            }
        });
        if (card != null) {
            builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Analytics.logDeleteCardEvent(card);
                    cardListAdapter.removeCard(card);
                    card.delete();
                    FirebaseDb.updateDeck(deck);
                }
            });
            frontInput.setText(card.getFrontText());
            // move cursor to the end of the input text
            frontInput.setSelection(card.getFrontText() != null ? card.getFrontText().length() : 0);
            backInput.setText(card.getBackText());

            frontLanguage.setSelectedLanguage(card.getFrontLanguageCode());
            backLanguage.setSelectedLanguage(card.getBackLanguageCode());
        } else {
            // default to main/secondary language settings
            frontLanguage.setSelectedLanguage(Preferences.getMainLanguageCode(this));
            backLanguage.setSelectedLanguage(Preferences.getSecondaryLanguageCode(this));
        }
        builder.setNegativeButton(R.string.cancel, null);
        builder.setCancelable(true);

        AlertDialog alertDialog = builder.create();
        if (card == null) {
            // show keyboard by default for new cards
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_management, menu);
        practiceMenuItem = menu.findItem(R.id.action_practice);
        shareUnshareMenuItem = menu.findItem(R.id.action_share_unshare);
        saveMenuItem = menu.findItem(R.id.action_save);
        if (!isReadOnly) {
            saveMenuItem.setVisible(false);
            if (deck.isSyncedWithFirebase()) {
                shareUnshareMenuItem.setIcon(R.drawable.ic_cloud_done_white_48dp);
                shareUnshareMenuItem.setTitle(R.string.unshare);
            }
        } else {
            practiceMenuItem.setVisible(false);
            shareUnshareMenuItem.setVisible(false);
            menu.findItem(R.id.action_reset_progress).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_practice) {
            Intent intent = new Intent(this, CardSwipeActivity.class);
            intent.putExtra(BaseActivity.ARG_DECK, deck);
            startActivityForResult(intent, BaseActivity.REQUEST_DECK);
            return true;
        } else if (item.getItemId() == R.id.action_shuffle) {
            cardListAdapter.shuffle();
            return true;
        } else if (item.getItemId() == R.id.action_share_unshare) {
            handlePublishUnpublishActionClicked();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            saveDeckToDeckList();
            return true;
        } else if (item.getItemId() == R.id.action_reset_progress) {
            showResetProgressDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handlePublishUnpublishActionClicked() {
        if (!deck.isSyncedWithFirebase()) {
            FirebaseDb.createNewDeck(deck);
            shareUnshareMenuItem.setIcon(R.drawable.ic_cloud_done_white_48dp);
            shareUnshareMenuItem.setTitle(R.string.unshare);
            Toast.makeText(this, R.string.deck_shared, Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.are_you_sure);
            builder.setMessage(R.string.deck_unshare_message);
            builder.setPositiveButton(R.string.unshare, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDb.deleteDeck(deck);
                    shareUnshareMenuItem.setIcon(R.drawable.ic_cloud_upload_white_48dp);
                    shareUnshareMenuItem.setTitle(R.string.share);
                    Toast.makeText(CardListActivity.this, R.string.deck_unshared, Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.setCancelable(true);
            builder.show();
        }
    }

    private void saveDeckToDeckList() {
        deck.save(); // auto generate deck id
        for (Card card : deck.getCards()) {
            card.setDeckId(deck.getId());
            card.save();
        }
        saveMenuItem.setVisible(false);
        Toast.makeText(this, R.string.deck_saved, Toast.LENGTH_LONG).show();
    }

    private void showResetProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.are_you_sure);
        builder.setMessage(R.string.deck_reset_progress);
        builder.setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (Card card : deck.getCards()) {
                    card.resetProgress();
                }
                cardListAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.setCancelable(true);
        builder.show();
    }

}