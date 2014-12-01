package com.emergency.services.alarm.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesUtil {

    public final static String SETTINGS = "settings";
    public final static String ALARM_INDEX = "alarm_index";
    public final static String VIBRATE = "vibrate";

    public static Editor getEditor(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.edit();
    }
}
