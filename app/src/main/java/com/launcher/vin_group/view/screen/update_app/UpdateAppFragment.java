package com.launcher.vin_group.view.screen.update_app;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.launcher.vin_group.R;
import com.launcher.vin_group.databinding.UpdateAppFragmentBinding;
import com.launcher.vin_group.util.Bus;
import com.launcher.vin_group.util.MethodUtil;
import com.launcher.vin_group.util.MyCons;
import com.launcher.vin_group.util.ViewUtil;
import com.launcher.vin_group.view.eventbus.ECancelUpdate;
import com.launcher.vin_group.view.screen.main.IMain;
import com.launcher.vin_group.view.screen.main.ParentRemote;

import java.io.File;


public class UpdateAppFragment extends DialogFragment implements IUpdateApp_View {

    static final String APK_URL = "APK_URL";

    public static UpdateAppFragment newInstance(String apkUrl) {
        UpdateAppFragment fragment = new UpdateAppFragment();
        Bundle params = new Bundle();
        params.putString(APK_URL, apkUrl);
        fragment.setArguments(params);
        return fragment;
    }

    IMain parentRemote;
    UpdateAppFragmentBinding binding;
    UpdateApp_Presenter presenter;
    UpdateApp_ViewModel viewModel = new UpdateApp_ViewModel();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentRemote = (IMain) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.update_app_fragment, container, false);

        viewModel.apkUrl = getArguments().getString(APK_URL);

        presenter = new UpdateApp_Presenter(viewModel, this);
        binding.setPresenter(presenter);
        binding.setViewModel(viewModel);

        presenter.loadInfo();

        return binding.getRoot();
    }

    @Override
    public void onDownloadCompleted() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(MyCons.APK_LAUNCHER_PATH)), "application/vnd.android.package-archive");
        startActivity(intent);
        dismiss();
    }

    @Override
    public void onDownloadFailed() {
        Bus.getInstance().post(new ECancelUpdate());
        dismiss();
    }

    @Override
    public void showAppInfo(String textInfo) {
        viewModel.info.set(textInfo);
    }

    @Override
    public void showMessage(String message) {
        ViewUtil.toast(getActivity(), message);
    }

    @Override
    public void onDestroy() {
        if (presenter != null) presenter.destroy();
        super.onDestroy();
    }
}
