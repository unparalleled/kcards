package com.mrkevinthomas.kcards.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.mrkevinthomas.kcards.models.Language;

import java.util.ArrayList;

public class LanguageSpinner extends AppCompatSpinner {

    public LanguageSpinner(Context context) {
        super(context);
    }

    public LanguageSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LanguageSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initialize(final SharedPreferences preferences, final String LANGUAGE_CODE_KEY) {
        ArrayList<String> languageNames = new ArrayList<>();
        for (Language language : Language.languages()) {
            languageNames.add(language.getDisplayName());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, languageNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        setAdapter(arrayAdapter);

        String selectedLanguageCode = preferences.getString(LANGUAGE_CODE_KEY, null);

        // set spinner to current user's references
        int i = 0;
        for (Language language : Language.languages()) {
            if (language.getGoogleTranslateCode().equals(selectedLanguageCode)) {
                setSelection(i);
            }
            i++;
        }

        setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Language language = Language.languages()[i];
                preferences.edit().putString(LANGUAGE_CODE_KEY, language.getGoogleTranslateCode()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // no worries
            }
        });
    }

    public Language getSelectedLanguage() {
        return Language.languages()[getSelectedItemPosition()];
    }

}
