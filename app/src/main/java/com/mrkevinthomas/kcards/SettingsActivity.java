package com.mrkevinthomas.kcards;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.mrkevinthomas.kcards.models.Language;
import com.mrkevinthomas.kcards.views.LanguageSpinner;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";

    private TextView appVersionTextView;
    private LanguageSpinner mainLanguageSpinner;
    private LanguageSpinner secondaryLanguageSpinner;

    private View debugLayout;
    private TextView loginIdText;

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

        setupDebugView();
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

    private void setupDebugView() {
        if (Preferences.isDebugMode()) {
            debugLayout = findViewById(R.id.settings_debug_layout);
            debugLayout.setVisibility(View.VISIBLE);

            loginIdText = (TextView) findViewById(R.id.settings_login_id);
            String loginId = ThisApp.get().getFirebaseUser() != null ? ThisApp.get().getFirebaseUser().getUid() : null;
            loginIdText.setText(getString(R.string.settings_login_id, loginId));

            appVersionTextView.setOnClickListener(null); // clear listener in case it was previously set
        } else {
            appVersionTextView.setOnClickListener(new View.OnClickListener() {
                private static final int COUNT_NEEDED = 10;
                private int count;
                private Toast toaster;

                @Override
                public void onClick(View v) {
                    if (toaster == null) {
                        toaster = Toast.makeText(getApplication(), "", Toast.LENGTH_LONG);
                    }

                    count++;
                    if (count >= COUNT_NEEDED) {
                        Preferences.setDebugMode();
                        setupDebugView();
                        toaster.setText(R.string.settings_debug_set);
                        toaster.show();
                    } else {
                        String countString = getString(R.string.settings_debug_clicks, COUNT_NEEDED - count);
                        toaster.setText(countString);
                        toaster.show();
                    }
                }
            });
        }
    }

}
