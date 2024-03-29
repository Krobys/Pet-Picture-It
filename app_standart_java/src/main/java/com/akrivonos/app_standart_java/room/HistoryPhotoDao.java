package com.akrivonos.app_standart_java.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryPhotoDao {

    @Query("SELECT * FROM historyphoto WHERE user = :userName")
    List<HistoryPhoto> getHistoryConvention(String userName);

    @Insert
    void addToHistoryConvention(HistoryPhoto historyPhoto);

    @Query("DELETE FROM historyphoto WHERE id NOT IN (SELECT id from historyphoto ORDER BY id LIMIT 20)")
    void deleteOverLimitHistory();
}
