package com.akrivonos.app_standart_java.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.akrivonos.app_standart_java.models.PhotoInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.akrivonos.app_standart_java.constants.Values.CURRENT_USER_NAME;
import static com.akrivonos.app_standart_java.constants.Values.FAVORITE_TABLE;
import static com.akrivonos.app_standart_java.constants.Values.HISTORY_TABLE;


public class DatabaseControl extends SQLiteOpenHelper implements DatabaseControlListener {


    private SQLiteDatabase db;
    private Cursor query = null;
    private final WeakReference<Context> contextWeakReference;

    public DatabaseControl(Context context) {
        super(context, "app.database", null, 1);
        db = getWritableDatabase();
        String CREATE_TABLE_FAVORITE = "CREATE TABLE IF NOT EXISTS " + FAVORITE_TABLE + "(user TEXT, request TEXT, url TEXT)";
        db.execSQL(CREATE_TABLE_FAVORITE);
        String CREATE_TABLE_HISTORY_CONVENTION = "CREATE TABLE IF NOT EXISTS " + HISTORY_TABLE + "(user TEXT, request TEXT, url TEXT)";
        db.execSQL(CREATE_TABLE_HISTORY_CONVENTION);
        contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void setPhotoFavorite(PhotoInfo photoInfo) { // установить фото в избранные
        String USER_NAME_FIELD = "user";
        String REQUEST_FIELD_TEXT = "request";
        String URL_TEXT = "url";

        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_NAME_FIELD, photoInfo.getUserName());
        cv.put(REQUEST_FIELD_TEXT, photoInfo.getRequestText());
        cv.put(URL_TEXT, photoInfo.getUrlText());
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
        query.close();
        return numEntries != 0;
    }

    @Override
    public ArrayList<PhotoInfo> getAllFavoritesForUser(String userName) {//получаем список запросов с списком избранных фотографий в каждом по запросам
        db = getReadableDatabase();
        query = db.rawQuery("SELECT * FROM " + FAVORITE_TABLE + " WHERE user = ? ORDER BY request DESC;", new String[]{userName});
        ArrayList<PhotoInfo> photosForTitle = new ArrayList<>();

        while (query.moveToNext()) {
            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.setUserName(query.getString(0));
            photoInfo.setRequestText(query.getString(1));
            photoInfo.setUrlText(query.getString(2));

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
        query = db.rawQuery("SELECT * FROM " + HISTORY_TABLE + " WHERE user = ? ORDER BY ? DESC;", new String[]{userName, userName});
        while (query.moveToNext()) {
            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.setUserName(query.getString(0));
            photoInfo.setRequestText(query.getString(1));
            photoInfo.setUrlText(query.getString(2));

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
        String USER_NAME_FIELD = "user";
        String REQUEST_FIELD_TEXT = "request";
        String URL_TEXT = "url";

        db = getWritableDatabase();
        query = db.rawQuery("SELECT * FROM " + HISTORY_TABLE + " WHERE user = ? ORDER BY request DESC;", new String[]{photoInfo.getUserName()});
        if (query.getCount() >= 20) {
            query.moveToFirst();
            PhotoInfo photoInfoForDelete = new PhotoInfo();
            photoInfoForDelete.setUserName(query.getString(0));
            photoInfoForDelete.setRequestText(query.getString(1));
            photoInfoForDelete.setUrlText(query.getString(2));
            db.execSQL("DELETE FROM " + HISTORY_TABLE + " WHERE user = ? AND request = ? AND url = ?;", new String[]{photoInfoForDelete.getUserName(), photoInfoForDelete.getRequestText(), photoInfoForDelete.getUrlText()});
        }

        ContentValues cv = new ContentValues();
        cv.put(USER_NAME_FIELD, photoInfo.getUserName());
        cv.put(REQUEST_FIELD_TEXT, photoInfo.getRequestText());
        cv.put(URL_TEXT, photoInfo.getUrlText());
        db.insert(HISTORY_TABLE, null, cv);
        db.close();
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
        String userName = getCurrentUserName();
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

    private String getCurrentUserName() {
        if (contextWeakReference.get() != null) {
            String currentUserName;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
            currentUserName = sharedPreferences.getString(CURRENT_USER_NAME, "");
            return currentUserName;
        }
        return "";
    }
}

