package com.mrkevinthomas.kcards.card_list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mrkevinthomas.kcards.R;

public class CardViewHolder extends RecyclerView.ViewHolder {
    public final TextView frontText;
    public final TextView backText;
    public final View progressIndicatorHolder;
    public final View progressIndicatorCorrect;
    public final View progressIndicatorIncorrect;

    public CardViewHolder(View itemView) {
        super(itemView);
        frontText = (TextView) itemView.findViewById(R.id.card_top_text);
        backText = (TextView) itemView.findViewById(R.id.card_bottom_text);
        progressIndicatorHolder = itemView.findViewById(R.id.progress_indicator_holder);
        progressIndicatorCorrect = itemView.findViewById(R.id.progress_indicator_correct);
        progressIndicatorIncorrect = itemView.findViewById(R.id.progress_indicator_incorrect);
    }
}