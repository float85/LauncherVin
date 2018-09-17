package com.launcher.vin_group.view.screen.video_player;

import com.launcher.vin_group.util.Bus;
import com.launcher.vin_group.view.eventbus.EDisplayPlayList;
import com.launcher.vin_group.view.eventbus.EPlayVideo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by tienh on 10/1/2017.
 */

public class VideoPlayer_Presenter {
    IVideoPlayer_View view;

    public VideoPlayer_Presenter(IVideoPlayer_View view) {
        this.view = view;
    }

    public void onResume(){
        Bus.getInstance().register(this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(EPlayVideo event) {
        ItemPlay videoPlayer = null;
        if (event.video != null) {
            videoPlayer = new ItemPlay(event.video);
        }

        ItemPlay videoPlayerNext = null;
        if (event.nextVideo != null) {
            videoPlayerNext = new ItemPlay(event.nextVideo);
        }
        view.playVideo(videoPlayer, videoPlayerNext);

        //
        String text = String.format("Playing: %d, Next: %d", event.video.id, event.nextVideo.id);
        view.showCurrentVideoInfo(text);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(EDisplayPlayList event) {
        view.showPlayList(event.text);
    }

    public void destroy() {
        Bus.getInstance().unRegister(this);
    }
}
