package com.mrkevinthomas.kcards.card_list;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mrkevinthomas.kcards.BaseActivity;
import com.mrkevinthomas.kcards.R;
import com.mrkevinthomas.kcards.card_pager.CardPagerActivity;
import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardListAdapter extends RecyclerView.Adapter<CardViewHolder> {

    private CardListActivity cardListActivity;

    private Deck deck;
    private List<Card> cardList = new ArrayList<>();

    private boolean isReadOnly;

    public CardListAdapter(@NonNull CardListActivity cardListActivity, @NonNull Deck deck, boolean isReadOnly) {
        this.cardListActivity = cardListActivity;
        this.deck = deck;
        this.cardList = deck.getCards();
        this.isReadOnly = isReadOnly;
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
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        if (position < cardList.size()) {
            final Card card = cardList.get(position);
            holder.frontText.setText(card.getFrontText());
            holder.backText.setText(card.getBackText());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(cardListActivity, CardPagerActivity.class);
                    intent.putExtra(BaseActivity.ARG_READ_ONLY, isReadOnly);
                    intent.putExtra(BaseActivity.ARG_DECK, deck);
                    intent.putExtra(BaseActivity.ARG_POSITION, holder.getAdapterPosition());
                    cardListActivity.startActivityForResult(intent, BaseActivity.REQUEST_DECK);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // no editing in read only mode
                    if (!isReadOnly) {
                        cardListActivity.showCardDialog(card);
                    }
                    return true;
                }
            });
            holder.itemView.setVisibility(View.VISIBLE);
            if (card.getCorrectCount() > 0 || card.getIncorrectCount() > 0) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.progressIndicatorCorrect.getLayoutParams();
                params.weight = card.getCorrectCount();
                holder.progressIndicatorCorrect.setLayoutParams(params);

                params = (LinearLayout.LayoutParams) holder.progressIndicatorIncorrect.getLayoutParams();
                params.weight = card.getIncorrectCount();
                holder.progressIndicatorIncorrect.setLayoutParams(params);

                holder.progressIndicatorHolder.setVisibility(View.VISIBLE);
            } else {
                holder.progressIndicatorHolder.setVisibility(View.GONE);
            }
        } else {
            holder.itemView.setVisibility(View.INVISIBLE); // dummy footer
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size() + 1; // +1 for dummy footer
    }

}
