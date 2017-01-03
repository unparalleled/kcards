package com.mrkevinthomas.kcards.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import com.mrkevinthomas.kcards.Logger;
import com.mrkevinthomas.kcards.models.Language;

import java.util.ArrayList;

public class LanguageSpinner extends AppCompatSpinner {
    private static final String TAG = "LanguageSpinner";

    public LanguageSpinner(Context context) {
        this(context, null);
    }

    public LanguageSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LanguageSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void initialize() {
        ArrayList<String> languageNames = new ArrayList<>();
        for (Language language : Language.languages()) {
            languageNames.add(language.getDisplayName());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, languageNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        setAdapter(arrayAdapter);
    }

    public void setSelectedLanguage(@Nullable String selectedLanguageCode) {
        if (selectedLanguageCode == null) {
            // use default language
            selectedLanguageCode = Language.DEFAULT.getGoogleTranslateCode();
        }

        int i = 0;
        for (Language language : Language.languages()) {
            if (language.getGoogleTranslateCode().equals(selectedLanguageCode)) {
                setSelection(i);
                return;
            }
            i++;
        }

        Logger.w(TAG, "could not find language code in list of supported languages");
    }

    public Language getSelectedLanguage() {
        return Language.languages()[getSelectedItemPosition()];
    }

}
