package com.launcher.vin_group.util;

import android.util.Log;

import com.bosphere.filelogger.FL;
import com.launcher.vin_group.BuildConfig;



public class LogUtils {

    /**
     * Log in debug mode.
     *
     * @param message
     */
    public static void d(String message) {
        try {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            String log = String.format("%s, %s, %d: %s",
                    stackTraceElement.getFileName(),
                    stackTraceElement.getMethodName(),
                    stackTraceElement.getLineNumber(),
                    message);
            if (DeviceUtil.isDev())
                FL.d(log);
            else Log.d(MyCons.LOG, log);
        } catch (Exception e) {
            Log.e(MyCons.LOG, e.getMessage());
        }
    }

    /**
     * Log in debug mode.
     *
     * @param message
     */
    public static void w(String message) {
        FL.w(message);
    }

    /**
     * Log in info mode.
     *
     * @param message
     */
    public static void i(String message) {
        try {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            if (BuildConfig.DEBUG)
                Log.i(stackTraceElement.getFileName() + " in "
                        + stackTraceElement.getMethodName() + " at line: "
                        + stackTraceElement.getLineNumber(), message);
        } catch (Exception e) {
            Log.e(MyCons.LOG, e.getMessage());
        }
    }

    /**
     * Log in error mode.
     *
     * @param message
     */
    public static void e(String message) {
        try {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            String log = String.format("%s, %s, %d: %s",
                    stackTraceElement.getFileName(),
                    stackTraceElement.getMethodName(),
                    stackTraceElement.getLineNumber(),
                    message);
            if (DeviceUtil.isDev())
                FL.e(log);
            else Log.e(MyCons.LOG, log);
        } catch (Exception e) {
            Log.e(MyCons.LOG, e.getMessage());
        }
    }
}
