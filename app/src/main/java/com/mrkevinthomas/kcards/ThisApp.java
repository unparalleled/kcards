package com.mrkevinthomas.kcards;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class ThisApp extends Application {
    private static ThisApp thisApp;

    private Toast toaster;
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate() {
        super.onCreate();
        thisApp = this;
        toaster = new Toast(this);

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

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showToast(int layoutId) {
        toaster.setView(LayoutInflater.from(getContext()).inflate(layoutId, null));
        toaster.setDuration(Toast.LENGTH_SHORT);
        toaster.show();
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
