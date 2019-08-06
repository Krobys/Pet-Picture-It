package com.akrivonos.app_standart_java.models;

import android.util.Log;

public class PhotoInfo {
    private String userName;
    private String requestText;
    private String urlText;

    public PhotoInfo(String userName, String requestText, String urlText) {
        this.userName = userName;
        this.requestText = requestText;
        this.urlText = urlText;
    }

    public PhotoInfo() {

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

    public void showPhotoInfos() {
        Log.d("test", "user: " + userName + " request: " + requestText + " url: " + urlText);
    }
}
