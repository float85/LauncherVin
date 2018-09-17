package com.launcher.vin_group.view.screen.update_app;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;


public class UpdateApp_ViewModel {
    public ObservableBoolean isLoading = new ObservableBoolean();
    public ObservableInt progress = new ObservableInt();
    public ObservableField<String> info = new ObservableField<>();
    public String apkUrl;

    public void showLoading(boolean show) {
        isLoading.set(show);
    }
}
