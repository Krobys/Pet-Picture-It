package com.akrivonos.app_standart_java.room.converters;

import android.net.Uri;

import androidx.room.TypeConverter;

public class UriConverter {

    @TypeConverter
    public String uriToString(Uri uri){
        return String.valueOf(uri);
    }
}
