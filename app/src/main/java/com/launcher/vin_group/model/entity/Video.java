package com.launcher.vin_group.model.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.launcher.vin_group.model.db.db_entity.DbVideo;
import com.launcher.vin_group.model.entity.enums.PLAY_TYPE;
import com.launcher.vin_group.view.screen.video_download.Video_ViewModel;

public class Video implements Cloneable {
    @Expose
    @SerializedName("id")
    public int id;

    @Expose
    @SerializedName("type")
    public PLAY_TYPE type;

    @Expose
    @SerializedName("video_url")
    public String url;

    @Expose
    @SerializedName("livestreaming")
    public String livestreaming;

    @Expose
    @SerializedName("schedule")
    public String schedule;

    @Expose
    @SerializedName("start")
    public long start;

    @Expose
    @SerializedName("duration")
    public long duration;

    @Expose
    @SerializedName("filesize")
    public float filesize;

    @Expose
    @SerializedName("created")
    public long created;

    @Expose
    @SerializedName("modified")
    public long modified;

    @Expose
    @SerializedName("description")
    public String description;

    @Expose
    @SerializedName("sortorder")
    public int sortorder;

    public String fileName;
    public String format;

    public int countRetry;

    public Video() {
    }

    //Ex: video.mp4
    public String getFullNameFormat() {
        return fileName + format;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Video_ViewModel)
            return ((Video_ViewModel) obj).id == id;
        else if (obj instanceof Video) {
            return ((Video) obj).id == id;
        } else if (obj instanceof DbVideo) {
            return ((DbVideo) obj).id == id;
        } else return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public Video clone() {
        try {
            return (Video) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
