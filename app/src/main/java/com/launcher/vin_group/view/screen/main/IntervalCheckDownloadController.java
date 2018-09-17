package com.launcher.vin_group.view.screen.main;

import com.launcher.vin_group.App;
import com.launcher.vin_group.model.EntityMapper;
import com.launcher.vin_group.model.db.Db;
import com.launcher.vin_group.model.db.db_entity.DbVideo;
import com.launcher.vin_group.model.entity.Video;
import com.launcher.vin_group.util.Bus;
import com.launcher.vin_group.util.LogUtils;
import com.launcher.vin_group.util.MyCons;
import com.launcher.vin_group.util.OnMainThread;
import com.launcher.vin_group.view.eventbus.EDownloading;
import com.launcher.vin_group.view.eventbus.ELoadFile;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.DownloadTaskHunter;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Using checking video in local is downloaded or not
 * If local_path = null -> send request download file
 */

public class IntervalCheckDownloadController {
    Observable<Video> obVideoNoLocalPath;
    ArrayList<Video> downloadingList = new ArrayList<>();
    CompositeSubscription subscription = new CompositeSubscription();

    public IntervalCheckDownloadController() {
        obVideoNoLocalPath =
                Observable.interval(2, TimeUnit.SECONDS)
                        .flatMap(aLong -> {
                            List<DbVideo> dbVideos = Db.getDb().videoDAO().getVideosNoLocalPath();
                            if (dbVideos != null) {
                                for (DbVideo dbVideo : dbVideos) {
                                    Video video = EntityMapper.DbVideoToVideo(dbVideo);
                                    if (!downloadingList.contains(video)) {
                                        return Observable.just(video);
                                    }
                                }
                            }
                            return Observable.just(new Video());
                        })
                        .compose(new OnMainThread<Video>());
    }

    public void start() {
        subscription.add(
                obVideoNoLocalPath
                        .subscribe(video -> {
                            if (video.id > 0) {
                                LogUtils.d("Found video no local path: " + video.id);
                                downloadingList.add(video);
                                startDownloadVideo(video);
                            }
                        }, throwable -> LogUtils.e(throwable.getMessage()))
        );
    }

    FileDownloadLargeFileListener finishListener = new FileDownloadLargeFileListener() {

        @Override
        protected void completed(final BaseDownloadTask task) {
            final Video video = (Video) task.getTag();
            postEventDownload(video.id, 100, video.countRetry);
            downloadingList.remove(video);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    DbVideo dbVideo = EntityMapper.VideoToDbVideo(video);
                    dbVideo.localPath = task.getPath();
                    Db.getDb().videoDAO().update(dbVideo);

                    LogUtils.d("ID: " + video.fileName + ": DONE");
                }
            }).start();
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            final Video video = (Video) task.getTag();
            video.countRetry++;
            postEventDownload(video.id, -1, video.countRetry);
            LogUtils.e(String.format("%s: %s . Failed: %d", task.getFilename(), e.getMessage(), video.countRetry));
            //way 1
            FileDownloader.getImpl().clear(task.getId(), task.getTargetFilePath());
            startDownloadVideo(video);

            //way 2
//            task.reuse();
//            task.start();
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            try {
                LogUtils.d("warn: " + task.getUrl());
                task.reuse();
                task.start();
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
        }

        @Override
        protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            try {
                LogUtils.d("pending: " + task.getUrl());
                task.reuse();
                task.start();
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
        }

        @Override
        protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            if (totalBytes > 0) {
                final Video video = (Video) task.getTag();
                int progress = Long.valueOf(soFarBytes * 100 / totalBytes).intValue();
                postEventDownload(video.id, progress, video.countRetry);
                LogUtils.d(String.format("progress %s: %d", task.getFilename(), progress));
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            try {
                LogUtils.d("paused: " + task.getUrl());
                task.reuse();
                task.start();
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
        }
    };

    void postEventDownload(int videoID, int percent, int countRetry) {
        Bus.getInstance().post(new EDownloading(videoID, percent, countRetry));
    }

    private void startDownloadVideo(Video video) {
        String path = App.getInstance().getStoragePath() + "/" + video.getFullNameFormat();

        FileDownloader.getImpl().create(video.url)
                .setPath(path, false)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(MyCons.INTERVAL_DOWNLOAD_PROGRESS)
                .setTag(video)
                .setAutoRetryTimes(MyCons.DOWNLOAD_RETRY_TIMES)
                .setListener(finishListener)
                .start();
    }

    public void destroy() {
        subscription.unsubscribe();
    }
}
