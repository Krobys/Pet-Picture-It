package com.akrivonos.app_standart_java.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PhotoInfo implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id = 0;
    @ColumnInfo(name = "user")
    private String userName = null;
    @ColumnInfo(name = "request")
    private String requestText = null;
    @ColumnInfo(name = "url")
    private String urlText = null;

    public PhotoInfo() {

    }

    public static final Creator<PhotoInfo> CREATOR = new Creator<PhotoInfo>() {
        @Override
        public PhotoInfo createFromParcel(Parcel in) {
            return new PhotoInfo(in);
        }

        @Override
        public PhotoInfo[] newArray(int size) {
            return new PhotoInfo[size];
        }
    };

    private PhotoInfo(Parcel in) {
        userName = in.readString();
        requestText = in.readString();
        urlText = in.readString();
    }

    public String getUserName() {
        return userName;
    }

    public long getId() {
        return id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getUrlText() {
        return urlText;
    }

    public void setUrlText(String urlText) {
        this.urlText = urlText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(requestText);
        dest.writeString(urlText);
    }
}
