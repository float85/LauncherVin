package com.launcher.vin_group.model.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.launcher.vin_group.model.db.db_entity.DbVideo;

import java.util.List;

@Dao
public interface VideoDAO {
    @Query("SELECT * FROM DbVideo order by sortorder asc")
    List<DbVideo> getVideos();

    @Query("SELECT * FROM DbVideo where type = 2 limit 1")
    DbVideo getLiveStreamVideo();

    @Query("SELECT * FROM DbVideo where type !=2 and (localPath is null Or localPath = '') limit 2 ")
    List<DbVideo> getVideosNoLocalPath();

    @Insert
    void insert(DbVideo dbVideo);

    @Update
    void update(DbVideo dbVideo);

    @Delete
    void delete(DbVideo dbVideo);

    @Delete
    void deleteAll(List<DbVideo> dbVideos);

    @Query("delete from DbVideo where id > 0")
    void clearAll();
}
