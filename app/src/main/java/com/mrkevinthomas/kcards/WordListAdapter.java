package com.mrkevinthomas.kcards;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kt on 9/5/16.
 */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordHolder> {

    private ArrayList<String> wordList = new ArrayList<>();

    public void setWordList(ArrayList<String> wordList) {
        this.wordList = wordList;
        notifyDataSetChanged();
    }

    public void addWord(String word) {
        wordList.add(word);
        notifyItemInserted(wordList.size() - 1);
    }

    @Override
    public WordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WordHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.word_view, null));
    }

    @Override
    public void onBindViewHolder(WordHolder holder, int position) {
        holder.wordText.setText(wordList.get(position));
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class WordHolder extends RecyclerView.ViewHolder {
        public final TextView wordText;

        public WordHolder(View itemView) {
            super(itemView);
            wordText = (TextView) itemView.findViewById(R.id.word_text);
        }
    }
}