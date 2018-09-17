package com.launcher.vin_group.view.screen.video_download;

import com.launcher.vin_group.model.entity.Video;

import java.util.List;



public interface IVideoDownload_View {
    void performDownload(List<Video> videos);
    void showMessage(String message);
}
