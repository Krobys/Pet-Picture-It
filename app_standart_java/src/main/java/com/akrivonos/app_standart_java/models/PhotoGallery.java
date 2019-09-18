package com.akrivonos.app_standart_java.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;

@Entity
public class PhotoGallery {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "uri")
    private String uriPhoto;
    @ColumnInfo(name = "user")
    private String userName;
    @ColumnInfo(name = "date")
    private long dateMillis;

    public Uri getUriPhotoU(){
        return Uri.parse(uriPhoto);
    }

    public String getUriPhoto() {
        return uriPhoto;
    }

    public void setUriPhoto(String uriPhoto) {
        this.uriPhoto = uriPhoto;
    }

    public void setUriPhoto(Uri uriPhoto) {
        this.uriPhoto = String.valueOf(uriPhoto);
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDateMillis(long dateMillis) {
        this.dateMillis = dateMillis;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}
