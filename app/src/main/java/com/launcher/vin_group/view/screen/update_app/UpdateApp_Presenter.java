package com.launcher.vin_group.view.screen.update_app;

import com.launcher.vin_group.BuildConfig;
import com.launcher.vin_group.util.DeviceUtil;
import com.launcher.vin_group.util.MyCons;
import com.launcher.vin_group.util.NetUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;



public class UpdateApp_Presenter {
    UpdateApp_ViewModel viewModel;
    IUpdateApp_View view;

    BaseDownloadTask downloadTask;

    public UpdateApp_Presenter(UpdateApp_ViewModel viewModel, IUpdateApp_View view) {
        this.viewModel = viewModel;
        this.view = view;
    }

    public void loadInfo() {
        int versionCode = DeviceUtil.currentVersion();
        String versionName = BuildConfig.VERSION_NAME;
        String mac = NetUtil.getMac();
        String macEth = NetUtil.getMacEth();

        String info = String.format("Code: %d, Version: %s, %s - %s ", versionCode, versionName, mac, macEth);
        view.showAppInfo(info);
    }

    public void doUpdateClick() {
        downloadFile();
    }

    void downloadFile() {
        view.showMessage("Bắt đầu tải ...");
        viewModel.showLoading(true);

        FileDownloader.getImpl().create(viewModel.apkUrl)
                .setPath(MyCons.APK_LAUNCHER_PATH, false)
                .setCallbackProgressTimes(700)
                .setMinIntervalUpdateSpeed(1000)
                .setAutoRetryTimes(100)
                .setForceReDownload(true)
                .setListener(finishListener)
                .start();
    }

    FileDownloadLargeFileListener finishListener = new FileDownloadLargeFileListener() {

        @Override
        protected void completed(final BaseDownloadTask task) {
            viewModel.showLoading(false);
            view.onDownloadCompleted();
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            viewModel.showLoading(false);
            view.showMessage("Quá trình cập nhật xảy ra sự cố.");
            view.onDownloadFailed();
        }

        @Override
        protected void warn(BaseDownloadTask task) {
        }

        @Override
        protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
        }

        @Override
        protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            if (totalBytes > 0) {
                int progress = Long.valueOf(soFarBytes * 100 / totalBytes).intValue();
                viewModel.progress.set(progress);
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
        }
    };

    public void destroy() {

    }
}
