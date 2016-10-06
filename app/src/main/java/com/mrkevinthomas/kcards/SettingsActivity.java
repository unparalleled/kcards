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

    private static final String MAIN_LANGUAGE = "main_language";
    private static final String SECONDARY_LANGUAGE = "secondary_language";

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
        setupLanguageSpinners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // don't show settings overflow item
        // this is already the settings activity
        return true;
    }

    private void setupLanguageSpinners() {
        ArrayList<String> languageNames = new ArrayList<>();
        for (Language language : Language.languages()) {
            languageNames.add(language.getDisplayName());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languageNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mainLanguageSpinner.setAdapter(arrayAdapter);
        secondaryLanguageSpinner.setAdapter(arrayAdapter);

        // set spinners to current user's references
        String mainLanguageCode = preferences.getString(MAIN_LANGUAGE, null);
        String secondaryLanguageCode = preferences.getString(SECONDARY_LANGUAGE, null);
        int i = 0;
        for (Language language : Language.languages()) {
            if (language.getGoogleTranslateCode().equals(mainLanguageCode)) {
                mainLanguageSpinner.setSelection(i);
            }
            if (language.getGoogleTranslateCode().equals(secondaryLanguageCode)) {
                secondaryLanguageSpinner.setSelection(i);
            }
            i++;
        }

        mainLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Language language = Language.languages()[i];
                preferences.edit().putString(MAIN_LANGUAGE, language.getGoogleTranslateCode()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // no worries
            }
        });

        secondaryLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Language language = Language.languages()[i];
                preferences.edit().putString(SECONDARY_LANGUAGE, language.getGoogleTranslateCode()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // no worries
            }
        });
    }

}
