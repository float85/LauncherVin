package com.launcher.vin_group.view.screen.box_setting;


public interface IBoxSetting_View {
    void showUpdateScreen(String urlLink);

    void onDeviceUpToDate();

    void showMessage(String message);

    void onDeviceReset();
}
