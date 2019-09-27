package com.akrivonos.app_standart_java.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Objects;

public class InternetUtils {
    public static boolean isInternetConnectionEnable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return false;
    }
}
