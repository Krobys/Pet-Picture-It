package com.akrivonos.app_standart_java.models;

import java.util.ArrayList;

public class PostDownloadPicturePack {

    public final static int TYPE_DOWNLOAD_STANDART = 1;
    public final static int TYPE_DOWNLOAD_CHEDULE = 2;
    private ArrayList<PhotoInfo> photos;
    private SettingsLoadPage settingsLoadPage;

    public ArrayList<PhotoInfo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<PhotoInfo> photos) {
        this.photos = photos;
    }


    public SettingsLoadPage getSettingsLoadPage() {
        return settingsLoadPage;
    }

    public void setSettingsLoadPage(SettingsLoadPage settingsLoadPage) {
        this.settingsLoadPage = settingsLoadPage;
    }


}
