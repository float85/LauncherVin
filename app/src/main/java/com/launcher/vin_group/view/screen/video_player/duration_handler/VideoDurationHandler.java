package com.launcher.vin_group.view.screen.video_player.duration_handler;

import android.net.Uri;
import android.view.View;
import android.widget.VideoView;

import com.launcher.vin_group.model.entity.enums.PLAY_TYPE;
import com.launcher.vin_group.view.screen.video_player.ItemPlay;

import java.io.File;



public class VideoDurationHandler implements DurationHandler {

    VideoView videoView;
    ItemPlay itemPlay;
    View bgBlack;
    boolean isCompleted;

    public VideoDurationHandler(VideoView videoView, ItemPlay itemPlay, View bgBlack) {
        this.videoView = videoView;
        this.itemPlay = itemPlay;
        this.bgBlack = bgBlack;

        if (itemPlay.type == PLAY_TYPE.LIVE_STREAM) {
            this.videoView.setVideoPath(itemPlay.url);
        } else {
            this.videoView.setVideoURI(Uri.fromFile(new File(itemPlay.localPathVideo)));
        }

        this.videoView.setOnCompletionListener(mp -> isCompleted = true);
        this.videoView.setOnErrorListener((mp, what, extra) -> {
            isCompleted = true;
            return true;
        });
    }

    @Override
    public void play() {
        if (!videoView.isPlaying()) {
            videoView.start();
            videoView.setVisibility(View.VISIBLE);
            bgBlack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void stop() {
        if (videoView != null) {
            isCompleted = true;
            videoView.setVisibility(View.GONE);
            bgBlack.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isCompleted(boolean isLiveStream) {
        if (itemPlay.type == PLAY_TYPE.LIVE_STREAM)
            return !isLiveStream;
        else
            return isCompleted;
    }

    @Override
    public void setStartTime(long time) {
    }

    @Override
    public PLAY_TYPE getType() {
        return itemPlay.type;
    }
}
