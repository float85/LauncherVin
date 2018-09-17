package com.launcher.vin_group.view.screen.main;

import android.text.TextUtils;

import com.launcher.vin_group.model.api.ApiClientImp;
import com.launcher.vin_group.model.db.Db;
import com.launcher.vin_group.model.db.db_entity.DbVideo;
import com.launcher.vin_group.model.entity.LauncherSetting;
import com.launcher.vin_group.util.DeviceUtil;
import com.launcher.vin_group.util.LogUtils;
import com.launcher.vin_group.util.NetUtil;
import com.launcher.vin_group.util.OnMainThread;

import java.util.List;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;



public class Main_Presenter {
    IMain view;
    CompositeSubscription subscription = new CompositeSubscription();

    public Main_Presenter(IMain view) {
        this.view = view;
    }

    public void check() {
        subscription.add(
                Observable
                        .create((Observable.OnSubscribe<Boolean>) subscriber -> {
                            List<DbVideo> list = Db.getDb().videoDAO().getVideos();
                            boolean exists = list != null && list.size() > 0;
                            subscriber.onNext(exists);
                            subscriber.onCompleted();
                        })
                        .compose(new OnMainThread<>())
                        .subscribe(hasData -> {
                            if (hasData)
                                view.showPlayerScreen();
                            else view.showDownloadScreen();
                        }, throwable -> {
                            view.showMessage(throwable.getMessage());
                        })
        );
    }

    public void checkVersion() {
        String macEth = NetUtil.getMacEth();
        String mac = NetUtil.getMac();

        subscription.add(
                ApiClientImp.getInstance().getLauncherSetting(mac, macEth, DeviceUtil.currentVersion())
                        .doOnSubscribe(() -> LogUtils.d("Start checkVersion"))
                        .onErrorReturn(throwable -> {
                            LauncherSetting launcherSetting = new LauncherSetting();
                            launcherSetting.versionCode = DeviceUtil.currentVersion();
                            launcherSetting.result = true;
                            return launcherSetting;
                        })
                        .compose(new OnMainThread<>())
                        .subscribe(launcherSetting -> {
                            if (launcherSetting.result) {
                                if (DeviceUtil.isUpToDate(launcherSetting.versionCode))
                                    check();
                                else {
                                    if (!TextUtils.isEmpty(launcherSetting.apkUrl)) {
                                        view.showUpdateScreen(launcherSetting.apkUrl);
                                    } else view.showMessage("Link tải không khả dụng");
                                }
                            } else {
                                view.showMessage(String.format("Thiết bị chưa được kích hoạt %s %s", mac, macEth));
                            }
                        }, e -> view.showMessage(e.getMessage()))
        );
    }

    public void checkVersionButton() {
        String macEth = NetUtil.getMacEth();
        String mac = NetUtil.getMac();

        subscription.add(
                ApiClientImp.getInstance().getLauncherSetting(mac, macEth, DeviceUtil.currentVersion())
                        .onErrorReturn(throwable -> {
                            LauncherSetting launcherSetting = new LauncherSetting();
                            launcherSetting.versionCode = DeviceUtil.currentVersion();
                            return launcherSetting;
                        })
                        .compose(new OnMainThread<>())
                        .subscribe(launcherSetting -> {
                            if (DeviceUtil.isUpToDate(launcherSetting.versionCode)) {
                                view.showMessage("Version Mới Nhất");
                            } else
                                view.showUpdateScreen(launcherSetting.apkUrl);
                        }, e -> view.showMessage(e.getMessage()))
        );
    }

    public void destroy() {
        subscription.unsubscribe();
    }
}
