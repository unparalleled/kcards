package com.mrkevinthomas.kcards.deck_list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrkevinthomas.kcards.R;

public class DeckViewHolder extends RecyclerView.ViewHolder {
    public final TextView deckName;
    public final TextView deckDescription;
    public final TextView deckCount;
    public final View deckCountHolder;
    public final ImageView deckActionIcon;
    public final TextView deckCreated;
    public final TextView deckUpdated;

    public DeckViewHolder(View itemView) {
        super(itemView);
        deckName = (TextView) itemView.findViewById(R.id.deck_name);
        deckDescription = (TextView) itemView.findViewById(R.id.deck_description);
        deckCount = (TextView) itemView.findViewById(R.id.deck_count);
        deckCountHolder = itemView.findViewById(R.id.deck_count_holder);
        deckActionIcon = (ImageView) itemView.findViewById(R.id.deck_action_icon);
        deckCreated = (TextView) itemView.findViewById(R.id.deck_created);
        deckUpdated = (TextView) itemView.findViewById(R.id.deck_updated);
    }
}