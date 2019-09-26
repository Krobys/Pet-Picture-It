package com.akrivonos.app_standart_java.room;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.akrivonos.app_standart_java.models.PhotoGallery;

@Entity
public class GalleryPhoto extends PhotoGallery {

    GalleryPhoto(){

    }

    @Ignore
    public GalleryPhoto(PhotoGallery photoGallery){
        setId(photoGallery.getId());
        setDateMillis(photoGallery.getDateMillis());
        setUriPhoto(photoGallery.getUriPhoto());
        setUserName(photoGallery.getUserName());
    }
}
