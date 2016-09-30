package com.mrkevinthomas.kcards;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardHolder> {

    private CardManagementActivity cardManagementActivity;
    private List<Card> cardList = new ArrayList<>();
    private Deck deck;

    public CardListAdapter(@NonNull CardManagementActivity cardManagementActivity, @NonNull Deck deck) {
        this.cardManagementActivity = cardManagementActivity;
        this.cardList = deck.getCards();
        this.deck = deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
        this.cardList = deck.getCards();
        notifyDataSetChanged();
    }

    public void addCard(Card card) {
        cardList.add(card);
        notifyItemInserted(cardList.size() - 1);
    }

    public void removeCard(Card card) {
        int position = cardList.indexOf(card);
        cardList.remove(card);
        notifyItemRemoved(position);
    }

    public void shuffle() {
        Collections.shuffle(cardList);
        notifyDataSetChanged();
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final CardHolder holder, int position) {
        if (position < cardList.size()) {
            final Card card = cardList.get(position);
            holder.frontText.setText(card.getFrontText());
            holder.backText.setText(card.getBackText());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(cardManagementActivity, CardViewActivity.class);
                    intent.putExtra(BaseActivity.ARG_DECK, deck);
                    intent.putExtra(BaseActivity.ARG_POSITION, holder.getAdapterPosition());
                    cardManagementActivity.startActivityForResult(intent, BaseActivity.REQUEST_DECK);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    cardManagementActivity.showCardDialog(card);
                    return true;
                }
            });
            holder.itemView.setVisibility(View.VISIBLE);
            if (card.getCorrectCount() > 0 || card.getIncorrectCount() > 0) {
                float totalCount = card.getCorrectCount() + card.getIncorrectCount();
                float percentCorrect = card.getCorrectCount() / totalCount;
                int correctColor = cardManagementActivity.getResources().getColor(R.color.green);
                int incorrectColor = cardManagementActivity.getResources().getColor(R.color.red);
                int color = (int) new ArgbEvaluator().evaluate(percentCorrect, incorrectColor, correctColor);
                holder.progressIndicator.setBackgroundColor(color);
                holder.progressIndicator.setVisibility(View.VISIBLE);
            } else {
                holder.progressIndicator.setVisibility(View.GONE);
            }
        } else {
            holder.itemView.setVisibility(View.INVISIBLE); // dummy footer
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size() + 1;
    }

    public static class CardHolder extends RecyclerView.ViewHolder {
        public final TextView frontText;
        public final TextView backText;
        public final View progressIndicator;

        public CardHolder(View itemView) {
            super(itemView);
            frontText = (TextView) itemView.findViewById(R.id.card_top_text);
            backText = (TextView) itemView.findViewById(R.id.card_bottom_text);
            progressIndicator = itemView.findViewById(R.id.progress_indicator);
        }
    }

}

