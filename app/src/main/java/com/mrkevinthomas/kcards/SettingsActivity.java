package com.mrkevinthomas.kcards;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.mrkevinthomas.kcards.models.Language;
import com.mrkevinthomas.kcards.ui.LanguageSpinner;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";

    private TextView appVersionTextView;
    private LanguageSpinner mainLanguageSpinner;
    private LanguageSpinner secondaryLanguageSpinner;

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

        appVersionTextView = (TextView) findViewById(R.id.settings_app_version);
        mainLanguageSpinner = (LanguageSpinner) findViewById(R.id.settings_main_language);
        secondaryLanguageSpinner = (LanguageSpinner) findViewById(R.id.settings_secondary_language);

        appVersionTextView.setText(getString(R.string.app_version_text,
                ThisApp.get().getAppVersionName(),
                String.valueOf(ThisApp.get().getAppVersionCode())));

        mainLanguageSpinner.setSelectedLanguage(Preferences.getMainLanguageCode(this));
        setupLanguageSpinnerListener(mainLanguageSpinner);

        secondaryLanguageSpinner.setSelectedLanguage(Preferences.getSecondaryLanguageCode(this));
        setupLanguageSpinnerListener(secondaryLanguageSpinner);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // don't show settings overflow item
        // this is already the settings activity
        return true;
    }

    private void setupLanguageSpinnerListener(LanguageSpinner languageSpinner) {
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Language language = Language.languages()[i];
                if (adapterView == mainLanguageSpinner) {
                    Preferences.putMainLanguageCode(language.getGoogleTranslateCode());
                } else if (adapterView == secondaryLanguageSpinner) {
                    Preferences.putSecondaryLanguageCode(language.getGoogleTranslateCode());
                } else {
                    Logger.w(TAG, "unknown spinner view");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // no worries
            }
        });
    }

}
