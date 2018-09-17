package com.launcher.vin_group.view.screen.video_player.duration_handler;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.launcher.vin_group.App;
import com.launcher.vin_group.R;
import com.launcher.vin_group.model.entity.enums.PLAY_TYPE;

import java.util.Calendar;



public class ImageDurationHandler implements DurationHandler {

    public long startTime;
    long duration;
    String url;
    ImageView imageView;

    public ImageDurationHandler(ImageView imageView, String url, long duration) {
        this.url = url;
        this.imageView = imageView;
        this.duration = duration == 0 ? 10000 : duration;
    }

    @Override
    public void play() {
        if (imageView != null)
            imageView.setVisibility(View.VISIBLE);

        if (startTime == 0) {
            setStartTime(Calendar.getInstance().getTimeInMillis());
            Glide.with(App.getInstance())
                    .load(url)
                    .dontAnimate()
                    .skipMemoryCache(true)
                    .into(imageView);
        }
    }

    @Override
    public void stop() {
        if (imageView != null)
            imageView.setVisibility(View.GONE);

        this.startTime = 0;
        this.duration = 0;
        this.imageView = null;
        this.url = null;
    }

    @Override
    public boolean isCompleted(boolean isLiveStream) {
        return startTime != 0 && Calendar.getInstance().getTimeInMillis() - (startTime + duration) > 0;
    }

    @Override
    public void setStartTime(long time) {
        this.startTime = time;
    }

    @Override
    public PLAY_TYPE getType() {
        return PLAY_TYPE.IMAGE;
    }
}
