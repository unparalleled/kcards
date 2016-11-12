package com.mrkevinthomas.kcards;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class Utils {
    private Utils() {
    }

    public static String getDateString(long timeStamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp);
        return DateFormat.format("MM/dd/yy", cal).toString();
    }
}