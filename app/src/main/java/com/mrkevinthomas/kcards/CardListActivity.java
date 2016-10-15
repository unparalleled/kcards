package com.mrkevinthomas.kcards;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;
import com.mrkevinthomas.kcards.ui.LanguageSpinner;

public class CardListActivity extends BaseActivity {

    private CardListAdapter cardListAdapter;
    private Deck deck;

    private boolean isReadOnly;

    private MenuItem publishUnpublishMenuItem;
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
        builder.setTitle(getString(R.string.card));
        builder.setView(dialogView);
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
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
                        updateObjectInSharedFirebaseDb();

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
                        updateObjectInSharedFirebaseDb();

                        Analytics.logEditCardEvent(card);
                    }
                    Toast.makeText(CardListActivity.this, getString(R.string.card_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CardListActivity.this, getString(R.string.card_must_have_front), Toast.LENGTH_LONG).show();
                }
            }
        });
        if (card != null) {
            builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cardListAdapter.removeCard(card);
                    card.delete();
                    updateObjectInSharedFirebaseDb();
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
            frontLanguage.setSelectedLanguage(SettingsActivity.getMainLanguage(this));
            backLanguage.setSelectedLanguage(SettingsActivity.getSecondaryLanguage(this));
        }
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setCancelable(true);

        // show keyboard by default
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_management, menu);
        publishUnpublishMenuItem = menu.findItem(R.id.action_publish_unpublish);
        saveMenuItem = menu.findItem(R.id.action_save);
        if (!isReadOnly) {
            saveMenuItem.setVisible(false);
            if (deck.isSyncedWithFirebase()) {
                publishUnpublishMenuItem.setIcon(R.drawable.ic_cloud_done_white_48dp);
                publishUnpublishMenuItem.setTitle(R.string.unpublish);
            }
        } else {
            publishUnpublishMenuItem.setVisible(false);
            menu.findItem(R.id.action_reset_progress).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish_unpublish) {
            handlePublishUnpublishActionClicked();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            saveDeckToDeckList();
            return true;
        } else if (item.getItemId() == R.id.action_suffle) {
            cardListAdapter.shuffle();
            return true;
        } else if (item.getItemId() == R.id.action_reset_progress) {
            resetDeckProgress();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handlePublishUnpublishActionClicked() {
        if (!deck.isSyncedWithFirebase()) {
            createNewObjectInSharedFirebaseDb();
            publishUnpublishMenuItem.setIcon(R.drawable.ic_cloud_done_white_48dp);
            publishUnpublishMenuItem.setTitle(R.string.unpublish);
            Toast.makeText(this, getString(R.string.deck_published), Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.are_you_sure);
            builder.setMessage(getString(R.string.deck_unpublish_message));
            builder.setPositiveButton(getString(R.string.unpublish), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteObjectInSharedFirebaseDb();
                    publishUnpublishMenuItem.setIcon(R.drawable.ic_cloud_upload_white_48dp);
                    publishUnpublishMenuItem.setTitle(R.string.publish);
                    Toast.makeText(CardListActivity.this, getString(R.string.deck_unpublished), Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), null);
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

    private void resetDeckProgress() {
        for (Card card : deck.getCards()) {
            card.resetProgress();
        }
        cardListAdapter.notifyDataSetChanged();
    }

    private void updateObjectInSharedFirebaseDb() {
        if (deck.isSyncedWithFirebase()) {
            // update previous deck object using firebase key
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference("decks/" + deck.getFirebaseKey());
            databaseReference.setValue(deck);
        }
    }

    private void createNewObjectInSharedFirebaseDb() {
        // push new deck object
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("decks").push();
        databaseReference.setValue(deck);

        String firebaseKey = databaseReference.getKey();
        deck.setFirebaseKey(firebaseKey);
        deck.save();
    }

    private void deleteObjectInSharedFirebaseDb() {
        // remove deck object using firebase key
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("decks/" + deck.getFirebaseKey());
        databaseReference.removeValue();

        deck.setFirebaseKey(null);
        deck.save();
    }

}