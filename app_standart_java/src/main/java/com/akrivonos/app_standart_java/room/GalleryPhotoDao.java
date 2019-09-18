package com.akrivonos.app_standart_java.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.net.Uri;

import com.akrivonos.app_standart_java.models.PhotoGallery;
import com.akrivonos.app_standart_java.room.converters.UriConverter;

import java.util.List;

@Dao
public interface GalleryPhotoDao {

    @Insert
    void addToGallery(GalleryPhoto galleryPhoto);

    @Query("DELETE FROM GalleryPhoto WHERE uri = :photo")
    void deleteFromGallery(@TypeConverters({UriConverter.class}) Uri photo);

    @Query("SELECT * FROM GalleryPhoto WHERE user = :userName ORDER BY date DESC")
    List<PhotoGallery> getPhotosFromGallery(String userName);

}
