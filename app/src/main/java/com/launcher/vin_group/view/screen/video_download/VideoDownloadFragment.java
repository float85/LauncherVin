package com.launcher.vin_group.view.screen.video_download;

import android.app.Activity;
import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.launcher.vin_group.R;
import com.launcher.vin_group.databinding.FragmentVideoDownloadBinding;
import com.launcher.vin_group.model.entity.Video;
import com.launcher.vin_group.util.Bus;
import com.launcher.vin_group.util.ServiceUtil;
import com.launcher.vin_group.util.ViewUtil;
import com.launcher.vin_group.view.eventbus.ELoadFile;
import com.launcher.vin_group.view.screen.main.ParentRemote;
import com.launcher.vin_group.view.screen.video_player.VideoPlayerFragment;
import com.launcher.vin_group.view.services.LauncherService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;



public class VideoDownloadFragment extends Fragment implements IVideoDownload_View {

    public static VideoDownloadFragment newInstance() {
        return new VideoDownloadFragment();
    }

    VideoDownload_Presenter presenter;

    VideoDownload_ViewModel viewModel = new VideoDownload_ViewModel();
    FragmentVideoDownloadBinding binding;
    ParentRemote parentRemote;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentRemote = (ParentRemote) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        Bus.getInstance().register(this);
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_download, container, false);

        presenter = new VideoDownload_Presenter(viewModel, this);
        binding.setViewModel(viewModel);
        presenter.load();

        binding.btnDownload.setOnClickListener(v -> presenter.load());

        loadImageLoading();

        return binding.getRoot();
    }

    private void loadImageLoading() {
        Glide.with(this)
                .load(R.drawable.loading_cat)
                .asGif()
                .skipMemoryCache(true)
                .into(binding.ivLoading);
    }

    @Override
    public void performDownload(List<Video> videos) {
        parentRemote.performDownload(videos);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(ELoadFile event) {
        viewModel.updateItem(event.video, event.progress);

        if (viewModel.isDownloadCompleted()) {
            parentRemote.showPlayerScreen();
        }
    }

    @Override
    public void showMessage(String message) {
        ViewUtil.toast(getActivity(), message);
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        Bus.getInstance().unRegister(this);
        super.onDestroy();
    }
}
