package com.mrkevinthomas.kcards.card_swipe;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.mrkevinthomas.kcards.BaseActivity;
import com.mrkevinthomas.kcards.R;
import com.mrkevinthomas.kcards.ThisApp;
import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;

public class CardSwipeActivity extends BaseActivity {

    private Deck deck;

    private boolean isSwapped;

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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_SWAPPED, isSwapped);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deck = getIntent().getParcelableExtra(ARG_DECK);

        if (savedInstanceState != null) {
            isSwapped = savedInstanceState.getBoolean(KEY_IS_SWAPPED);
        }

        getSupportActionBar().setTitle(deck.getName());
        getSupportActionBar().setSubtitle(deck.getDescription());

        fab.setVisibility(View.GONE);

        cardSwipeAdapter = new CardSwipeAdapter(this, deck, isSwapped);

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

    @Override
    public void finish() {
        // set the updated deck before finishing
        Intent result = new Intent();
        result.putExtra(ARG_DECK, deck);
        setResult(RESULT_OK, result);

        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_swipe, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_swap) {
            // workaround for https://github.com/Diolor/Swipecards/issues/29
            swipeFlingAdapterView.removeAllViewsInLayout();
            isSwapped = !isSwapped;
            cardSwipeAdapter.setSwapped(isSwapped);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

    public SwipeFlingAdapterView getSwipeFlingAdapterView() {
        return swipeFlingAdapterView;
    }

}
