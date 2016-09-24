package com.mrkevinthomas.kcards;

import android.content.DialogInterface;
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

public class CardManagementActivity extends BaseActivity {

    private CardListAdapter cardListAdapter;
    private Deck deck;

    private MenuItem publishUnpublishMenuItem;

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
                        card.save();
                        cardListAdapter.notifyDataSetChanged();
                        updateObjectInSharedFirebaseDb();

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
                    updateObjectInSharedFirebaseDb();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_management, menu);
        publishUnpublishMenuItem = menu.findItem(R.id.action_publish_unpublish);
        if (deck.isSyncedWithFirebase()) {
            publishUnpublishMenuItem.setIcon(R.drawable.ic_cloud_done_white_48dp);
            publishUnpublishMenuItem.setTitle(R.string.unpublish);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish_unpublish) {
            handlePublishUnpublishActionClicked();
            return true;
        } else if (item.getItemId() == R.id.action_suffle) {
            cardListAdapter.shuffle();
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
                    Toast.makeText(CardManagementActivity.this, getString(R.string.deck_unpublished), Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), null);
            builder.setCancelable(true);
            builder.show();
        }
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