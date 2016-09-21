package com.mrkevinthomas.kcards;

import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;

public class CardViewAdapter extends PagerAdapter {

    private Deck deck;

    private CardViewActivity cardViewActivity;
    private boolean isHidden;

    private ArrayList<View> activeViews = new ArrayList<>();

    public CardViewAdapter(@NonNull Deck deck, @NonNull CardViewActivity cardViewActivity, boolean isHidden) {
        this.deck = deck;
        this.cardViewActivity = cardViewActivity;
        this.isHidden = isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
        for (View itemView : activeViews) {
            setupCardAnswerCover(itemView);
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

        TextView frontText = (TextView) itemView.findViewById(R.id.card_front_text);
        TextView backText = (TextView) itemView.findViewById(R.id.card_back_text);

        frontText.setText(card.getFrontText());
        itemView.findViewById(R.id.card_front_holder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewActivity.getTextToSpeech().speak(card.getFrontText(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        backText.setText(card.getBackText());
        itemView.findViewById(R.id.card_back_holder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewActivity.getTextToSpeech().speak(card.getBackText(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

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
        webView.setWebViewClient(new WebViewClient() {
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
