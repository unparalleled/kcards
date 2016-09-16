package com.mrkevinthomas.kcards;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mrkevinthomas.kcards.models.Language;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";

    private static final String LANGUAGE_FILE = "languages.json";

    private static final String MAIN_LANGUAGE = "main_language";
    private static final String SECONDARY_LANGUAGE = "secondary_language";

    private SharedPreferences preferences;

    private List<Language> availableLanguages;

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

        appVersionTextView.setText(
                getString(R.string.app_version_text, ThisApp.get().getAppVersionName(), ThisApp.get().getAppVersionCode()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadLanguages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // don't show settings overflow item
        // this is already the settings activity
        return true;
    }

    private void loadLanguages() {
        SQLite.select()
                .from(Language.class)
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<Language>() {
                    @Override
                    public void onQueryResult(QueryTransaction transaction, @NonNull CursorResult<Language> tResult) {
                        // called when query returns on UI thread
                        List<Language> languages = tResult.toListClose();
                        if (languages != null && !languages.isEmpty()) {
                            availableLanguages = languages;
                        } else {
                            loadLanguagesFromFile();
                        }
                        setupLanguageSpinners();
                    }
                }).execute();
    }

    private void loadLanguagesFromFile() {
        try {
            Reader reader = new InputStreamReader(getAssets().open(LANGUAGE_FILE), "UTF-8");
            Language[] languages = new Gson().fromJson(reader, Language[].class);
            if (languages != null && languages.length > 0) {
                for (Language language : languages) {
                    language.save();
                }
                availableLanguages = Arrays.asList(languages);
            }
        } catch (IOException e) {
            // this should be extremely rare, but not the end of the world if it happens
            Log.e(TAG, "failed to load languages json file", e);
            Analytics.logLoadFileFailed(LANGUAGE_FILE);
        }
    }

    private void setupLanguageSpinners() {
        ArrayList<String> languageNames = new ArrayList<>();
        for (Language language : availableLanguages) {
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
        for (Language language : availableLanguages) {
            if (language.getCode().equals(mainLanguageCode)) {
                mainLanguageSpinner.setSelection(i);
            }
            if (language.getCode().equals(secondaryLanguageCode)) {
                secondaryLanguageSpinner.setSelection(i);
            }
            i++;
        }

        mainLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Language language = availableLanguages.get(i);
                preferences.edit().putString(MAIN_LANGUAGE, language.getCode()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // no worries
            }
        });

        secondaryLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Language language = availableLanguages.get(i);
                preferences.edit().putString(SECONDARY_LANGUAGE, language.getCode()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // no worries
            }
        });
    }

}
