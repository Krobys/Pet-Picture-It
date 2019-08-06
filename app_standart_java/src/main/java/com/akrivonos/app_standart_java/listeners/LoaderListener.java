package com.akrivonos.app_standart_java.listeners;

import com.akrivonos.app_standart_java.models.Photo;

import java.util.ArrayList;

public interface LoaderListener {
    void startLoading();

    void finishLoading(ArrayList<Photo> photos);
}
