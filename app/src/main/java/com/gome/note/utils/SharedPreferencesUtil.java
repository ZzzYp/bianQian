/**
 *
 */
package com.gome.note.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * @author viston
 */
public class SharedPreferencesUtil {
    public static final String SHARE_FILE_NAME = "shanlin_credit";

    public static final String KEY_FIRST_START_APP = "first_start_app";

    public final static String DELETE_LABEL_DIALOG_NOT_ALERT = "delete_label_dialog_not_alert";
    public final static String DELETE_HOME_DIALOG_NOT_ALERT = "delete_home_dialog_not_alert";
    public final static String DELETE_CREATE_DIALOG_NOT_ALERT = "delete_create_dialog_not_alert";

    /**
     * @param context
     * @param key
     * @param value
     */
    @SuppressLint("CommitPrefEdits")
    public static void saveStringValue(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(SHARE_FILE_NAME, Context.
                MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    @SuppressLint("CommitPrefEdits")
    public static void saveBooelanValue(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(SHARE_FILE_NAME, Context.
                MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    @SuppressLint("CommitPrefEdits")
    public static void saveIntValue(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(
                SHARE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    @SuppressLint("CommitPrefEdits")
    public static void saveLongValue(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(
                SHARE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * @param context
     * @param key
     * @return
     */
    public static String getStringValue(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(
                SHARE_FILE_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static int getIntValue(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(
                SHARE_FILE_NAME, Context.MODE_PRIVATE);
        return settings.getInt(key, 0);
    }

    public static long getLongValue(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(
                SHARE_FILE_NAME, Context.MODE_PRIVATE);
        return settings.getLong(key, 0);
    }


    public static boolean getBooleanValue(Context context, String key, boolean defaultValue) {
        return context.getSharedPreferences(SHARE_FILE_NAME, Context.MODE_PRIVATE).getBoolean
                (key, defaultValue);
    }

    public static boolean getBooleanValue(Context context, String key) {
        return getBooleanValue(context, key, false);
    }

    /**
     * @param context
     * @param keys
     */
    @SuppressLint("CommitPrefEdits")
    public static void cleanStringValue(Context context, String... keys) {
        for (String key : keys) {
            SharedPreferences settings = context.getSharedPreferences(
                    SHARE_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            if (settings.contains(key)) {
                editor.remove(key).commit();
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    public static void cleanLongValue(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(
                SHARE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void clear(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                SHARE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }
}
