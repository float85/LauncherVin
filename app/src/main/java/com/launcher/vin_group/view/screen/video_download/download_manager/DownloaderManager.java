package com.launcher.vin_group.view.screen.video_download.download_manager;

import android.os.Environment;

import com.launcher.vin_group.App;
import com.launcher.vin_group.model.EntityMapper;
import com.launcher.vin_group.model.db.Db;
import com.launcher.vin_group.model.db.db_entity.DbVideo;
import com.launcher.vin_group.model.entity.Video;
import com.launcher.vin_group.model.entity.enums.PLAY_TYPE;
import com.launcher.vin_group.util.Bus;
import com.launcher.vin_group.util.LogUtils;
import com.launcher.vin_group.util.MyCons;
import com.launcher.vin_group.view.eventbus.ELoadFile;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.ArrayList;
import java.util.List;



public class DownloaderManager {

    FileDownloadQueueSet queueSet;

    public DownloaderManager() {
        configDownloadManager();
    }

    private void configDownloadManager() {
        queueSet = new FileDownloadQueueSet(new FileDownloadLargeFileListener() {
            @Override
            protected void completed(final BaseDownloadTask task) {
                final Video video = (Video) task.getTag();
                Bus.getInstance().post(new ELoadFile(video, 100));
                LogUtils.d(String.format("completed %s", task.getFilename()));

                //TODO:
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
                Bus.getInstance().post(new ELoadFile((Video) task.getTag(), -1));
                LogUtils.e(String.format("%s: %s", task.getFilename(), e.getMessage()));
                task.reuse();
                task.start();
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                try {
                    task.reuse();
                    task.start();
                } catch (Exception e) {
                    LogUtils.e(e.getMessage());
                }
            }

            @Override
            protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                if (totalBytes > 0)
                    LogUtils.d(String.format("pending %s: %d", task.getFilename(), soFarBytes * 100 / totalBytes));
            }

            @Override
            protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                if (totalBytes > 0) {
                    int progress = Long.valueOf(soFarBytes * 100 / totalBytes).intValue();
                    Bus.getInstance().post(new ELoadFile((Video) task.getTag(), progress));
                    LogUtils.d(String.format("progress %s: %d", task.getFilename(), soFarBytes * 100 / totalBytes));
                }
            }

            @Override
            protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                if (totalBytes > 0){
                    LogUtils.d(String.format("pause %s: %d", task.getFilename(), soFarBytes * 100 / totalBytes));
                    try {
                        task.reuse();
                        task.start();
                    } catch (Exception e) {
                        LogUtils.e(e.getMessage());
                    }
                }
            }
        });
    }

    public void download(List<Video> videos) {
        final List<BaseDownloadTask> tasks = new ArrayList<>();
        for (Video video : videos) {
            if (video.type == PLAY_TYPE.VIDEO || video.type == PLAY_TYPE.IMAGE) {
                BaseDownloadTask task = FileDownloader.getImpl().create(video.url);

                String path = App.getInstance().getStoragePath() + "/" + video.getFullNameFormat();
                task.setTag(video);
                task.setPath(path);
                tasks.add(task);
            }
        }
        queueSet.setForceReDownload(true);
        queueSet.setCallbackProgressMinInterval(MyCons.INTERVAL_DOWNLOAD_PROGRESS);
        queueSet.setAutoRetryTimes(MyCons.DOWNLOAD_RETRY_TIMES);
        queueSet.downloadSequentially(tasks);
        queueSet.start();
    }
}
