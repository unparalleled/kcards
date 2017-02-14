package com.mrkevinthomas.kcards.deck_list;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mrkevinthomas.kcards.Analytics;
import com.mrkevinthomas.kcards.BaseActivity;
import com.mrkevinthomas.kcards.Preferences;
import com.mrkevinthomas.kcards.R;
import com.mrkevinthomas.kcards.Utils;
import com.mrkevinthomas.kcards.card_list.CardListActivity;
import com.mrkevinthomas.kcards.card_swipe.CardSwipeActivity;
import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeckListAdapter extends RecyclerView.Adapter<DeckViewHolder> {

    private DeckListActivity deckListActivity;
    private List<Deck> deckList = new ArrayList<>();
    private boolean isReadOnly;

    public DeckListAdapter(@NonNull DeckListActivity deckListActivity) {
        this.deckListActivity = deckListActivity;
    }

    public void setDeckList(@NonNull List<Deck> deckList) {
        // default to sorting by last updated
        Collections.sort(deckList, Deck.UPDATED_COMPARATOR);

        this.deckList = deckList;
        notifyDataSetChanged();
    }

    public void addDeck(Deck deck) {
        deckList.add(deck);
        notifyItemInserted(deckList.size() - 1);
    }

    public void removeDeck(Deck deck) {
        int position = deckList.indexOf(deck);
        deckList.remove(deck);
        notifyItemRemoved(position);
    }

    public void clear() {
        deckList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void sort(Comparator<Deck> comparator) {
        Collections.sort(deckList, comparator);
        notifyDataSetChanged();
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    @Override
    public DeckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeckViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.deck_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final DeckViewHolder holder, int position) {
        if (position < deckList.size()) {
            final Deck deck = deckList.get(position);

            holder.deckName.setText(deck.getName());
            holder.deckDescription.setText(deck.getDescription());
            holder.deckCount.setText(
                    deckListActivity.getResources().getQuantityString(R.plurals.card_count, deck.size(), deck.size()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(deckListActivity, CardListActivity.class);
                    intent.putExtra(BaseActivity.ARG_DECK, deck);
                    intent.putExtra(BaseActivity.ARG_READ_ONLY, isReadOnly);
                    deckListActivity.startActivity(intent);
                }
            });

            if (!isReadOnly) {
                holder.deckActionIcon.setImageResource(R.drawable.ic_playlist_play_black_48dp);
                holder.deckCountHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(deckListActivity, CardSwipeActivity.class);
                        intent.putExtra(BaseActivity.ARG_DECK, deck);
                        deckListActivity.startActivity(intent);
                    }
                });
            } else {
                holder.deckActionIcon.setImageResource(Preferences.isFollowingDeck(deck.getFirebaseKey()) ?
                        R.drawable.ic_favorite_black_48dp : R.drawable.ic_favorite_border_black_48dp);
                holder.deckCountHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Preferences.isFollowingDeck(deck.getFirebaseKey())) {
                            Toast.makeText(deckListActivity, R.string.favorites_removed, Toast.LENGTH_SHORT).show();
                            Preferences.unfollowDeck(deck.getFirebaseKey());
                            holder.deckActionIcon.setImageResource(R.drawable.ic_favorite_border_black_48dp);
                            Analytics.logDeckUnfollowed(deck);
                        } else {
                            Toast.makeText(deckListActivity, R.string.favorites_added, Toast.LENGTH_SHORT).show();
                            Preferences.followDeck(deck.getFirebaseKey());
                            holder.deckActionIcon.setImageResource(R.drawable.ic_favorite_black_48dp);
                            Analytics.logDeckFollowed(deck);
                        }
                    }
                });
            }

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isReadOnly) {
                        deckListActivity.showDeckDialog(deck);
                    }
                    return true;
                }
            });

            String createdDate = Utils.getDateString(deck.getCreatedTimeMs());
            holder.deckCreated.setText(deckListActivity.getString(R.string.created_at, createdDate));

            String updatedDate = Utils.getDateString(deck.getUpdatedTimeMs());
            holder.deckUpdated.setText(deckListActivity.getString(R.string.updated_at, updatedDate));

            holder.publicIcon.setVisibility(!isReadOnly && deck.isSyncedWithFirebase() ? View.VISIBLE : View.GONE);

            holder.itemView.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setVisibility(View.INVISIBLE); // dummy footer
        }
    }

    @Override
    public int getItemCount() {
        return deckList.size() + 1; // +1 for dummy footer
    }

}
