package com.launcher.vin_group.model.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class PlayList {
    @Expose
    @SerializedName("id")
    public int id;

    @Expose
    @SerializedName("description")
    public String description;

    @Expose
    @SerializedName("created")
    public long created;

    @Expose
    @SerializedName("modified")
    public long modified;

    @Expose
    @SerializedName("playlist_type")
    public long playlistType;

    @Expose
    @SerializedName("playlist")
    public List<Video> playlist;

    public PlayList() {
    }
}
