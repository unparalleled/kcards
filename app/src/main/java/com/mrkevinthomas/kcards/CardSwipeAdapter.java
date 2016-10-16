package com.mrkevinthomas.kcards;

import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mrkevinthomas.kcards.models.Deck;
import com.mrkevinthomas.kcards.ui.CardItem;

public class CardSwipeAdapter extends BaseAdapter implements CardItem.Delegate {

    private Deck deck;
    private CardSwipeActivity cardSwipeActivity;

    public CardSwipeAdapter(@NonNull Deck deck, @NonNull CardSwipeActivity cardSwipeActivity) {
        this.deck = deck;
        this.cardSwipeActivity = cardSwipeActivity;
    }

    public void removeFirst() {
        this.deck.getCards().remove(0);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return deck.size();
    }

    @Override
    public Object getItem(int position) {
        return deck.getCards().get(position);
    }

    @Override
    public long getItemId(int position) {
        return deck.getCards().get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardItem cardItem = (CardItem) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);

        cardItem.setupViews(deck.getCards().get(position), this);

        return cardItem;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean isSwapped() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public TextToSpeech getTextToSpeech() {
        return cardSwipeActivity.getTextToSpeech();
    }

}
