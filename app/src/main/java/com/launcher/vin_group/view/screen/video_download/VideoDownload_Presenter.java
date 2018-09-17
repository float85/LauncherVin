package com.launcher.vin_group.view.screen.video_download;

import com.launcher.vin_group.model.EntityMapper;
import com.launcher.vin_group.model.api.ApiClientImp;
import com.launcher.vin_group.model.db.Db;
import com.launcher.vin_group.model.entity.Video;
import com.launcher.vin_group.util.LogUtils;
import com.launcher.vin_group.util.MyCons;
import com.launcher.vin_group.util.NetUtil;
import com.launcher.vin_group.util.OnMainThread;

import java.util.List;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;



public class VideoDownload_Presenter {

    VideoDownload_ViewModel viewModel;
    IVideoDownload_View view;
    CompositeSubscription subscription = new CompositeSubscription();

    public VideoDownload_Presenter(VideoDownload_ViewModel viewModel, IVideoDownload_View view) {
        this.viewModel = viewModel;
        this.view = view;
    }

    public void load() {
        viewModel.setStatus("Đang tải dữ liệu");

        String macEth = NetUtil.getMacEth();
        String mac = NetUtil.getMac();

        Observable<List<Video>> listObservable =
                ApiClientImp.getInstance().getPlayList(mac, macEth)
                        .doOnSubscribe(() -> viewModel.isLoading.set(true))
                        .flatMap(playList -> Observable.just(playList.playlist))
                        .flatMap(Observable::from)
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

                                        video.fileName = String.format("%s_%d", url.substring(lastIndexOfSlash + 1, lastIndexOfDot), video.id);
                                    }

                                    try {
                                        Db.getDb().videoDAO().insert(EntityMapper.VideoToDbVideo(video));
                                    } catch (Exception e) {
                                        LogUtils.e(e.getMessage());
                                    }
                                }
                            } catch (Exception e) {
                                LogUtils.e(e.getMessage());
                            }
                        })
                        .toList()
                        .compose(new OnMainThread<>());

        subscription.add(
                listObservable
                        .subscribe(videos -> {
                            viewModel.isLoading.set(false);

                            viewModel.setList(videos);
                            view.performDownload(videos);
                            LogUtils.d(videos.size() + "");
                        }, throwable -> {
                            viewModel.isLoading.set(false);
                            LogUtils.e(throwable.getMessage());
                            //TODO: bad retry
                            load();
                        })
        );
    }

    public void onDestroy() {
        subscription.unsubscribe();
    }
}
