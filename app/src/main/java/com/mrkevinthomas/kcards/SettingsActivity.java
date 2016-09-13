package com.mrkevinthomas.kcards;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends BaseActivity {

    private TextView appVersionTextView;

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

        appVersionTextView = (TextView) findViewById(R.id.app_version_text);
        appVersionTextView.setText(
                getString(R.string.app_version_text, ThisApp.get().getAppVersionName(), ThisApp.get().getAppVersionCode()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // don't show settings overflow item
        // this is already the settings activity
        return true;
    }

}
