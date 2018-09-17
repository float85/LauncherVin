package com.launcher.vin_group.view.screen.video_player;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import com.launcher.vin_group.App;
import com.launcher.vin_group.BuildConfig;
import com.launcher.vin_group.R;
import com.launcher.vin_group.databinding.FragmentPlayvideoBinding;
import com.launcher.vin_group.model.entity.enums.PLAY_TYPE;
import com.launcher.vin_group.util.DeviceUtil;
import com.launcher.vin_group.util.LogUtils;
import com.launcher.vin_group.util.ServiceUtil;
import com.launcher.vin_group.view.screen.video_player.duration_handler.DurationHandler;
import com.launcher.vin_group.view.screen.video_player.duration_handler.ImageDurationHandler;
import com.launcher.vin_group.view.screen.video_player.duration_handler.VideoDurationHandler;
import com.launcher.vin_group.view.services.LauncherService;

/**
 * Created by tienh on 10/1/2017.
 */

public class VideoPlayerFragment extends Fragment implements IVideoPlayer_View {
    private static int idNow;

    FragmentPlayvideoBinding binding;
    VideoPlayer_Presenter presenter;

    public static VideoPlayerFragment newInstance() {
        return new VideoPlayerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playvideo, container, false);
        presenter = new VideoPlayer_Presenter(this);

        //config media controller
//        if (DeviceUtil.isDev()){
//            MediaController mediaController = new MediaController(getActivity());
//            mediaController.setAnchorView(binding.videoView);
//            binding.videoView.setMediaController(mediaController);
//        }

        binding.videoView.setZOrderOnTop(true);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) presenter.onResume();
        //show video player
        ServiceUtil.startService(getActivity(), LauncherService.class);
    }

    static public int currenGetIDNow() {
        return idNow;
    }

    DurationHandler durationHandler;

    @Override
    public void playVideo(final ItemPlay currentItem, final ItemPlay nextItem) {
        PLAY_TYPE playType = currentItem.type;
        if (durationHandler == null) {
            if (playType == PLAY_TYPE.IMAGE) {
                durationHandler = new ImageDurationHandler(binding.imgView, currentItem.localPathVideo, currentItem.duration);
            } else {
                durationHandler = new VideoDurationHandler(binding.videoView, currentItem, binding.bgBlack);
            }
        }

        if (idNow != currentItem.id) {
            idNow = currentItem.id;
            durationHandler.play();
        } else {
            if (durationHandler.isCompleted(App.isLiveStreamMode)) {
                idNow = nextItem.id;
                durationHandler.stop();
                durationHandler = null;
            } else durationHandler.play();
        }

        if (currentItem.type == PLAY_TYPE.SCROLL_TEXT) {
            binding.tvPlay.setText(currentItem.description);
        }
    }

    @Override
    public void showCurrentVideoInfo(final String text) {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.tvCurrentPlay.setText(text);
                }
            });
    }

    @Override
    public void showPlayList(final String text) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.tvPlayList.setText(text);
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        idNow = 0;
        if (presenter != null)
            presenter.destroy();
        LogUtils.d("onDestroy : idNow =" + idNow);
        super.onDestroy();
    }
}
