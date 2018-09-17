package com.launcher.vin_group.view.screen.main;

import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import com.launcher.vin_group.model.EntityMapper;
import com.launcher.vin_group.model.db.Db;
import com.launcher.vin_group.model.db.db_entity.DbVideo;
import com.launcher.vin_group.model.entity.Video;
import com.launcher.vin_group.model.entity.enums.PLAY_TYPE;
import com.launcher.vin_group.util.Bus;
import com.launcher.vin_group.util.LogUtils;
import com.launcher.vin_group.util.MyCons;
import com.launcher.vin_group.util.OnMainThread;
import com.launcher.vin_group.view.eventbus.EDisplayPlayList;
import com.launcher.vin_group.view.eventbus.EPlayVideo;
import com.launcher.vin_group.view.screen.video_player.VideoPlayerFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
  * Use for broadcast event current video, next video
 */

public class IntervalPlayerController {
    Observable<Pair<Video, Video>> obControlVideoPlaylist;
    static Random random = new Random();
    CompositeSubscription subscription = new CompositeSubscription();

    public IntervalPlayerController() {
        final Observable<Pair<Video, Video>> obChecking = genIntervalChecking();

        obControlVideoPlaylist = Observable.interval(2, TimeUnit.SECONDS)
                .flatMap(aLong -> obChecking.onErrorReturn(throwable -> new Pair<>(null, null)))
                .compose(new OnMainThread<>());
    }

    public void start() {
        subscription.add(
                obControlVideoPlaylist
                        .onErrorReturn(throwable -> new Pair<>(null, null))
                        .subscribe(pair -> {
                            postNextVideoEvent(pair.first, pair.second);
                        }, e -> {
                            LogUtils.e(e.getMessage());
                        })
        );
    }

    Observable<Pair<Video, Video>> genIntervalChecking() {
        return
                Observable
                        .create((Observable.OnSubscribe<DbVideo>) subscriber -> {
                            try {
                                DbVideo liveStreamVideo = Db.getDb().videoDAO().getLiveStreamVideo();
                                List<DbVideo> dbVideos = Db.getDb().videoDAO().getVideos();

                                if (liveStreamVideo != null) {
                                    for (DbVideo dbVideo : dbVideos) {
                                        if (liveStreamVideo.id != dbVideo.id) {
                                            subscriber.onNext(liveStreamVideo);
                                            subscriber.onNext(dbVideo);
                                            break;
                                        }
                                    }
                                } else {
                                    if (dbVideos != null) {
                                        for (DbVideo dbVideo : dbVideos) {
                                            subscriber.onNext(dbVideo);
                                        }
                                    }
                                }
                                subscriber.onCompleted();
                            } catch (Exception e) {
                                subscriber.onError(e);
                            }
                        })
                        .filter(dbVideo -> {
                            PLAY_TYPE playType = PLAY_TYPE.findByValue(dbVideo.type);
                            if (playType == PLAY_TYPE.LIVE_STREAM) {
                                return true;
                            }
                            return dbVideo.localPath != null;
                        })
                        .map(dbVideo -> EntityMapper.DbVideoToVideo(dbVideo))
                        .toList()
                        .doOnNext(videos -> postDisplayPlayList(videos))
                        .map(videos -> {
                            Video current = null;
                            Video next = null;

                            if (VideoPlayerFragment.currenGetIDNow() == 0) {
                                current = getByIndex(videos, 0);
                                next = getByIndex(videos, 1);
                            } else {
                                //find current video
                                boolean found = false;
                                for (int i = 0; i < videos.size(); i++) {
                                    if (videos.get(i).id == VideoPlayerFragment.currenGetIDNow()) {
                                        current = getByIndex(videos, i);
                                        next = getByIndex(videos, i + 1);

                                        if (next == null)
                                            next = getByIndex(videos, 0);

                                        found = true;

                                        break;
                                    }
                                }

                                //incase playing video has been removed from new playlist
                                if (!found) {
                                    current = getByIndex(videos, 0);
                                    next = getByIndex(videos, 1);
                                }
                            }

                            return new Pair<>(current, next);
                        });
    }

    static Video getByIndex(List<Video> list, int index) {
        try {
            return list.get(index);
        } catch (Exception e) {
            Video video = list.get(0).clone();
            video.id = -random.nextInt(10);
            return video;
        }
    }

    void postDisplayPlayList(List<Video> videos) {
        try {
            List<String> strings = Observable.from(videos)
                    .map(video -> String.valueOf(video.id))
                    .toList()
                    .toBlocking()
                    .firstOrDefault(new ArrayList<>());

            String text = TextUtils.join(", ", strings);
            Bus.getInstance().post(new EDisplayPlayList(text));
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    void postNextVideoEvent(Video current, Video next) {
        try {
            if (current != null && next != null) {
                Bus.getInstance().post(new EPlayVideo(current, next));
                Log.d(MyCons.LOG, "Current Video: " + current.id + ", Next: " + next.id);
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    public void destroy() {
        subscription.unsubscribe();
    }
}
