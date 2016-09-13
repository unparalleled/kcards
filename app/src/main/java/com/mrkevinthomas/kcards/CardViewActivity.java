package com.mrkevinthomas.kcards;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.mrkevinthomas.kcards.models.Deck;

import java.util.Locale;

public class CardViewActivity extends BaseActivity {

    private static final int OFFSCREEN_PAGE_LIMIT = 2;

    private Deck deck;
    private int position;

    private ViewPager viewPager;
    private CardViewAdapter cardViewAdapter;

    private TextToSpeech textToSpeech;

    @Override
    protected boolean shouldShowUpButton() {
        return true;
    }

    @Override
    protected int getViewId() {
        return R.layout.view_pager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deck = getIntent().getParcelableExtra(ARG_DECK);
        position = getIntent().getIntExtra(ARG_POSITION, 0);

        getSupportActionBar().setTitle(deck.getName());
        getSupportActionBar().setSubtitle(deck.getDescription());

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);

        cardViewAdapter = new CardViewAdapter(deck, this);
        viewPager.setAdapter(cardViewAdapter);
        viewPager.setCurrentItem(position);
        viewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        textToSpeech.setLanguage(Locale.US);
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
