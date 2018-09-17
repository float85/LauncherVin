package com.launcher.vin_group.util;

import com.launcher.vin_group.BuildConfig;



public class DeviceUtil {
    public static boolean isUpToDate(int latestVersionCode) {
        return currentVersion() == latestVersionCode;
    }

    public static int currentVersion() {
        return BuildConfig.VERSION_CODE;
    }

    public static String currentVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public static boolean isDev() {
        if (!BuildConfig.FLAVOR.equals("vin")) {
            return true;
        }
        return false;
    }
}
