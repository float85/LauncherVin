package com.launcher.vin_group.model.db.db_entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.launcher.vin_group.model.entity.Video;


@Entity
public class DbVideo {
    @PrimaryKey
    public int id;

    @ColumnInfo
    public int type;

    @ColumnInfo
    public String url;

    @ColumnInfo
    public String livestreaming;

    @ColumnInfo
    public String description;

    @ColumnInfo
    public String schedule;

    @ColumnInfo
    public long start;

    @ColumnInfo
    public long duration;

    @ColumnInfo
    public float filesize;

    @ColumnInfo
    public long created;

    @ColumnInfo
    public long modified;

    @ColumnInfo
    public String fileName;

    @ColumnInfo
    public String format;

    @ColumnInfo
    public String localPath;

    @ColumnInfo
    public int sortorder;

    public DbVideo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public float getFilesize() {
        return filesize;
    }

    public void setFilesize(float filesize) {
        this.filesize = filesize;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DbVideo) {
            return ((DbVideo) obj).id == id;
        } else if (obj instanceof Video) {
            return ((Video) obj).id == id;
        }
        return super.equals(obj);
    }

    public boolean isDiff(DbVideo old) {
        return !old.url.equals(url) || old.modified != modified;
    }
}
