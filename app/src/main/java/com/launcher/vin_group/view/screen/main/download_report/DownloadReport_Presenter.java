package com.launcher.vin_group.view.screen.main.download_report;

import com.launcher.vin_group.util.Bus;
import com.launcher.vin_group.view.eventbus.EDownloading;
import com.launcher.vin_group.view.screen.video_download.IVideoDownload_View;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;



public class DownloadReport_Presenter {

    DownloadReport_ViewModel viewModel;
    IDownloadReport_View view;

    public DownloadReport_Presenter(DownloadReport_ViewModel viewModel, IDownloadReport_View view) {
        this.viewModel = viewModel;
        this.view = view;
    }

    public void startListening(){
        Bus.getInstance().register(this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(final EDownloading event) {
        viewModel.setDownloadProgress(event.percent);
    }

    public void destroy() {
        Bus.getInstance().unRegister(this);
    }
}
