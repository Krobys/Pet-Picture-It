package com.akrivonos.app_standart_java.room;

import android.net.Uri;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

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
