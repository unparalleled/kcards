package com.mrkevinthomas.kcards.ui;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mrkevinthomas.kcards.R;
import com.mrkevinthomas.kcards.ThisApp;
import com.mrkevinthomas.kcards.models.Card;

public class CardItem extends FrameLayout {

    public interface Delegate {
        boolean isHidden();
        boolean isSwapped();
        boolean isReadOnly();
        TextToSpeech getTextToSpeech();
        void onCorrect();
        void onIncorrect();
    }

    private Card card;
    private Delegate delegate;

    private TextView topTextView;
    private TextView bottomTextView;
    private View cardAnswerCover;
    private FloatingActionButton correctFab;
    private FloatingActionButton incorrectFab;
    private WebView webView;
    private ProgressBar progressBar;

    public CardItem(Context context) {
        this(context, null);
    }

    public CardItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.card_item_view, this, true);
    }

    public void setupViews(@NonNull Card card, @NonNull Delegate delegate) {
        this.card = card;
        this.delegate = delegate;

        topTextView = (TextView) findViewById(R.id.card_top_text);
        bottomTextView = (TextView) findViewById(R.id.card_bottom_text);
        cardAnswerCover = findViewById(R.id.card_answer_cover);
        correctFab = (FloatingActionButton) findViewById(R.id.fab_correct);
        incorrectFab = (FloatingActionButton) findViewById(R.id.fab_wrong);
        webView = (WebView) findViewById(R.id.web_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        setupCardText();
        setupCardAnswerCover();
        setupCorrectAndWrongFabs();
        setupWebView();
    }

    public void setupCardText() {
        final String topText = delegate.isSwapped() ? card.getBackText() : card.getFrontText();
        topTextView.setText(topText);
        topTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.getTextToSpeech()
                        .setLanguage(delegate.isSwapped() ? card.getBackLanguage().getLocale() : card.getFrontLanguage().getLocale());
                delegate.getTextToSpeech().speak(topText, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        final String bottomText = delegate.isSwapped() ? card.getFrontText() : card.getBackText();
        bottomTextView.setText(bottomText);
        bottomTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.getTextToSpeech()
                        .setLanguage(delegate.isSwapped() ? card.getFrontLanguage().getLocale() : card.getBackLanguage().getLocale());
                delegate.getTextToSpeech().speak(bottomText, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    public void setupCardAnswerCover() {
        if (delegate.isHidden()) {
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

    private void setupCorrectAndWrongFabs() {
        correctFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.incrementCorrect();
                correctFab.setVisibility(View.GONE);
                incorrectFab.setVisibility(View.GONE);
                ThisApp.get().showToast(R.layout.toast_correct);
                delegate.onCorrect();
            }
        });

        incorrectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.incrementIncorrect();
                correctFab.setVisibility(View.GONE);
                incorrectFab.setVisibility(View.GONE);
                ThisApp.get().showToast(R.layout.toast_incorrect);
                delegate.onIncorrect();
            }
        });

        if (delegate.isReadOnly()) {
            correctFab.setVisibility(View.GONE);
            incorrectFab.setVisibility(View.GONE);
        }
    }

    private void setupWebView() {
        final String CACHE_MISS_DESCRIPTION = "net::ERR_CACHE_MISS";

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
