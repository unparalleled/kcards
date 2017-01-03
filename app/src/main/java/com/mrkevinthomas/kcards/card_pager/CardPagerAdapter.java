package com.mrkevinthomas.kcards.card_pager;

import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrkevinthomas.kcards.R;
import com.mrkevinthomas.kcards.models.Deck;
import com.mrkevinthomas.kcards.views.CardItem;

import java.util.ArrayList;

public class CardPagerAdapter extends PagerAdapter implements CardItem.Delegate {

    private CardPagerActivity cardPagerActivity;
    private Deck deck;
    private boolean isReadOnly;
    private boolean isSwapped;
    private boolean isHidden;

    private ArrayList<CardItem> activeViews = new ArrayList<>();

    public CardPagerAdapter(@NonNull CardPagerActivity cardPagerActivity,
                            @NonNull Deck deck,
                            boolean isReadOnly,
                            boolean isSwapped,
                            boolean isHidden) {
        this.cardPagerActivity = cardPagerActivity;
        this.deck = deck;
        this.isReadOnly = isReadOnly;
        this.isSwapped = isSwapped;
        this.isHidden = isHidden;
    }

    public void setSwapped(boolean swapped) {
        isSwapped = swapped;
        for (CardItem cardItem : activeViews) {
            cardItem.setupCardText(); // update ui directly without having to notify a dataset change
        }
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
        for (CardItem cardItem : activeViews) {
            cardItem.setupCardAnswerCover(); // update ui directly without having to notify a dataset change
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
