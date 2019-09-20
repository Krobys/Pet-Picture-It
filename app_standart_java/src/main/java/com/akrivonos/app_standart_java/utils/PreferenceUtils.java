package com.akrivonos.app_standart_java.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.akrivonos.app_standart_java.constants.Values.CURRENT_USER_NAME;
import static com.akrivonos.app_standart_java.constants.Values.SEARCH_FIELD_TEXT;

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

}
