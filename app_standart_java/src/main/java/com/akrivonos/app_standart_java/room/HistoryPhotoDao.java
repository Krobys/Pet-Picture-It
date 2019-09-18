package com.akrivonos.app_standart_java.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface HistoryPhotoDao {

    @Query("SELECT * FROM historyphoto WHERE user = :userName")
    List<HistoryPhoto> getHistoryConvention(String userName);

    @Insert
    void addToHistoryConvention(HistoryPhoto historyPhoto);

    @Query("DELETE FROM historyphoto WHERE id NOT IN (SELECT id from historyphoto ORDER BY id DESC LIMIT 20)")
    void deleteOverLimitHistory();
}
