package com.mrkevinthomas.kcards;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;
import java.util.List;

public class DeckListAdapter extends RecyclerView.Adapter<DeckListAdapter.WordHolder> {

    private List<Deck> deckList = new ArrayList<>();

    public void setDeckList(List<Deck> deckList) {
        this.deckList = deckList;
        notifyDataSetChanged();
    }

    public void addDeck(Deck deck) {
        deckList.add(deck);
        notifyItemInserted(deckList.size() - 1);
    }

    @Override
    public WordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WordHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.deck_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(WordHolder holder, int position) {
        holder.deckName.setText(deckList.get(position).getName());
        holder.deckDescription.setText(deckList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return deckList.size();
    }

    public static class WordHolder extends RecyclerView.ViewHolder {
        public final TextView deckName;
        public final TextView deckDescription;

        public WordHolder(View itemView) {
            super(itemView);
            deckName = (TextView) itemView.findViewById(R.id.deck_name);
            deckDescription = (TextView) itemView.findViewById(R.id.deck_description);
        }
    }

}
