package com.mrkevinthomas.kcards;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrkevinthomas.kcards.models.Card;

import java.util.ArrayList;
import java.util.List;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardHolder> {

    private CardManagementActivity cardManagementActivity;
    private List<Card> cardList = new ArrayList<>();

    public CardListAdapter(CardManagementActivity cardManagementActivity, List<Card> cardList) {
        this.cardManagementActivity = cardManagementActivity;
        this.cardList = cardList;
    }

    public void addCard(Card card) {
        cardList.add(card);
        notifyItemInserted(cardList.size() - 1);
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CardHolder holder, int position) {
        final Card card = cardList.get(position);
        holder.frontText.setText(card.getFrontText());
        holder.backText.setText(card.getBackText());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardHolder extends RecyclerView.ViewHolder {
        public final TextView frontText;
        public final TextView backText;

        public CardHolder(View itemView) {
            super(itemView);
            frontText = (TextView) itemView.findViewById(R.id.card_front_text);
            backText = (TextView) itemView.findViewById(R.id.card_back_text);
        }
    }

}

