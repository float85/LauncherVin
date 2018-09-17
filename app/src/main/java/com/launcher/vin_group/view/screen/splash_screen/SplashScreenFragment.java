package com.launcher.vin_group.view.screen.splash_screen;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.launcher.vin_group.R;
import com.launcher.vin_group.databinding.FragmentSplashBinding;
import com.launcher.vin_group.view.screen.video_download.VideoDownloadFragment;



public class SplashScreenFragment extends Fragment {

    public static SplashScreenFragment newInstance() {
        return new SplashScreenFragment();
    }

    FragmentSplashBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false);


//        new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        getFragmentManager().beginTransaction()
//                                .replace(R.id.container, VideoDownloadFragment.newInstance())
//                                .commit();
//                    }
//                }, 1200);

        return binding.getRoot();
    }
}
