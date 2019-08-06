package com.akrivonos.app_standart_java.database;

import com.akrivonos.app_standart_java.models.PhotoInfo;

import java.util.ArrayList;

public interface DatabaseControlListener {
    void setPhotoFavorite(PhotoInfo photoInfo);

    void setPhotoNotFavorite(PhotoInfo photoInfo);

    boolean checkIsFavorite(String photoUrl);

    ArrayList<ArrayList<PhotoInfo>> getAllFavoritesForUser(String userName);

    ArrayList<ArrayList<PhotoInfo>> getHistoryConvention(String userName);

    void addToHistoryConvention(PhotoInfo photoInfo);
}

