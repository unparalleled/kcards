package com.mrkevinthomas.kcards.models;

import android.support.annotation.Nullable;

import java.util.Locale;

public class Language {

    public static final Language DEFAULT = new Language("en", "English", Locale.ENGLISH);

    private static final Language[] languages = {
            new Language("zh-CN", "Chinese", Locale.CHINESE),
            new Language("en", "English", Locale.ENGLISH),
            new Language("fr", "French", Locale.FRENCH),
            new Language("de", "German", Locale.GERMAN),
            new Language("it", "Italian", Locale.ITALIAN),
            new Language("ja", "Japanese", Locale.JAPANESE),
            new Language("ko", "Korean", Locale.KOREAN),
    };

    public static Language[] languages() {
        return languages;
    }

    public static Language fromCode(@Nullable String code) {
        for (Language language : languages) {
            if (language.getGoogleTranslateCode().equalsIgnoreCase(code)) {
                return language;
            }
        }
        return DEFAULT;
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