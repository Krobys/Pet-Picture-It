package com.akrivonos.app_standart_java.room.converters;

import android.arch.persistence.room.TypeConverter;
import android.net.Uri;

public class UriConverter {

    @TypeConverter
    public String uriToString(Uri uri){
        return String.valueOf(uri);
    }
}
