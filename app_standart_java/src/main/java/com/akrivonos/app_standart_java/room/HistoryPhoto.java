package com.akrivonos.app_standart_java.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.akrivonos.app_standart_java.models.PhotoInfo;

@Entity
public class HistoryPhoto extends PhotoInfo {

    HistoryPhoto(){

    }

    @Ignore
    public HistoryPhoto(PhotoInfo photoInfo){
        this.setId(photoInfo.getId());
        this.setRequestText(photoInfo.getRequestText());
        this.setUrlText(photoInfo.getUrlText());
        this.setUserName(photoInfo.getUserName());
    }
}
