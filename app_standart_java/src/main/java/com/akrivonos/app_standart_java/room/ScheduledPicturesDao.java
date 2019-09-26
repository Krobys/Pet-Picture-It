package com.akrivonos.app_standart_java.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScheduledPicturesDao {

    @Query("SELECT * FROM ScheduledPictures WHERE user = :userName")
    List<ScheduledPictures> getSchedulePictures(String userName);

    @Insert
    void addToSheduledTable(ScheduledPictures scheduledPictures);

    @Query("DELETE FROM ScheduledPictures")
    void clearTable();
}
