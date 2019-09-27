package com.akrivonos.app_standart_java.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.akrivonos.app_standart_java.constants.Values.CURRENT_USER_NAME;
import static com.akrivonos.app_standart_java.constants.Values.SCHEDULED_TASK_ID;
import static com.akrivonos.app_standart_java.constants.Values.SEARCH_FIELD_TEXT;
import static com.akrivonos.app_standart_java.constants.Values.STATE_MEET_VALUE;
import static com.akrivonos.app_standart_java.fragments.SettingsFragment.ID_RADIOBUTTON_SELECTED_SCHEDULED_SETTINGS;
import static com.akrivonos.app_standart_java.fragments.SettingsFragment.TEXT_REQUEST_SCHEDULED_SETTINGS;

public class PreferenceUtils {

    public static String getCurrentUserName(Context context) { //получение имени текущего пользователя
        if (context != null) {
            String currentUserName;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            currentUserName = sharedPreferences.getString(CURRENT_USER_NAME, "");
            return currentUserName;
        }
        return "default";
    }

    public static void saveCurrentUser(Context context, String currentUserName) { //сохранение состояния поля для ввода
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(CURRENT_USER_NAME, currentUserName).apply();
    }

    public static void saveSearchField(Context context, String textToSave) { //сохранение состояния поля для ввода
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(SEARCH_FIELD_TEXT, textToSave).apply();
    }

    public static String restoreSearchField(Context context) { //востановление состояния поля для ввода
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains(SEARCH_FIELD_TEXT)) {
            return sharedPreferences.getString(SEARCH_FIELD_TEXT, "");
        }
        return "";
    }

    public static void saveStateMeetRequierments(Context context, Boolean state) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(STATE_MEET_VALUE, state).apply();
    }

    public static Boolean getStateMeetRequierments(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(STATE_MEET_VALUE, false);
    }

    public static void setScheduledTaskId(Context context, String idScheduledTask) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(SCHEDULED_TASK_ID, idScheduledTask).apply();
    }

    public static String getScheduledTaskId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(SCHEDULED_TASK_ID, "0");
    }

    public static void setScheduleTaskSettings(Context context, String requestScheduleText, int idRadioButton) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .putString(TEXT_REQUEST_SCHEDULED_SETTINGS, requestScheduleText)
                .putInt(ID_RADIOBUTTON_SELECTED_SCHEDULED_SETTINGS, idRadioButton)
                .apply();
    }

    public static String getScheduleTaskRequestText(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(TEXT_REQUEST_SCHEDULED_SETTINGS, "");
    }

    public static int getScheduleTaskIdRadiobuttonSelected(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(ID_RADIOBUTTON_SELECTED_SCHEDULED_SETTINGS, 1);
    }
}
