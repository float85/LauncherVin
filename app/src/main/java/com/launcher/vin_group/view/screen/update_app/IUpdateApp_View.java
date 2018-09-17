package com.launcher.vin_group.view.screen.update_app;


public interface IUpdateApp_View {
    void onDownloadCompleted();
    void onDownloadFailed();
    void showAppInfo(String textInfo);
    void showMessage(String message);
}
