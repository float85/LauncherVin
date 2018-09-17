package com.launcher.vin_group.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;



public class ServiceUtil {
    public static void startService(Context context, Class<?> serviceClass) {
        if (context != null && !isMyServiceRunning(context, serviceClass)) {
            Intent intent = new Intent(context, serviceClass);
            context.startService(intent);
        }
    }

    public static void stopService(Context context, Class<?> serviceClass) {
        if (isMyServiceRunning(context, serviceClass)) {
            Intent intent = new Intent(context, serviceClass);
            context.stopService(intent);
        }
    }

    private static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
