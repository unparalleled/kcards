package com.mrkevinthomas.kcards;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.mrkevinthomas.kcards.models.Deck;

public class CardSwipeActivity extends BaseActivity {

    private Deck deck;

    private SwipeFlingAdapterView swipeFlingAdapterView;
    private CardSwipeAdapter cardSwipeAdapter;

    private TextToSpeech textToSpeech;

    @Override
    protected boolean shouldShowUpButton() {
        return true;
    }

    @Override
    protected int getViewId() {
        return R.layout.card_swipe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deck = getIntent().getParcelableExtra(ARG_DECK);

        getSupportActionBar().setTitle(deck.getName());
        getSupportActionBar().setSubtitle(deck.getDescription());

        fab.setVisibility(View.GONE);

        swipeFlingAdapterView = (SwipeFlingAdapterView) findViewById(R.id.swipe_container);
        cardSwipeAdapter = new CardSwipeAdapter(deck, this);
        swipeFlingAdapterView.setAdapter(cardSwipeAdapter);
        swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                cardSwipeAdapter.removeFirst();
            }

            @Override
            public void onLeftCardExit(Object o) {

            }

            @Override
            public void onRightCardExit(Object o) {

            }

            @Override
            public void onAdapterAboutToEmpty(int i) {

            }

            @Override
            public void onScroll(float v) {

            }
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

}