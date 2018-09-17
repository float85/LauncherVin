package com.launcher.vin_group.view.screen.video_download;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.launcher.vin_group.model.entity.enums.PLAY_TYPE;
import com.launcher.vin_group.model.entity.Video;



public class Video_ViewModel {
    Video video;

    public int id;
    public String name;
    public String format;
    public ObservableInt progress = new ObservableInt(0);
    public ObservableBoolean showIconVideo = new ObservableBoolean();
    public ObservableBoolean showIconImage = new ObservableBoolean();
    public ObservableBoolean showError = new ObservableBoolean();
    public ObservableField<String> textPercent = new ObservableField<>();

    public Video_ViewModel(Video video) {
        this.video = video;

        id = video.id;
        name = video.fileName;
        format = video.format;
        showIconImage.set(video.type == PLAY_TYPE.IMAGE);
        showIconVideo.set(video.type == PLAY_TYPE.VIDEO);

        updateProcess(0);
    }

    public void updateProcess(int progress) {
        if (progress > -1) {
            textPercent.set("- " + progress + "%");
            this.progress.set(progress);
        }

        showError.set(progress == -1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Video_ViewModel)
            return ((Video_ViewModel) obj).id == id;
        else if (obj instanceof Video) {
            return ((Video) obj).id == id;
        } else return false;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
