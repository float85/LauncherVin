package com.launcher.vin_group.model.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.launcher.vin_group.model.db.dao.VideoDAO;


@Database(entities = com.launcher.vin_group.model.db.db_entity.DbVideo.class, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract VideoDAO videoDAO();
}
