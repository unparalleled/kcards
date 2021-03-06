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
import com.mrkevinthomas.kcards.views.CardItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class CardSwipeAdapter extends BaseAdapter implements CardItem.Delegate {

    private CardSwipeActivity cardSwipeActivity;
    private Deck deck;
    private boolean isSwapped;
    private boolean isHidden;

    private List<Card> cards = new ArrayList<>();

    private Map<Long, CardItem> activeViews = new HashMap<>();

    public CardSwipeAdapter(@NonNull CardSwipeActivity cardSwipeActivity, @NonNull Deck deck, boolean isSwapped, boolean isHidden) {
        this.cardSwipeActivity = cardSwipeActivity;
        this.deck = deck;
        this.isSwapped = isSwapped;
        this.isHidden = isHidden;
        chooseCardsToShow();
    }

    public void setSwapped(boolean swapped) {
        isSwapped = swapped;
        for (CardItem cardItem : activeViews.values()) {
            cardItem.setupCardText();
        }
        notifyDataSetChanged();
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
        for (CardItem cardItem : activeViews.values()) {
            cardItem.setupCardAnswerCover();
        }
        notifyDataSetChanged();
    }

    public void removeFirst() {
        activeViews.remove(cards.get(0).getId());
        cards.remove(0);
        notifyDataSetChanged();
    }

    public void chooseCardsToShow() {
        final int CARD_LIST_SIZE = 3;
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
            if (!cards.contains(chosen) || cards.containsAll(deck.getCards())) {
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
        Card card = cards.get(position);

        CardItem cardItem;
        if (activeViews.get(card.getId()) != null) {
            cardItem = activeViews.get(card.getId());
        } else {
            if (convertView != null) {
                cardItem = (CardItem) convertView;
                cardItem.fillViews(card, this);
            } else {
                cardItem = (CardItem) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
                cardItem.findViews();
                cardItem.fillViews(card, this);
            }
            activeViews.put(card.getId(), cardItem);
        }

        return cardItem;
    }

    @Override
    public boolean isHidden() {
        return isHidden;
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
