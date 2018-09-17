package com.launcher.vin_group.view.eventbus;

import com.launcher.vin_group.model.entity.Video;

public class EPlayVideo {
    public Video video, nextVideo;

    public EPlayVideo(Video video, Video videoNext) {
        this.video = video;
        this.nextVideo = videoNext;
    }
}
