package com.launcher.vin_group.view.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.launcher.vin_group.App;
import com.launcher.vin_group.model.EntityMapper;
import com.launcher.vin_group.model.api.ApiClientImp;
import com.launcher.vin_group.model.db.Db;
import com.launcher.vin_group.model.db.db_entity.DbVideo;
import com.launcher.vin_group.model.entity.PlayList;
import com.launcher.vin_group.model.entity.Video;
import com.launcher.vin_group.model.entity.enums.PLAY_TYPE;
import com.launcher.vin_group.util.Bus;
import com.launcher.vin_group.util.LogUtils;
import com.launcher.vin_group.util.MyCons;
import com.launcher.vin_group.util.NetUtil;
import com.launcher.vin_group.util.OnMainThread;
import com.launcher.vin_group.view.eventbus.ERedownloadPlayList;
import com.launcher.vin_group.view.screen.video_player.VideoPlayerFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;



public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d("AlarmReceiver");

        String macEth = NetUtil.getMacEth();
        String mac = NetUtil.getMac();

        ApiClientImp.getInstance().getPlayList(mac, macEth)
                .flatMap(playList -> Observable.just(playList.playlist))
                .flatMap(videos -> Observable.from(videos))
                .doOnNext(video -> {
                    try {
                        if (video.url != null || video.livestreaming != null) {
                            String url = video.url.toLowerCase();

                            //check file extension
                            if (url.contains(MyCons.MP4)) {
                                video.format = MyCons.MP4;
                            } else if (url.contains(MyCons.JPG)) {
                                video.format = MyCons.JPG;
                            } else if (url.contains(MyCons.PNG)) {
                                video.format = MyCons.PNG;
                            } else if (url.contains(MyCons.GIF)) {
                                video.format = MyCons.GIF;
                            }

                            //extract file name
                            if (video.format != null) {
                                int lastIndexOfDot = url.lastIndexOf(".");
                                int lastIndexOfSlash = url.lastIndexOf("/");

                                video.fileName = url.substring(lastIndexOfSlash + 1, lastIndexOfDot);
                            }
                        }
                    } catch (Exception e) {
                        LogUtils.e(e.getMessage());
                    }
                })
                .toList()
                .onErrorReturn(throwable -> new ArrayList<>())
                .doOnNext(videos -> {
                    LogUtils.d("Fresh play list: " + videos.size());
                    if (videos.size() > 0) {
                        updateLocalPlayList(videos);
                    }
                })
                .compose(new OnMainThread<>())
                .subscribe(v -> {
                }, e -> LogUtils.e(e.getMessage()), () -> LogUtils.d("Sync completed"));
    }

    void deleteFile(List<DbVideo> dbVideos) {
        for (DbVideo video : dbVideos) {
            File file = new File(video.localPath);
            if (file.exists())
                file.delete();
        }
    }

    void updateLocalPlayList(List<Video> videos) {
        List<DbVideo> localVideos = Db.getDb().videoDAO().getVideos();

        boolean isLiveStreamMode = checkLiveStreamMode(videos);
        boolean atLeastOneLocalPath = checkAtLeastOneFileHaveLocalPath(localVideos);

        App.isLiveStreamMode = isLiveStreamMode;
        LogUtils.d("LiveStream mode: " + isLiveStreamMode);

        //insert or update item
        for (Video video : videos) {
            try {
                DbVideo nNewVideo = EntityMapper.VideoToDbVideo(video);

                //if local db has data
                if (localVideos != null && !localVideos.isEmpty()) {
                    int index = localVideos.indexOf(video);

                    if (index == -1) {
                        Db.getDb().videoDAO().insert(nNewVideo);
                    } else {
                        DbVideo oldVideo = localVideos.get(index);
                        if (oldVideo.isDiff(nNewVideo)) {
                            Db.getDb().videoDAO().update(nNewVideo);
                        }
                    }
                } else {
                    Db.getDb().videoDAO().insert(nNewVideo);
                }
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
        }

        //if local list have at least one item
        if (!atLeastOneLocalPath) {
            //delete old item doesn't belong to playlist
            for (DbVideo localVideo : localVideos) {
                if (videos.indexOf(localVideo) == -1) {

                    boolean isVideoNotPlaying = localVideo.id != VideoPlayerFragment.currenGetIDNow();
                    if (!isLiveStreamMode && isVideoNotPlaying) {
                        Db.getDb().videoDAO().delete(localVideo);
                        deleteFile(Arrays.asList(localVideo));
                    }
                }
            }
        }
    }

    boolean checkAtLeastOneFileHaveLocalPath(List<DbVideo> dbVideos) {
        int count = 0;
        if (dbVideos != null) {
            for (DbVideo dbVideo : dbVideos) {
                if (dbVideo.localPath != null) {
                    count++;
                }
            }
        }
        return count == 1;
    }

    boolean checkLiveStreamMode(List<Video> videos) {
        return videos.size() == 1
                && videos.get(0).type == PLAY_TYPE.LIVE_STREAM
                && videos.get(0).livestreaming != null;
    }
}
