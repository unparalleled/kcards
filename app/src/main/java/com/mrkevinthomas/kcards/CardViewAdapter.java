package com.mrkevinthomas.kcards;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.mrkevinthomas.kcards.models.Card;
import com.mrkevinthomas.kcards.models.Deck;

import java.util.ArrayList;
import java.util.List;

public class CardViewAdapter extends PagerAdapter {

    private List<Card> cardList = new ArrayList<>();
    private Deck deck;

    public CardViewAdapter(@NonNull Deck deck) {
        this.cardList = deck.getCards();
        this.deck = deck;
    }

    @Override
    public int getCount() {
        return cardList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.card_view_item, container, false);
        Card card = cardList.get(position);

        TextView frontText = (TextView) itemView.findViewById(R.id.card_front_text);
        TextView backText = (TextView) itemView.findViewById(R.id.card_back_text);
        WebView webView = (WebView) itemView.findViewById(R.id.web_view);

        frontText.setText(card.getFrontText());
        backText.setText(card.getBackText());

        webView.setWebViewClient(new WebViewClient());
        final String URL = "https://www.google.com/search?tbm=isch&q=" + card.getFrontText();
        webView.loadUrl(URL);

        container.addView(itemView, 0);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
