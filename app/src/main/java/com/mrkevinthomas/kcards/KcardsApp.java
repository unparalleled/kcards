package com.mrkevinthomas.kcards;

import android.app.Application;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by kt on 9/5/16.
 */
public class KcardsApp extends Application {

    private static KcardsApp kcardsApp;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        kcardsApp = this;
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public static void logAnalyticsEvent(String name, Bundle params) {
        kcardsApp.firebaseAnalytics.logEvent(name, params);
    }

}
