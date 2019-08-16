package com.akrivonos.app_standart_java.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoInfo implements Parcelable {
    private String userName = null;
    private String requestText = null;
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

    public void setUserName(String userName) {
        this.userName = userName;
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
