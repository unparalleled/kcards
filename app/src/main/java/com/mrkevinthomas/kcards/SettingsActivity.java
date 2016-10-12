package com.mrkevinthomas.kcards;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.mrkevinthomas.kcards.models.Language;

import java.util.ArrayList;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";

    private static final String KEY_MAIN_LANGUAGE = "main_language";
    private static final String KEY_SECONDARY_LANGUAGE = "secondary_language";

    private SharedPreferences preferences;

    private TextView appVersionTextView;
    private Spinner mainLanguageSpinner;
    private Spinner secondaryLanguageSpinner;

    protected boolean shouldShowUpButton() {
        return true;
    }

    protected int getViewId() {
        return R.layout.settings_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fab.setVisibility(View.GONE);
        getSupportActionBar().setTitle(R.string.settings);

        preferences = getSharedPreferences(TAG, MODE_PRIVATE);

        appVersionTextView = (TextView) findViewById(R.id.settings_app_version);
        mainLanguageSpinner = (Spinner) findViewById(R.id.settings_main_language);
        secondaryLanguageSpinner = (Spinner) findViewById(R.id.settings_secondary_language);

        appVersionTextView.setText(getString(R.string.app_version_text,
                ThisApp.get().getAppVersionName(),
                String.valueOf(ThisApp.get().getAppVersionCode())));
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupLanguageSpinner(mainLanguageSpinner, KEY_MAIN_LANGUAGE);
        setupLanguageSpinner(secondaryLanguageSpinner, KEY_SECONDARY_LANGUAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // don't show settings overflow item
        // this is already the settings activity
        return true;
    }

    private void setupLanguageSpinner(Spinner languageSpinner, final String LANGUAGE_CODE_KEY) {
        ArrayList<String> languageNames = new ArrayList<>();
        for (Language language : Language.languages()) {
            languageNames.add(language.getDisplayName());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languageNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        languageSpinner.setAdapter(arrayAdapter);

        String languageCode = preferences.getString(LANGUAGE_CODE_KEY, null);

        // set spinner to current user's references
        int i = 0;
        for (Language language : Language.languages()) {
            if (language.getGoogleTranslateCode().equals(languageCode)) {
                languageSpinner.setSelection(i);
            }
            i++;
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

}
