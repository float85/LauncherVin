package com.launcher.vin_group.model.api;

import com.launcher.vin_group.model.entity.LCUpdateResult;
import com.launcher.vin_group.model.entity.LauncherSetting;
import com.launcher.vin_group.model.entity.PlayList;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


public interface ApiClient {
    @GET("playlist.json")
    Observable<PlayList> getPlayList(@Query("mac") String mac, @Query("mac_eth") String macEth);

    @GET("launcher.json")
    Observable<LauncherSetting> getLauncherSetting(@Query("mac") String mac, @Query("mac_eth") String macEth, @Query("version_code") int versionCode);

    @POST("launcher.json")
    Observable<LCUpdateResult> postLauncher(@Query("mac") String mac,
                                            @Query("version_code") String vesioncode,
                                            @Query("version_name") String vesionname);
}
