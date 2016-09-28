package com.mrkevinthomas.kcards;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.firebase.auth.FirebaseUser;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

public class ThisApp extends Application {
    private static final String TAG = "kcards";

    private static ThisApp thisApp;

    private FirebaseUser firebaseUser;

    @Override
    public void onCreate() {
        super.onCreate();
        thisApp = this;

        // initialize firebase analytics
        Analytics.init(this);

        // initialize db flow
        FlowManager.init(new FlowConfig.Builder(this).build());
    }

    public static ThisApp get() {
        return thisApp;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public int getAppVersionCode() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's version name from the {@code PackageManager}.
     */
    public String getAppVersionName() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

}
