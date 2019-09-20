package com.akrivonos.app_standart_java.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.akrivonos.app_standart_java.models.PhotoInfo;

import java.util.List;

@Dao
public interface FavoritePhotoDao{

    @Query("SELECT * FROM favoritephoto WHERE user = (:user)")
    List<PhotoInfo> getFavoritesForUser(String user);

    @Insert
    void setPhotoFavorite(FavoritePhoto favoritePhoto);

    @Delete
    void setPhotoNotFavorite(FavoritePhoto favoritePhoto);

    @Query("SELECT * FROM favoritephoto WHERE url = (:photoUrl) AND user = (:user)")
    List<PhotoInfo> checkIsFavorite(String photoUrl, String user);

}
