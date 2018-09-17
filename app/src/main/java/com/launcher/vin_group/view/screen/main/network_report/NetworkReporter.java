package com.launcher.vin_group.view.screen.main.network_report;

import android.net.NetworkInfo;

import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.github.pwittchen.reactivenetwork.library.internet.observing.strategy.SocketInternetObservingStrategy;
import com.launcher.vin_group.App;
import com.launcher.vin_group.model.api.ApiClientImp;
import com.launcher.vin_group.util.DeviceUtil;
import com.launcher.vin_group.util.OnMainThread;

import rx.subscriptions.CompositeSubscription;



public class NetworkReporter {
    INetworkReport networkReport;
    CompositeSubscription subscription = new CompositeSubscription();

    public NetworkReporter(INetworkReport networkReport) {
        this.networkReport = networkReport;

        //check internet
//        if (DeviceUtil.isDev()) {
//            subscription.add(
//                    ReactiveNetwork.observeNetworkConnectivity(App.getInstance())
//                            .compose(new OnMainThread<>())
//                            .subscribe(connectivity -> networkReport.isConnected(connectivity.getState() == NetworkInfo.State.CONNECTED),
//                                    e -> {
//                                    })
//            );
//        }
    }

    public void onDestroy() {
        subscription.unsubscribe();
    }


}
