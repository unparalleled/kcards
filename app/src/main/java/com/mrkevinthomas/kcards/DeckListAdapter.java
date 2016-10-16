package com.mrkevinthomas.kcards;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;
import java.util.List;

public class DeckListAdapter extends RecyclerView.Adapter<DeckListAdapter.DeckHolder> {

    private DeckListActivity deckListActivity;
    private List<Deck> deckList = new ArrayList<>();
    private boolean isReadOnly;

    public DeckListAdapter(@NonNull DeckListActivity deckListActivity) {
        this.deckListActivity = deckListActivity;
    }

    public void setDeckList(@NonNull List<Deck> deckList) {
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

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    @Override
    public DeckHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeckHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.deck_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DeckHolder holder, int position) {
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
            holder.deckCountHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(deckListActivity, CardSwipeActivity.class);
                    intent.putExtra(BaseActivity.ARG_DECK, deck);
                    deckListActivity.startActivity(intent);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isReadOnly) {
                        deckListActivity.showDeckDialog(deck);
                    }
                    return true;
                }
            });
            holder.itemView.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setVisibility(View.INVISIBLE); // dummy footer
        }
    }

    @Override
    public int getItemCount() {
        return deckList.size() + 1;
    }

    public static class DeckHolder extends RecyclerView.ViewHolder {
        public final TextView deckName;
        public final TextView deckDescription;
        public final TextView deckCount;
        public final View deckCountHolder;

        public DeckHolder(View itemView) {
            super(itemView);
            deckName = (TextView) itemView.findViewById(R.id.deck_name);
            deckDescription = (TextView) itemView.findViewById(R.id.deck_description);
            deckCount = (TextView) itemView.findViewById(R.id.deck_count);
            deckCountHolder = itemView.findViewById(R.id.deck_count_holder);
        }
    }

}
