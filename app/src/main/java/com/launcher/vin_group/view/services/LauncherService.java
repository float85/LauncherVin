package com.launcher.vin_group.view.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.launcher.vin_group.util.DeviceUtil;
import com.launcher.vin_group.util.LogUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class LauncherService extends Service {

    private AlarmManager alarmMgr;
    private PendingIntent checkPlayListIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        alarmMgr = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);

        checkPlayListIntent = getPendingIntentForBroadcast(AlarmReceiver.class);

        Calendar calendar = Calendar.getInstance();

        int min = DeviceUtil.isDev() ? 1 : 5;
//        long interval = TimeUnit.MINUTES.toMillis(min);
        long interval = 30000;
        LogUtils.d("Interval Service time : " + interval);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1000, interval, checkPlayListIntent);
    }

    PendingIntent getPendingIntentForBroadcast(Class clazz) {
        Intent intent = new Intent(getBaseContext(), clazz);
        return PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (alarmMgr != null)
            alarmMgr.cancel(checkPlayListIntent);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
