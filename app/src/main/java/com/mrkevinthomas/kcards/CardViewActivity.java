package com.mrkevinthomas.kcards;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.mrkevinthomas.kcards.models.Deck;

public class CardViewActivity extends BaseActivity {

    private Deck deck;
    private int position;

    private ViewPager viewPager;
    private CardViewAdapter cardViewAdapter;

    @Override
    protected boolean shouldShowUpButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deck = getIntent().getParcelableExtra(ARG_DECK);
        position = getIntent().getIntExtra(ARG_POSITION, 0);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);

        cardViewAdapter = new CardViewAdapter(deck);
        viewPager.setAdapter(cardViewAdapter);
        viewPager.setCurrentItem(position);
    }

}
