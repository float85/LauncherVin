package com.launcher.vin_group.view.screen.main;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.launcher.vin_group.R;
import com.launcher.vin_group.databinding.ActivityMainBinding;
import com.launcher.vin_group.model.entity.Video;
import com.launcher.vin_group.util.Bus;
import com.launcher.vin_group.util.FileUtil;
import com.launcher.vin_group.util.ServiceUtil;
import com.launcher.vin_group.util.ViewUtil;
import com.launcher.vin_group.util.WindowWatcher;
import com.launcher.vin_group.view.eventbus.ECancelUpdate;
import com.launcher.vin_group.view.eventbus.ERedownloadPlayList;
import com.launcher.vin_group.view.screen.box_setting.BoxSettingFragment;
import com.launcher.vin_group.view.screen.main.download_report.DownloadReport_Presenter;
import com.launcher.vin_group.view.screen.main.download_report.DownloadReport_ViewModel;
import com.launcher.vin_group.view.screen.main.download_report.IDownloadReport_View;
import com.launcher.vin_group.view.screen.main.network_report.INetworkReport;
import com.launcher.vin_group.view.screen.main.network_report.NetworkReporter;
import com.launcher.vin_group.view.screen.update_app.UpdateAppFragment;
import com.launcher.vin_group.view.screen.video_download.VideoDownloadFragment;
import com.launcher.vin_group.view.screen.video_download.download_manager.DownloaderManager;
import com.launcher.vin_group.view.screen.video_player.VideoPlayerFragment;
import com.launcher.vin_group.view.services.LauncherService;
import com.liulishuo.filedownloader.FileDownloader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ParentRemote, IMain, INetworkReport, IDownloadReport_View {

    public static Intent createIntent(Context baseContext) {
        return new Intent(baseContext, MainActivity.class);
    }

    DownloaderManager downloaderManager;

    IntervalPlayerController videoIntervalController;
    IntervalCheckDownloadController checkDownloadController;

    Main_Presenter mainPresenter;

    NetworkReporter networkReporter;
    ActivityMainBinding binding;
    WindowWatcher windowWatcher;

    DownloadReport_Presenter downloadReportPresenter;
    DownloadReport_ViewModel downloadReportViewModel = new DownloadReport_ViewModel();
    String apkUrllink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        windowWatcher = new WindowWatcher(getWindow());

        downloaderManager = new DownloaderManager();
        networkReporter = new NetworkReporter(this);

        downloadReportPresenter = new DownloadReport_Presenter(downloadReportViewModel, this);
        binding.setDownloadReportViewModel(downloadReportViewModel);

        if (FileUtil.checkLocalFolder()) {
            start();
        } else showMessage("Không thể tạo thư mục lưu trữ");
    }

    @Override
    protected void onResume() {
        Bus.getInstance().register(this);
        if (windowWatcher != null)
            windowWatcher.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (windowWatcher != null)
            windowWatcher.onPause();
        super.onPause();
    }

    void start() {
        mainPresenter = new Main_Presenter(this);
        mainPresenter.checkVersion();
    }

    @Override
    public void performDownload(List<Video> videos) {
        downloaderManager.download(videos);
    }

    @Override
    public void showDownloadScreen() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, VideoDownloadFragment.newInstance())
                .commit();
    }

    @Override
    public void showPlayerScreen() {
        downloadReportPresenter.startListening();

        startIntervalService();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, VideoPlayerFragment.newInstance())
                .commit();
    }

    @Override
    public void showMessage(String message) {
        ViewUtil.toast(this, message);
    }

    @Override
    public void showUpdateScreen(String apkUrl) {
        this.apkUrllink = apkUrl;
        UpdateAppFragment.newInstance(apkUrl)
                .show(getFragmentManager(), "");
    }

    @Override
    public void startIntervalService() {
        videoIntervalController = new IntervalPlayerController();
        videoIntervalController.start();

        checkDownloadController = new IntervalCheckDownloadController();
        checkDownloadController.start();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(final ECancelUpdate event) {
        mainPresenter.check();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(final ERedownloadPlayList event) {
        ServiceUtil.stopService(this, LauncherService.class);
        startActivity(createIntent(getBaseContext()));
        finish();
    }

    @Override
    public void isConnected(boolean connected) {
        binding.sectionNetwork.setVisibility(connected ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        if (mainPresenter != null)
            mainPresenter.destroy();

        if (networkReporter != null)
            networkReporter.onDestroy();

        if (downloadReportPresenter != null)
            downloadReportPresenter.destroy();

        if (videoIntervalController != null)
            videoIntervalController.destroy();

        if (checkDownloadController != null)
            checkDownloadController.destroy();

        if (windowWatcher != null)
            windowWatcher.destroy();

        FileDownloader.getImpl().clearAllTaskData();

        Bus.getInstance().unRegister(this);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_ENTER:
                    showBoxSetting();
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    showBoxSetting();
                    break;
                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                    showBoxSetting();
                    break;
                default:
                    return super.onKeyDown(keyCode, event);
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    void showBoxSetting() {
        BoxSettingFragment.newInstance().show(getFragmentManager(), "");
    }
}
