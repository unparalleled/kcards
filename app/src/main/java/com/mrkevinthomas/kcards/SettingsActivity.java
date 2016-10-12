package com.mrkevinthomas.kcards;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.mrkevinthomas.kcards.models.Language;
import com.mrkevinthomas.kcards.ui.LanguageSpinner;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";

    private static final String KEY_MAIN_LANGUAGE = "main_language";
    private static final String KEY_SECONDARY_LANGUAGE = "secondary_language";

    public static String getMainLanguage(Context context) {
        return context.getSharedPreferences(TAG, MODE_PRIVATE).getString(KEY_MAIN_LANGUAGE, null);
    }

    public static String getSecondaryLanguage(Context context) {
        return context.getSharedPreferences(TAG, MODE_PRIVATE).getString(KEY_SECONDARY_LANGUAGE, null);
    }

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

    private void setupLanguageSpinner(LanguageSpinner languageSpinner, final String LANGUAGE_CODE_KEY) {
        final SharedPreferences preferences = getSharedPreferences(TAG, MODE_PRIVATE);
        String selectedLanguageCode = preferences.getString(LANGUAGE_CODE_KEY, null);
        languageSpinner.setSelectedLanguage(selectedLanguageCode);

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
