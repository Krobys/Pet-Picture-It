package com.akrivonos.app_standart_java.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.akrivonos.app_standart_java.models.PhotoGallery;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.akrivonos.app_standart_java.constants.Values.DATE_PHOTO_FIELD;
import static com.akrivonos.app_standart_java.constants.Values.FAVORITE_TABLE;
import static com.akrivonos.app_standart_java.constants.Values.GALLERY_TABLE;
import static com.akrivonos.app_standart_java.constants.Values.HISTORY_TABLE;
import static com.akrivonos.app_standart_java.constants.Values.REQUEST_TEXT_FIELD;
import static com.akrivonos.app_standart_java.constants.Values.URI_PHOTO_FIELD;
import static com.akrivonos.app_standart_java.constants.Values.URL_TEXT_FIELD;
import static com.akrivonos.app_standart_java.constants.Values.USER_NAME_FIELD;


public class DatabaseControl extends SQLiteOpenHelper implements DatabaseControlListener {

    private SQLiteDatabase db;
    private final WeakReference<Context> contextWeakReference;

    public DatabaseControl(Context context) {
        super(context, "app.database", null, 1);
        db = getWritableDatabase();
        String CREATE_TABLE_FAVORITE = "CREATE TABLE IF NOT EXISTS " + FAVORITE_TABLE + "(user TEXT, request TEXT, url TEXT)";
        db.execSQL(CREATE_TABLE_FAVORITE);
        String CREATE_TABLE_HISTORY_CONVENTION = "CREATE TABLE IF NOT EXISTS " + HISTORY_TABLE + "(user TEXT, request TEXT, url TEXT)";
        db.execSQL(CREATE_TABLE_HISTORY_CONVENTION);
        String CREATE_TABLE_GALLERY = "CREATE TABLE IF NOT EXISTS " + GALLERY_TABLE + "(user TEXT, date INTEGER, uri TEXT)";
        db.execSQL(CREATE_TABLE_GALLERY);
        contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void setPhotoFavorite(PhotoInfo photoInfo) { // установить фото в избранные
        String USER_NAME_FIELD = "user";
        String REQUEST_TEXT_FIELD = "request";
        String URL_TEXT_FIELD = "url";

        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_NAME_FIELD, photoInfo.getUserName());
        cv.put(REQUEST_TEXT_FIELD, photoInfo.getRequestText());
        cv.put(URL_TEXT_FIELD, photoInfo.getUrlText());
        db.insert(FAVORITE_TABLE, null, cv);
        db.close();
    }

    @Override
    public void setPhotoNotFavorite(PhotoInfo photoInfo) { // убрать фото из избранных

        db = getWritableDatabase();
        db.execSQL("DELETE FROM " + FAVORITE_TABLE + " WHERE user = ? AND request = ? AND url = ?;", new String[]{photoInfo.getUserName(), photoInfo.getRequestText(), photoInfo.getUrlText()});
        db.close();
    }

    @Override
    public boolean checkIsFavorite(String photoUrl) {
        db = getReadableDatabase();
        long numEntries = DatabaseUtils.queryNumEntries(db, FAVORITE_TABLE, "url = ?", new String[]{photoUrl});
        db.close();
        return numEntries != 0;
    }

    @Override
    public ArrayList<PhotoInfo> getAllFavoritesForUser(String userName) {//получаем список запросов с списком избранных фотографий в каждом по запросам
        db = getReadableDatabase();
        Cursor query = db.rawQuery("SELECT * FROM " + FAVORITE_TABLE + " WHERE user = ? ORDER BY request DESC;", new String[]{userName});
        if (query == null) return null;
        ArrayList<PhotoInfo> photosForTitle = new ArrayList<>();

        while (query.moveToNext()) {
            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.setUserName(query.getString(query.getColumnIndex(USER_NAME_FIELD)));
            photoInfo.setRequestText(query.getString(query.getColumnIndex(REQUEST_TEXT_FIELD)));
            photoInfo.setUrlText(query.getString(query.getColumnIndex(URL_TEXT_FIELD)));

            photosForTitle.add(photoInfo);
        }

        db.close();
        query.close();
        Collections.reverse(photosForTitle);
        return sortBySections(photosForTitle);
    }

    @Override
    public ArrayList<PhotoInfo> getHistoryConvention(String userName) {  //получаем список запросов с списком фотографий из истории в каждом по запросам
        ArrayList<PhotoInfo> photosHistory = new ArrayList<>();
        db = getReadableDatabase();
        Cursor query = db.rawQuery("SELECT * FROM " + HISTORY_TABLE + " WHERE user = ? ORDER BY ? DESC;", new String[]{userName, userName});
        if (query == null) return null;
        while (query.moveToNext()) {
            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.setUserName(query.getString(query.getColumnIndex(USER_NAME_FIELD)));
            photoInfo.setRequestText(query.getString(query.getColumnIndex(REQUEST_TEXT_FIELD)));
            photoInfo.setUrlText(query.getString(query.getColumnIndex(URL_TEXT_FIELD)));

            photosHistory.add(photoInfo);
        }
        Collections.reverse(photosHistory);
        db.close();
        query.close();
        return photosHistory;
    }

    @Override
    public void addToHistoryConvention(PhotoInfo photoInfo) { //добавить в историю

        db = getWritableDatabase();
        Cursor query = db.rawQuery("SELECT * FROM " + HISTORY_TABLE + " WHERE user = ? ORDER BY request DESC;", new String[]{photoInfo.getUserName()});
        if (query == null) return;
        if (query.getCount() >= 20) {
            query.moveToFirst();
            PhotoInfo photoInfoForDelete = new PhotoInfo();
            photoInfoForDelete.setUserName(query.getString(query.getColumnIndex(USER_NAME_FIELD)));
            photoInfoForDelete.setRequestText(query.getString(query.getColumnIndex(REQUEST_TEXT_FIELD)));
            photoInfoForDelete.setUrlText(query.getString(query.getColumnIndex(URL_TEXT_FIELD)));
            db.execSQL("DELETE FROM " + HISTORY_TABLE + " WHERE user = ? AND request = ? AND url = ?;", new String[]{photoInfoForDelete.getUserName(), photoInfoForDelete.getRequestText(), photoInfoForDelete.getUrlText()});
        }
        query.close();

        ContentValues cv = new ContentValues();
        cv.put(USER_NAME_FIELD, photoInfo.getUserName());
        cv.put(REQUEST_TEXT_FIELD, photoInfo.getRequestText());
        cv.put(URL_TEXT_FIELD, photoInfo.getUrlText());
        db.insert(HISTORY_TABLE, null, cv);
        db.close();
    }

    @Override
    public void addToGallery(PhotoGallery photoGallery) {
        String USER_NAME_FIELD = "user";
        String DATE_FIELD = "date";
        String URI_TEXT = "uri";

        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_NAME_FIELD, photoGallery.getUserName());
        cv.put(DATE_FIELD, photoGallery.getDateMillis());
        cv.put(URI_TEXT, photoGallery.getUriPhoto().toString());
        db.insert(GALLERY_TABLE, null, cv);
        db.close();
    }

    @Override
    public void deleteFromGallery(Uri uriPhotoToDelete) {
        db = getWritableDatabase();
        db.execSQL("DELETE FROM " + GALLERY_TABLE + " WHERE uri = ?;", new String[]{uriPhotoToDelete.toString()});
        db.close();
    }

    @Override
    public ArrayList<PhotoGallery> getPhotosFromGallery(String userName) {
        ArrayList<PhotoGallery> photosGallery = new ArrayList<>();
        db = getReadableDatabase();
        Cursor query = db.rawQuery("SELECT * FROM " + GALLERY_TABLE + " WHERE user = ? ORDER BY date DESC;", new String[]{userName});
        if (query == null) return null;
        while (query.moveToNext()) {
            PhotoGallery photoGallery = new PhotoGallery();
            photoGallery.setUserName(query.getString(query.getColumnIndex(USER_NAME_FIELD)));
            photoGallery.setDateMillis(Long.valueOf(query.getString(query.getColumnIndex(DATE_PHOTO_FIELD))));
            photoGallery.setUriPhoto(query.getString(query.getColumnIndex(URI_PHOTO_FIELD)));

            photosGallery.add(photoGallery);
        }
        db.close();
        query.close();
        return photosGallery;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private ArrayList<PhotoInfo> sortBySections(ArrayList<PhotoInfo> photos) { // сортируем фотографии по секциям и добавляем элементы для заглавия
        Map<String, ArrayList<String>> photoMap = new HashMap<>();

        for (PhotoInfo photoInfo : photos) {
            String key = photoInfo.getRequestText();
            String value = photoInfo.getUrlText();

            ArrayList<String> section;
            if (photoMap.containsKey(key)) {
                section = photoMap.get(key);
                section.add(value);
                photoMap.put(key, section);
            } else {
                section = new ArrayList<>();
                section.add(value);
                photoMap.put(key, section);
            }
        }
        return addTitleItemToArray(photoMap);
    }

    private ArrayList<PhotoInfo> addTitleItemToArray(Map<String, ArrayList<String>> photoMap) {//добавление оглавляющего элемента для каждого раздела

        String userName = PreferenceUtils.getCurrentUserName(contextWeakReference.get());//проверка на null делается в методе
        ArrayList<PhotoInfo> photosWithTitle = new ArrayList<>();
        PhotoInfo photoInfo;

        for (String key : photoMap.keySet()) {
            photoInfo = new PhotoInfo();
            photoInfo.setRequestText(key);
            photosWithTitle.add(photoInfo);
            for (String url : photoMap.get(key)) {
                photoInfo = new PhotoInfo();
                photoInfo.setRequestText(key);
                photoInfo.setUrlText(url);
                photoInfo.setUserName(userName);
                photosWithTitle.add(photoInfo);
            }
        }
        return photosWithTitle;
    }
}

