package com.launcher.vin_group.view.screen.video_player;


import android.os.Environment;

import com.launcher.vin_group.model.entity.Video;
import com.launcher.vin_group.model.entity.enums.PLAY_TYPE;

public class ItemPlay {

    public int id;
    public String localPathVideo;
    String path = Environment.getExternalStorageDirectory() + "/Adtruevn";
    public String url;
    public PLAY_TYPE type;
    long duration;
    String description;

    public ItemPlay(Video video) {
        this.id = video.id;
        this.type = video.type;
        if (video.type == PLAY_TYPE.VIDEO)
            this.url = video.url;
        else if (video.type == PLAY_TYPE.LIVE_STREAM) {
            this.url = video.livestreaming;
        }
        this.duration = video.duration;
        this.description = video.description;
        localPathVideo = path + "/" + video.fileName + video.format;
    }
}
