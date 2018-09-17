package com.launcher.vin_group.view.screen.video_player;



public interface IVideoPlayer_View {
    void playVideo(ItemPlay video, ItemPlay videoNext);
    void showCurrentVideoInfo(String text);
    void showPlayList(String text);
}
