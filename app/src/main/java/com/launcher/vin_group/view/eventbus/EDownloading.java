package com.launcher.vin_group.view.eventbus;



public class EDownloading {
    public int videoID;
    public int percent;
    public int countRetry;

    public EDownloading(int videoID, int percent, int countRetry) {
        this.videoID = videoID;
        this.percent = percent;
        this.countRetry = countRetry;
    }
}
