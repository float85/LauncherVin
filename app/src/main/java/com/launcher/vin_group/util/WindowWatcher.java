package com.launcher.vin_group.util;

import android.view.View;
import android.view.Window;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;



public class WindowWatcher {
    Subscription subscription;
    Window window;
    boolean play;

    public WindowWatcher(Window window) {
        this.window = window;
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE;

        subscription = Observable
                .interval(5, TimeUnit.SECONDS)
                .compose(new OnMainThread<>())
                .subscribe(v -> {
                    if (play) {
                        try {
                            window.getDecorView().setSystemUiVisibility(flags);
                        } catch (Exception e) {
                            LogUtils.e(e.getMessage());
                        }
                    }
                }, e -> {

                });
    }

    public void onResume() {
        play = true;
    }

    public void onPause() {
        play = false;
    }

    public void destroy() {
        if (subscription != null)
            subscription.unsubscribe();
    }
}
