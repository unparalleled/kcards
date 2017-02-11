package com.mrkevinthomas.kcards;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.CallSuper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mrkevinthomas.kcards.models.Deck;

public abstract class CardViewActivity extends BaseActivity {
    protected Deck deck;

    protected boolean isSwapped;
    protected boolean isHidden = true; // start with answers hidden

    protected MenuItem showHideMenuItem;

    protected TextToSpeech textToSpeech;

    @Override
    protected boolean shouldShowUpButton() {
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_SWAPPED, isSwapped);
        outState.putBoolean(KEY_IS_HIDDEN, isHidden);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isSwapped = savedInstanceState.getBoolean(KEY_IS_SWAPPED);
            isHidden = savedInstanceState.getBoolean(KEY_IS_HIDDEN);
        }

        deck = getIntent().getParcelableExtra(ARG_DECK);

        getSupportActionBar().setTitle(deck.getName());
        getSupportActionBar().setSubtitle(deck.getDescription());

        fab.setVisibility(View.GONE);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_view_actions, menu);
        showHideMenuItem = menu.findItem(R.id.action_show_hide);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_swap) {
            handleSwapActionClicked();
            return true;
        } else if (item.getItemId() == R.id.action_show_hide) {
            handleShowHideActionClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @CallSuper
    protected void handleSwapActionClicked() {
        isSwapped = !isSwapped;
    }

    @CallSuper
    protected void handleShowHideActionClicked() {
        isHidden = !isHidden;
        showHideMenuItem.setIcon(isHidden ?
                R.drawable.ic_visibility_white_48dp :
                R.drawable.ic_visibility_off_white_48dp);
        showHideMenuItem.setTitle(isHidden ?
                R.string.show :
                R.string.hide);
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

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

}
