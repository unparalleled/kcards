package com.mrkevinthomas.kcards.card_swipe;

import android.os.Bundle;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.mrkevinthomas.kcards.CardViewActivity;
import com.mrkevinthomas.kcards.R;
import com.mrkevinthomas.kcards.ThisApp;
import com.mrkevinthomas.kcards.models.Card;

public class CardSwipeActivity extends CardViewActivity {

    private SwipeFlingAdapterView swipeFlingAdapterView;
    private CardSwipeAdapter cardSwipeAdapter;

    @Override
    protected int getViewId() {
        return R.layout.card_swipe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cardSwipeAdapter = new CardSwipeAdapter(this, deck, isSwapped, isHidden);

        swipeFlingAdapterView = (SwipeFlingAdapterView) findViewById(R.id.swipe_container);
        swipeFlingAdapterView.setAdapter(cardSwipeAdapter);
        swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                cardSwipeAdapter.removeFirst();
            }

            @Override
            public void onLeftCardExit(Object o) {
                Card card = (Card) o;
                card.incrementIncorrect();
                ThisApp.get().showToast(R.layout.toast_incorrect);
            }

            @Override
            public void onRightCardExit(Object o) {
                Card card = (Card) o;
                card.incrementCorrect();
                ThisApp.get().showToast(R.layout.toast_correct);
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {
                cardSwipeAdapter.chooseCardsToShow();
            }

            @Override
            public void onScroll(float v) {

            }
        });
    }

    @Override
    protected void handleSwapActionClicked() {
        super.handleSwapActionClicked();
        swipeFlingAdapterView.removeAllViewsInLayout(); // workaround for https://github.com/Diolor/Swipecards/issues/29
        cardSwipeAdapter.setSwapped(isSwapped);
    }

    @Override
    protected void handleShowHideActionClicked() {
        super.handleShowHideActionClicked();
        swipeFlingAdapterView.removeAllViewsInLayout(); // workaround for https://github.com/Diolor/Swipecards/issues/29
        cardSwipeAdapter.setHidden(isSwapped);
    }

    public SwipeFlingAdapterView getSwipeFlingAdapterView() {
        return swipeFlingAdapterView;
    }

}
