package com.akrivonos.app_standart_java.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.net.Uri;

import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.room.converters.UriConverter;

import java.util.List;

@Dao
public interface FavoritePhotoDao{

    @Query("SELECT * FROM favoritephoto WHERE user = (:user)")
    List<PhotoInfo> getFavoritesForUser(String user);

    @Insert
    void setPhotoFavorite(FavoritePhoto favoritePhoto);

    @Query("DELETE FROM FavoritePhoto WHERE url = :photo AND user = :user")
    void setPhotoNotFavorite(String photo, String user);

    @Query("SELECT * FROM favoritephoto WHERE url = (:photoUrl) AND user = (:user)")
    List<PhotoInfo> checkIsFavorite(String photoUrl, String user);

}
