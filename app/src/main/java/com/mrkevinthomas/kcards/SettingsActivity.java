package com.mrkevinthomas.kcards;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.mrkevinthomas.kcards.ui.LanguageSpinner;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";

    private static final String KEY_MAIN_LANGUAGE = "main_language";
    private static final String KEY_SECONDARY_LANGUAGE = "secondary_language";

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(TAG, MODE_PRIVATE);
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
        mainLanguageSpinner.initialize(getPrefs(this), KEY_MAIN_LANGUAGE);
        secondaryLanguageSpinner.initialize(getPrefs(this), KEY_SECONDARY_LANGUAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // don't show settings overflow item
        // this is already the settings activity
        return true;
    }

}
