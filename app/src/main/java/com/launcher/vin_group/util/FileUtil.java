package com.launcher.vin_group.util;

import com.launcher.vin_group.App;

import java.io.File;



public class FileUtil {
    public static boolean checkLocalFolder() {
        File folder = new File(App.getInstance().getStoragePath());
        if (folder.exists()) {
            return true;
        } else {
            return folder.mkdir();
        }
    }
}
