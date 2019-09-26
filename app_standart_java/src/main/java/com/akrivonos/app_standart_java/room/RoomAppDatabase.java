package com.akrivonos.app_standart_java.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.akrivonos.app_standart_java.models.PhotoGallery;
import com.akrivonos.app_standart_java.models.PhotoInfo;

@Database(entities = {ScheduledPictures.class, HistoryPhoto.class, FavoritePhoto.class, GalleryPhoto.class, PhotoGallery.class, PhotoInfo.class}, version = 4, exportSchema = false)
public abstract class RoomAppDatabase extends RoomDatabase {
    public abstract FavoritePhotoDao favoritePhotoDao();
    public abstract HistoryPhotoDao historyPhotoDao();
    public abstract GalleryPhotoDao galleryPhotoDao();

    public abstract ScheduledPicturesDao scheduledPicturesDao();
}
