package com.akrivonos.app_standart_java.room;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.akrivonos.app_standart_java.models.PhotoInfo;

@Entity
public class FavoritePhoto extends PhotoInfo {

    FavoritePhoto(){

    }

    @Ignore
    public FavoritePhoto(PhotoInfo photoInfo){
        this.setId(photoInfo.getId());
        this.setRequestText(photoInfo.getRequestText());
        this.setUrlText(photoInfo.getUrlText());
        this.setUserName(photoInfo.getUserName());
    }
}
