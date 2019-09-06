package com.akrivonos.app_standart_java.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.akrivonos.app_standart_java.constants.Values.CURRENT_USER_NAME;

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
}
