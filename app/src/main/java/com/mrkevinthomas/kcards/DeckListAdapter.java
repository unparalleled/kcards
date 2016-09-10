package com.mrkevinthomas.kcards;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;
import java.util.List;

public class DeckListAdapter extends RecyclerView.Adapter<DeckListAdapter.DeckHolder> {

    private DeckManagementActivity deckManagementActivity;
    private List<Deck> deckList = new ArrayList<>();

    public DeckListAdapter(DeckManagementActivity deckManagementActivity) {
        this.deckManagementActivity = deckManagementActivity;
    }

    public void setDeckList(List<Deck> deckList) {
        this.deckList = deckList;
        notifyDataSetChanged();
    }

    public void addDeck(Deck deck) {
        deckList.add(deck);
        notifyItemInserted(deckList.size() - 1);
    }

    @Override
    public DeckHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeckHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.deck_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DeckHolder holder, int position) {
        final Deck deck = deckList.get(position);
        holder.deckName.setText(deck.getName());
        holder.deckDescription.setText(deck.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(deckManagementActivity, CardManagementActivity.class);
                intent.putExtra(CardManagementActivity.ARG_DECK, deck);
                deckManagementActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deckList.size();
    }

    public static class DeckHolder extends RecyclerView.ViewHolder {
        public final TextView deckName;
        public final TextView deckDescription;

        public DeckHolder(View itemView) {
            super(itemView);
            deckName = (TextView) itemView.findViewById(R.id.deck_name);
            deckDescription = (TextView) itemView.findViewById(R.id.deck_description);
        }
    }

}
