package com.mrkevinthomas.kcards;

import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;

public class CardViewAdapter extends PagerAdapter {
    private static final String CACHE_MISS_DESCRIPTION = "net::ERR_CACHE_MISS";

    private Deck deck;

    private CardViewActivity cardViewActivity;
    private boolean isHidden;
    private boolean isSwapped;
    private boolean isReadOnly;

    private ArrayList<View> activeViews = new ArrayList<>();

    public CardViewAdapter(@NonNull Deck deck, @NonNull CardViewActivity cardViewActivity, boolean isHidden, boolean isReadOnly) {
        this.deck = deck;
        this.cardViewActivity = cardViewActivity;
        this.isHidden = isHidden;
        this.isReadOnly = isReadOnly;
    }

    public void setHidden(boolean hidden) {
        this.isHidden = hidden;
        for (View itemView : activeViews) {
            setupCardAnswerCover(itemView);
        }
    }

    public void swap() {
        isSwapped = !isSwapped;
        for (View itemView : activeViews) {
            setupCardText(itemView, (Card) itemView.getTag());
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
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.card_view_item, container, false);
        final Card card = deck.getCards().get(position);
        itemView.setTag(card);

        setupCardText(itemView, card);
        setupCorrectAndWrongFabs(itemView, card);
        setupCardAnswerCover(itemView);
        setupWebView(itemView, card);

        container.addView(itemView, 0);
        activeViews.add(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        activeViews.remove(object);
    }

    private void setupCardText(View itemView, final Card card) {
        ViewGroup topHolder = (ViewGroup) itemView.findViewById(R.id.card_top_holder);
        ViewGroup bottomHolder = (ViewGroup) itemView.findViewById(R.id.card_bottom_holder);
        TextView topTextView = (TextView) itemView.findViewById(R.id.card_top_text);
        TextView bottomTextView = (TextView) itemView.findViewById(R.id.card_bottom_text);

        final String topText = isSwapped ? card.getBackText() : card.getFrontText();
        topTextView.setText(topText);
        topHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewActivity.getTextToSpeech().speak(topText, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        final String bottomText = isSwapped ? card.getFrontText() : card.getBackText();
        bottomTextView.setText(bottomText);
        bottomHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewActivity.getTextToSpeech().speak(bottomText, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    private void setupCorrectAndWrongFabs(View itemView, final Card card) {
        final FloatingActionButton correctFab = (FloatingActionButton) itemView.findViewById(R.id.fab_correct);
        final FloatingActionButton incorrectFab = (FloatingActionButton) itemView.findViewById(R.id.fab_wrong);

        correctFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.incrementCorrect();
                correctFab.setVisibility(View.GONE);
                incorrectFab.setVisibility(View.GONE);
                showToast(R.layout.toast_correct);
            }
        });

        incorrectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.incrementIncorrect();
                correctFab.setVisibility(View.GONE);
                incorrectFab.setVisibility(View.GONE);
                showToast(R.layout.toast_incorrect);
            }
        });

        if (isReadOnly) {
            correctFab.setVisibility(View.GONE);
            incorrectFab.setVisibility(View.GONE);
        }
    }

    private void showToast(int layoutId) {
        Toast toast = new Toast(cardViewActivity);
        toast.setView(cardViewActivity.getLayoutInflater().inflate(layoutId, null));
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    private void setupCardAnswerCover(View itemView) {
        final View cardAnswerCover = itemView.findViewById(R.id.card_answer_cover);

        if (isHidden) {
            cardAnswerCover.setVisibility(View.VISIBLE);
            cardAnswerCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardAnswerCover.setVisibility(View.GONE);
                }
            });
        } else {
            cardAnswerCover.setVisibility(View.GONE);
        }
    }

    private void setupWebView(View itemView, Card card) {
        final WebView webView = (WebView) itemView.findViewById(R.id.web_view);
        final ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);

        webView.getSettings().setJavaScriptEnabled(true);

        if (ThisApp.get().isConnected()) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY); // attempt a force load of the cache
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (CACHE_MISS_DESCRIPTION.equals(description)) {
                    // clear error response
                    view.loadUrl("about:blank");
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // hide page headers
                webView.loadUrl("javascript: document.getElementById('main').scrollIntoView();");
                webView.loadUrl("javascript: document.getElementById('ires').scrollIntoView();");

                // wait a short period for scrolling to complete
                webView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1000);
            }
        });
        final String URL = "https://www.google.com/search?tbm=isch&q=" + card.getFrontText();
        webView.loadUrl(URL);
    }

}
