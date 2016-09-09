package com.mrkevinthomas.kcards;

import android.app.Application;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

public class KcardsApp extends Application {

    private static KcardsApp kcardsApp;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        kcardsApp = this;
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        FlowManager.init(new FlowConfig.Builder(this).build());
    }

    public static void logAnalyticsEvent(String name, Bundle params) {
        kcardsApp.firebaseAnalytics.logEvent(name, params);
    }

}
