package com.launcher.vin_group.view.screen.video_player.duration_handler;

import com.launcher.vin_group.model.entity.enums.PLAY_TYPE;



public interface DurationHandler {
    void play();

    void stop();

    boolean isCompleted(boolean isLiveStreamMode);

    void setStartTime(long time);

    PLAY_TYPE getType();
}
