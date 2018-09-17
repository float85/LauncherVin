package com.launcher.vin_group.view.screen.main;

import com.launcher.vin_group.model.entity.Video;

import java.util.List;


public interface ParentRemote {
    void startIntervalService();
    void performDownload(List<Video> videos);
    void showPlayerScreen();
}
