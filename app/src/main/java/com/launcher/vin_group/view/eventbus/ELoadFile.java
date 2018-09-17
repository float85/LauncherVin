package com.launcher.vin_group.view.eventbus;

import com.launcher.vin_group.model.entity.Video;



public class ELoadFile {
    public Video video;
    public int progress;

    public ELoadFile(Video video, int progress) {
        this.video = video;
        this.progress = progress;
    }
}
