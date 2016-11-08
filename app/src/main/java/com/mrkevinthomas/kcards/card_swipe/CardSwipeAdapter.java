package com.mrkevinthomas.kcards.card_swipe;

import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mrkevinthomas.kcards.R;
import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;
import com.mrkevinthomas.kcards.ui.CardItem;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class CardSwipeAdapter extends BaseAdapter implements CardItem.Delegate {

    private Deck deck;
    private CardSwipeActivity cardSwipeActivity;
    private boolean isSwapped;

    private List<Card> cards = new ArrayList<>();

    public CardSwipeAdapter(@NonNull Deck deck, @NonNull CardSwipeActivity cardSwipeActivity) {
        this.deck = deck;
        this.cardSwipeActivity = cardSwipeActivity;
        chooseCardsToShow();
    }

    public void swap() {
        isSwapped = !isSwapped;
        notifyDataSetChanged();
    }

    public void removeFirst() {
        cards.remove(0);
        notifyDataSetChanged();
    }

    public void chooseCardsToShow() {
        final int CARD_LIST_SIZE = Math.min(3, deck.size());
        final double DEFAULT_WEIGHT = 10;
        final Random RANDOM = new Random();

        // first created weighted map of all the cards in the deck
        final NavigableMap<Double, Card> map = new TreeMap<>();
        double total = 0;
        for (Card card : deck.getCards()) {
            int count = card.getCorrectCount() + card.getIncorrectCount();
            double ratio = count == 0 ? 0 : card.getCorrectCount() / count;
            double weight = ratio == 0 ? DEFAULT_WEIGHT : 1 / ratio;
            total += weight;
            map.put(total, card);
        }

        // then choose a given number of cards using random weighted selection
        while (cards.size() < CARD_LIST_SIZE) {
            double value = RANDOM.nextDouble() * total;
            Card chosen = map.ceilingEntry(value).getValue();
            if (!cards.contains(chosen)) {
                cards.add(chosen);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cards.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardItem cardItem = (CardItem) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        cardItem.setupViews(cards.get(position), this);
        return cardItem;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean isSwapped() {
        return isSwapped;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public TextToSpeech getTextToSpeech() {
        return cardSwipeActivity.getTextToSpeech();
    }

    @Override
    public void onCorrect() {
        cardSwipeActivity.getSwipeFlingAdapterView().getTopCardListener().selectRight();
    }

    @Override
    public void onIncorrect() {
        cardSwipeActivity.getSwipeFlingAdapterView().getTopCardListener().selectLeft();
    }

}