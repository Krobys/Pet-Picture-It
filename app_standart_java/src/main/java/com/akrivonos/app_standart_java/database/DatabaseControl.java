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

import static com.akrivonos.app_standart_java.AuthActivity.CURRENT_USER_NAME;

public class DatabaseControl extends SQLiteOpenHelper implements DatabaseControlListener {

    private final String favoriteTable = "pictureTable";
    private final String historyTable = "historyTable";
    private SQLiteDatabase db;
    private Cursor query = null;
    WeakReference<Context> contextWeakReference;


    public DatabaseControl(Context context) {
        super(context, "app.database", null, 1);
        contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_FAVORITE = "CREATE TABLE IF NOT EXISTS " + favoriteTable + "(user TEXT, request TEXT, url TEXT)";
        db.execSQL(CREATE_TABLE_FAVORITE);
        String CREATE_TABLE_HISTORY_CONVENTION = "CREATE TABLE IF NOT EXISTS " + historyTable + "(user TEXT, request TEXT, url TEXT)";
        db.execSQL(CREATE_TABLE_HISTORY_CONVENTION);
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
        db.insert(favoriteTable, null, cv);
        db.close();
    }

    @Override
    public void setPhotoNotFavorite(PhotoInfo photoInfo) { // убрать фото из избранных

        db = getWritableDatabase();
        db.execSQL("DELETE FROM " + favoriteTable + " WHERE user = '" + photoInfo.getUserName() + "' AND request = '" + photoInfo.getRequestText() + "' AND url = '" + photoInfo.getUrlText() + "';");
        db.close();
    }

    @Override
    public boolean checkIsFavorite(String photoUrl) {
        db = getReadableDatabase();
        long numEntries = DatabaseUtils.queryNumEntries(db, favoriteTable, "url = ?", new String[]{photoUrl});
        boolean result = numEntries != 0;
        db.close();
        query.close();
        return result;
    }

    @Override
    public ArrayList<PhotoInfo> getAllFavoritesForUser(String userName) {//получаем список запросов с списком избранных фотографий в каждом по запросам
        db = getReadableDatabase();
        query = db.rawQuery("SELECT * FROM " + favoriteTable + " WHERE user = '" + userName + "' ORDER BY request DESC;", null);
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
        return sortBySections(photosForTitle);
    }

    @Override
    public ArrayList<PhotoInfo> getHistoryConvention(String userName) {  //получаем список запросов с списком фотографий из истории в каждом по запросам
        ArrayList<PhotoInfo> photosHistory = new ArrayList<>();
        db = getReadableDatabase();
        query = db.rawQuery("SELECT * FROM " + historyTable + " WHERE user = ? ORDER BY ? DESC;", new String[]{userName, userName});
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
        query = db.rawQuery("SELECT * FROM " + historyTable + " WHERE user = '" + photoInfo.getUserName() + "' ORDER BY request DESC;", null);
        if (query.getCount() >= 20) {
            query.moveToFirst();
            PhotoInfo photoInfoForDelete = new PhotoInfo();
            photoInfoForDelete.setUserName(query.getString(0));
            photoInfoForDelete.setRequestText(query.getString(1));
            photoInfoForDelete.setUrlText(query.getString(2));
            db.execSQL("DELETE FROM " + historyTable + " WHERE user = '" + photoInfoForDelete.getUserName() + "' AND request = '" + photoInfoForDelete.getRequestText() + "' AND url = '" + photoInfoForDelete.getUrlText() + "';");
        }

        ContentValues cv = new ContentValues();
        cv.put(USER_NAME_FIELD, photoInfo.getUserName());
        cv.put(REQUEST_FIELD_TEXT, photoInfo.getRequestText());
        cv.put(URL_TEXT, photoInfo.getUrlText());
        db.insert(historyTable, null, cv);
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

        ArrayList<PhotoInfo> photosWithTitle = new ArrayList<>();
        PhotoInfo photoInfo;
        String userName = getCurrentUserName();
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

    private String getCurrentUserName() { //получение имени текущего пользователя
        String currentUserName;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
        currentUserName = sharedPreferences.getString(CURRENT_USER_NAME, "");
        return currentUserName;
    }
}

