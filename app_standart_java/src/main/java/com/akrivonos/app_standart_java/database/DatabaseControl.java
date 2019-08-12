package com.akrivonos.app_standart_java.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.models.PhotoMap;

import java.util.ArrayList;

public class DatabaseControl extends SQLiteOpenHelper implements DatabaseControlListener {

    private String favoriteTable = "pictureTable";
    private String historyTable = "historyTable";
    private SQLiteDatabase db;
    private Cursor query = null;
    private String CREATE_TABLE_FAVORITE = "CREATE TABLE IF NOT EXISTS " + favoriteTable + "(user TEXT, request TEXT, url TEXT)";
    private String CREATE_TABLE_HISTORY_CONVENTION = "CREATE TABLE IF NOT EXISTS " + historyTable + "(user TEXT, request TEXT, url TEXT)";


    public DatabaseControl(Context context) {
        super(context, "app.database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVORITE);
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
    public PhotoMap getAllFavoritesForUser(String userName) {//получаем список запросов с списком избранных фотографий в каждом по запросам
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
    public PhotoMap getHistoryConvention(String userName) {  //получаем список запросов с списком фотографий из истории в каждом по запросам
        ArrayList<PhotoInfo> photosHistory = new ArrayList<>();
        db = getReadableDatabase();
        query = db.rawQuery("SELECT * FROM " + historyTable + " WHERE user = '" + userName + "' ORDER BY request DESC;", null);
        while (query.moveToNext()) {
            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.setUserName(query.getString(0));
            photoInfo.setRequestText(query.getString(1));
            photoInfo.setUrlText(query.getString(2));

            photosHistory.add(photoInfo);
        }
        db.close();
        query.close();
        return sortBySections(photosHistory);
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
            query.moveToLast();
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

    private PhotoMap sortBySections(ArrayList<PhotoInfo> photos) { // сортируем фотографии по секциям
        PhotoMap photoMap = new PhotoMap();

        for (PhotoInfo photoInfo : photos) {
            photoMap.addToMap(photoInfo.getRequestText(), photoInfo.getUrlText());
        }
        return photoMap;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
