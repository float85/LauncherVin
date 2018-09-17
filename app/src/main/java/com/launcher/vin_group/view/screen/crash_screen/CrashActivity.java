package com.launcher.vin_group.view.screen.crash_screen;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.launcher.vin_group.R;
import com.launcher.vin_group.databinding.ActivityCrashBinding;
import com.launcher.vin_group.util.LogUtils;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class CrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCrashBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_crash);

        binding.btnRestartApp.setOnClickListener(v -> {
            CustomActivityOnCrash.restartApplication(CrashActivity.this, CustomActivityOnCrash.getConfigFromIntent(getIntent()));
        });

        LogUtils.e(CustomActivityOnCrash.getAllErrorDetailsFromIntent(getBaseContext(), getIntent()));

        binding.btnErrorDetails.setOnClickListener(v -> {
            String error = CustomActivityOnCrash.getAllErrorDetailsFromIntent(getBaseContext(), getIntent());
            Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
        });
    }
}
