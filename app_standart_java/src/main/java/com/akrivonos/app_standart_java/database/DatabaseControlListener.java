package com.akrivonos.app_standart_java.database;

import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.models.PhotoMap;


public interface DatabaseControlListener {
    void setPhotoFavorite(PhotoInfo photoInfo);

    void setPhotoNotFavorite(PhotoInfo photoInfo);

    boolean checkIsFavorite(String photoUrl);

    PhotoMap getAllFavoritesForUser(String userName);

    PhotoMap getHistoryConvention(String userName);

    void addToHistoryConvention(PhotoInfo photoInfo);
}

