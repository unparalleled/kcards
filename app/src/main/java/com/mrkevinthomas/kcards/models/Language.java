package com.mrkevinthomas.kcards.models;

import java.util.Locale;

public class Language {

    private static final Language[] languages = {
            new Language("en", "English", Locale.ENGLISH),
            new Language("ko", "Korean", Locale.KOREAN),
    };

    public static Language[] languages() {
        return languages;
    }

    // use this as a unique key for a Language
    private final String googleTranslateCode;
    private final String displayName;
    private final Locale locale;

    private Language(String googleTranslateCode, String displayName, Locale locale) {
        this.googleTranslateCode = googleTranslateCode;
        this.displayName = displayName;
        this.locale = locale;
    }

    public String getGoogleTranslateCode() {
        return googleTranslateCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Locale getLocale() {
        return locale;
    }

}