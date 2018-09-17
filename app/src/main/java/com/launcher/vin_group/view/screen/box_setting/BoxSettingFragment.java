package com.launcher.vin_group.view.screen.box_setting;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.launcher.vin_group.App;
import com.launcher.vin_group.R;
import com.launcher.vin_group.databinding.FragmentBoxSettingBinding;
import com.launcher.vin_group.util.Bus;
import com.launcher.vin_group.util.LogUtils;
import com.launcher.vin_group.util.MethodUtil;
import com.launcher.vin_group.util.ViewUtil;
import com.launcher.vin_group.view.eventbus.ERedownloadPlayList;
import com.launcher.vin_group.view.screen.update_app.UpdateAppFragment;



public class BoxSettingFragment extends DialogFragment implements IBoxSetting_View {

    private static final int REQUEST_SETTINGS = 99;

    public static BoxSettingFragment newInstance() {
        return new BoxSettingFragment();
    }

    FragmentBoxSettingBinding binding;
    BoxSetting_Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_box_setting, container, false);

        presenter = new BoxSetting_Presenter(this);

        configKeyHandler();

        binding.vDownload.setOnClickListener(clickListener);
        binding.vSetting.setOnClickListener(clickListener);
        binding.vFolder.setOnClickListener(clickListener);
        binding.vReset.setOnClickListener(clickListener);
        binding.tvClose.setOnClickListener(clickListener);

        return binding.getRoot();
    }

    private void configKeyHandler() {
        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            try {
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    View currentView = getDialog().getCurrentFocus();
                    View nextFocus = null;

                    if (currentView != null) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_RIGHT:
                                nextFocus = findViewById(currentView.getNextFocusRightId());
                                break;
                            case KeyEvent.KEYCODE_DPAD_LEFT:
                                nextFocus = findViewById(currentView.getNextFocusLeftId());
                                break;
                            case KeyEvent.KEYCODE_DPAD_DOWN:
                                nextFocus = findViewById(currentView.getNextFocusDownId());
                                break;
                            case KeyEvent.KEYCODE_DPAD_UP:
                                nextFocus = findViewById(currentView.getNextFocusUpId());
                                break;
                            case KeyEvent.KEYCODE_ENTER:
                                currentView.performClick();
                                break;
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                                currentView.performClick();
                                break;
                            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                                currentView.performClick();
                                break;
                        }
                    }

                    if (nextFocus != null) {
                        nextFocus.requestFocus();
                    }
                }
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
            return true;
        });
    }

    View findViewById(int id) {
        return binding.getRoot().findViewById(id);
    }

    View.OnClickListener clickListener = v -> {
        switch (v.getId()) {
            case R.id.vDownload:
                presenter.checkVersion();
                break;
            case R.id.vSetting:
                startActivityForResult(new Intent(Settings.ACTION_SETTINGS), REQUEST_SETTINGS);
                dismiss();
                break;
            case R.id.vFolder:
                MethodUtil.openUsb(getActivity());
                dismiss();
                break;
            case R.id.vReset:
                ViewUtil.initConfirmDialog(getActivity(), "Khôi phục lại cài đặt ban đầu của box ?",
                        (dialog, which) -> {
                            presenter.reset();
                        }, (dialog, which) -> {
                            dialog.dismiss();
                        }).show();
                break;
            case R.id.tvClose:
                dismiss();
                break;
        }
    };

    @Override
    public void showUpdateScreen(String urlLink) {
        dismiss();
        UpdateAppFragment.newInstance(urlLink)
                .show(getFragmentManager(), "");
    }

    @Override
    public void onDeviceUpToDate() {
        showMessage("Thiết bị đang sử dụng phiên bản mới nhất");
        dismiss();
    }

    @Override
    public void showMessage(String message) {
        ViewUtil.toast(getActivity(), message);
    }

    @Override
    public void onDeviceReset() {
        new Handler().postDelayed(() -> Bus.getInstance().post(new ERedownloadPlayList()), 200);
        dismiss();
    }

    @Override
    public void onDestroy() {
        if (presenter != null)
            presenter.destroy();

        super.onDestroy();
    }
}
