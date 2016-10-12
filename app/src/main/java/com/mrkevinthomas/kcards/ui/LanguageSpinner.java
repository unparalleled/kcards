package com.mrkevinthomas.kcards.ui;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import com.mrkevinthomas.kcards.models.Language;

import java.util.ArrayList;

public class LanguageSpinner extends AppCompatSpinner {

    public LanguageSpinner(Context context) {
        super(context);
        initialize();
    }

    public LanguageSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
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

    public void setSelectedLanguage(String selectedLanguageCode) {
        int i = 0;
        for (Language language : Language.languages()) {
            if (language.getGoogleTranslateCode().equals(selectedLanguageCode)) {
                setSelection(i);
            }
            i++;
        }
    }

    public Language getSelectedLanguage() {
        return Language.languages()[getSelectedItemPosition()];
    }

}
