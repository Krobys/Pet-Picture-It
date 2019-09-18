package com.akrivonos.app_standart_java.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.akrivonos.app_standart_java.models.PhotoGallery;
import com.akrivonos.app_standart_java.models.PhotoInfo;

@Database(entities = {HistoryPhoto.class, FavoritePhoto.class, GalleryPhoto.class, PhotoGallery.class, PhotoInfo.class}, version = 3, exportSchema = false)
public abstract class RoomAppDatabase extends RoomDatabase {
    public abstract FavoritePhotoDao favoritePhotoDao();
    public abstract HistoryPhotoDao historyPhotoDao();
    public abstract GalleryPhotoDao galleryPhotoDao();
}
