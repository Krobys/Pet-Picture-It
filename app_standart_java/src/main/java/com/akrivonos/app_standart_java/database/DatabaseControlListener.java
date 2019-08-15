package com.akrivonos.app_standart_java.database;

import com.akrivonos.app_standart_java.models.PhotoInfo;

import java.util.ArrayList;
import java.util.Map;


public interface DatabaseControlListener {
    void setPhotoFavorite(PhotoInfo photoInfo);

    void setPhotoNotFavorite(PhotoInfo photoInfo);

    boolean checkIsFavorite(String photoUrl);

    Map<String, ArrayList<String>> getAllFavoritesForUser(String userName);

    Map<String, ArrayList<String>> getHistoryConvention(String userName);

    void addToHistoryConvention(PhotoInfo photoInfo);
}

