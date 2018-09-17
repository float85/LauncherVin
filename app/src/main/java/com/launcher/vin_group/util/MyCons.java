package com.launcher.vin_group.util;

import android.os.Environment;



public class MyCons {
    public static final String LOG = "katana";
    public static final String MP4 = ".mp4";
    public static final String JPG = ".jpg";
    public static final String PNG = ".png";
    public static final String GIF = ".gif";
    public static final String M3U8 = ".m3u8";
    public static final int LIMIT_SPEED = 500000;
    public static final int INTERVAL_DOWNLOAD_PROGRESS = 1000;
    public static final int DOWNLOAD_RETRY_TIMES = 100;

    public static final String APP_PATH = Environment.getExternalStorageDirectory() + "/Adtruevn";
    public static final String APK_LAUNCHER_PATH = APP_PATH + "/vin.apk";
}
