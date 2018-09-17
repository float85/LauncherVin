package com.launcher.vin_group.model.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class  LauncherSetting {
    @Expose
    @SerializedName("filename")
    public String apkUrl;

    @Expose
    @SerializedName("version_code")
    public int versionCode;

    @Expose
    @SerializedName("version_name")
    public String versionName;

    @Expose
    @SerializedName("created")
    public long created;

    @Expose
    @SerializedName("updated")
    public long updated;

    @Expose
    @SerializedName("result")
    public boolean result;
}
