package com.mrkevinthomas.kcards.card_pager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.mrkevinthomas.kcards.CardViewActivity;
import com.mrkevinthomas.kcards.R;

public class CardPagerActivity extends CardViewActivity {

    private static final int OFFSCREEN_PAGE_LIMIT = 2;

    private CardPagerAdapter cardPagerAdapter;

    @Override
    protected int getViewId() {
        return R.layout.view_pager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int position = getIntent().getIntExtra(ARG_POSITION, 0);
        boolean isReadOnly = getIntent().getBooleanExtra(ARG_READ_ONLY, false);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        cardPagerAdapter = new CardPagerAdapter(this, deck, isReadOnly, isSwapped, isHidden);
        viewPager.setAdapter(cardPagerAdapter);
        viewPager.setCurrentItem(position);
        viewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
    }

    @Override
    protected void handleSwapActionClicked() {
        super.handleSwapActionClicked();
        cardPagerAdapter.setSwapped(isSwapped);
    }

    @Override
    protected void handleShowHideActionClicked() {
        super.handleShowHideActionClicked();
        cardPagerAdapter.setHidden(isHidden);
    }

}
