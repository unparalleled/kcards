package com.mrkevinthomas.kcards;

import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrkevinthomas.kcards.models.Deck;
import com.mrkevinthomas.kcards.ui.CardItem;

import java.util.ArrayList;

public class CardPagerAdapter extends PagerAdapter implements CardItem.Delegate {

    private Deck deck;

    private CardPagerActivity cardPagerActivity;
    private boolean isHidden;
    private boolean isSwapped;
    private boolean isReadOnly;

    private ArrayList<CardItem> activeViews = new ArrayList<>();

    public CardPagerAdapter(@NonNull Deck deck, @NonNull CardPagerActivity cardPagerActivity, boolean isHidden, boolean isReadOnly) {
        this.deck = deck;
        this.cardPagerActivity = cardPagerActivity;
        this.isHidden = isHidden;
        this.isReadOnly = isReadOnly;
    }

    public void setHidden(boolean hidden) {
        this.isHidden = hidden;
        for (CardItem cardItem : activeViews) {
            cardItem.setupCardAnswerCover();
        }
    }

    public void swap() {
        isSwapped = !isSwapped;
        for (CardItem cardItem : activeViews) {
            cardItem.setupCardText();
        }
    }

    @Override
    public int getCount() {
        return deck.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        CardItem cardItem = (CardItem) LayoutInflater.from(container.getContext()).inflate(R.layout.card_item, container, false);

        cardItem.setupViews(deck.getCards().get(position), this);

        container.addView(cardItem, 0);
        activeViews.add(cardItem);
        return cardItem;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        activeViews.remove(object);
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
        return isReadOnly;
    }

    @Override
    public TextToSpeech getTextToSpeech() {
        return cardPagerActivity.getTextToSpeech();
    }

    @Override
    public void onCorrect() {

    }

    @Override
    public void onIncorrect() {

    }

}
