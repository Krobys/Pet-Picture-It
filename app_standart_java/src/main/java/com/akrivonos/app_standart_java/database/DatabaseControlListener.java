package com.akrivonos.app_standart_java.database;

import android.net.Uri;

import com.akrivonos.app_standart_java.models.PhotoGallery;
import com.akrivonos.app_standart_java.models.PhotoInfo;

import java.util.ArrayList;


public interface DatabaseControlListener {
    void setPhotoFavorite(PhotoInfo photoInfo);

    void setPhotoNotFavorite(PhotoInfo photoInfo);

    boolean checkIsFavorite(String photoUrl);

    ArrayList<PhotoInfo> getAllFavoritesForUser(String userName);

    ArrayList<PhotoInfo> getHistoryConvention(String userName);

    void addToHistoryConvention(PhotoInfo photoInfo);

    void addToGallery(PhotoGallery photoGallery);

    void deleteFromGallery(Uri photo);

    ArrayList<PhotoGallery> getPhotosFromGallery(String userName);
}

