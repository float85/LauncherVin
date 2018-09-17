package com.launcher.vin_group.view.screen.box_setting;

import android.text.TextUtils;

import com.launcher.vin_group.App;
import com.launcher.vin_group.model.api.ApiClientImp;
import com.launcher.vin_group.model.db.Db;
import com.launcher.vin_group.model.entity.LauncherSetting;
import com.launcher.vin_group.util.DeviceUtil;
import com.launcher.vin_group.util.NetUtil;
import com.launcher.vin_group.util.OnMainThread;

import java.io.File;
import java.io.FileDescriptor;

import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;


public class BoxSetting_Presenter {
    IBoxSetting_View view;
    CompositeSubscription subscription = new CompositeSubscription();

    public BoxSetting_Presenter(IBoxSetting_View view) {
        this.view = view;
    }

    public void checkVersion() {
        String macEth = NetUtil.getMacEth();
        String mac = NetUtil.getMac();

        view.showMessage("Bắt đầu kiểm tra phiên bản");

        //TODO: refactor
        subscription.add(
                ApiClientImp.getInstance().getLauncherSetting(mac, macEth, DeviceUtil.currentVersion())
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
                                    view.onDeviceUpToDate();
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

    public void reset() {
        Observable.create(
                subscriber -> {
                    try {
                        Db.getDb().videoDAO().clearAll();
                        File folder = new File(App.getInstance().getStoragePath());
                        if (folder.exists()) {
                            for (File file : folder.listFiles()) {
                                if (file.exists())
                                    file.delete();
                            }
                        }

                        subscriber.onNext("");
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                })
                .compose(new OnMainThread<>())
                .subscribe(v -> {
                    view.onDeviceReset();
                }, e -> {
                    view.showMessage("Khôi phục cài đặt gốc thất bại. Vui lòng thử lại");
                });
    }

    public void destroy() {
        subscription.unsubscribe();
    }
}
