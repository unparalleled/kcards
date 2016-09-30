package com.mrkevinthomas.kcards;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mrkevinthomas.kcards.models.Deck;

import java.util.Locale;

public class CardViewActivity extends BaseActivity {

    private static final int OFFSCREEN_PAGE_LIMIT = 2;

    private Deck deck;
    private int position;

    private ViewPager viewPager;
    private CardViewAdapter cardViewAdapter;

    private MenuItem showHideMenuItem;
    private boolean isHidden = true; // start with answers hidden
    private boolean isReadOnly;

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
        isReadOnly = getIntent().getBooleanExtra(ARG_READ_ONLY, false);

        getSupportActionBar().setTitle(deck.getName());
        getSupportActionBar().setSubtitle(deck.getDescription());

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);

        cardViewAdapter = new CardViewAdapter(deck, this, isHidden, isReadOnly);
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
        getMenuInflater().inflate(R.menu.card_viewer, menu);
        showHideMenuItem = menu.findItem(R.id.action_show_hide);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_hide) {
            handleShowHideActionClicked();
            return true;
        } else if (item.getItemId() == R.id.action_show_swap) {
            cardViewAdapter.swap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleShowHideActionClicked() {
        isHidden = !isHidden;
        cardViewAdapter.setHidden(isHidden);
        showHideMenuItem.setIcon(isHidden ?
                R.drawable.ic_visibility_white_48dp :
                R.drawable.ic_visibility_off_white_48dp);
        showHideMenuItem.setTitle(isHidden ?
                R.string.show :
                R.string.hide);
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

}
