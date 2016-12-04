package com.mrkevinthomas.kcards;

import android.util.Log;

/**
 * Wrapper around system logging to allow for filtering by build type.
 */
public class Logger {

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg, t);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg, t);
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg, t);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg, t);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg, t);
        }
    }

}
