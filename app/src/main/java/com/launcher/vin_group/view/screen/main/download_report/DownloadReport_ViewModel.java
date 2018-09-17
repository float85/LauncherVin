package com.launcher.vin_group.view.screen.main.download_report;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;



public class DownloadReport_ViewModel {
    public ObservableInt progress = new ObservableInt();
    public ObservableField<String> textProgress = new ObservableField<>();
    public ObservableBoolean show = new ObservableBoolean();

    public DownloadReport_ViewModel() {

    }

    public void setDownloadProgress(int downloadPercent) {
        show.set(downloadPercent > 0 && downloadPercent < 100);

        progress.set(downloadPercent);
        textProgress.set(downloadPercent + "%");
    }
}
