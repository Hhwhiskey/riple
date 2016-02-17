package com.khfire22.riple.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Kevin on 1/20/2016.
 */
public class SaveToSharedPrefs {

    // Save booleanTips to shared prefs
    public void saveBooleanPreferences(Context context, String key, Boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.putBoolean("allTipsBoolean", false);
        editor.commit();
    }

    // Save String to shared prefs
    public static void saveStringPreferences(Context context, String key, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // Save all tips to shard prefs
    public void saveAllTipsBoolean(Context context, Boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("allTipsBoolean", value);
        editor.putBoolean("ripleTips", value);
        editor.putBoolean("dropTips", value);
        editor.putBoolean("trickleTips", value);
        editor.putBoolean("friendTips", value);
        editor.putBoolean("postDropTips", value);
        editor.putBoolean("viewUserTips", value);
        editor.putBoolean("viewDropTips", value);
        editor.commit();
    }

    // Save the unread count for the related user
    public static void saveUnreadCount(Context context, String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
