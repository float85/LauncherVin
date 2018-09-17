package com.launcher.vin_group.util;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.launcher.vin_group.App;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



public class NetUtil {
    public static String getMacEth() {
        try {
            return loadFileAsString("/sys/class/net/eth0/address")
                    .toUpperCase().substring(0, 17);
        } catch (IOException e) {
            return "";
        }
    }

    public static String loadFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    public static String getMac() {
        try {
            return getWifiManager().getConnectionInfo().getBSSID();
        } catch (Exception e) {
            return "";
        }
    }

    public static WifiManager getWifiManager() {
        return (WifiManager) App.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }
}
