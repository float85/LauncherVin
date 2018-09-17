package com.launcher.vin_group.model.db;

import android.arch.persistence.room.Room;
import android.content.Context;


public class Db {
    static AppDatabase db;

    public static AppDatabase getDb() {
        return db;
    }

    public static void init(Context context) {
        db = Room.databaseBuilder(context,
                AppDatabase.class, "launcher_videos").build();
    }
}
