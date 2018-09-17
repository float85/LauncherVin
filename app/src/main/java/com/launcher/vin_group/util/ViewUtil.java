package com.launcher.vin_group.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.launcher.vin_group.R;


public class ViewUtil {

    public static void hideKeyBoard(Context context, View et) {
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        }
    }

    public static void toast(Context context, String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static AlertDialog initConfirmDialog(Context context, String message, DialogInterface.OnClickListener confirmClick, DialogInterface.OnClickListener cancelClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Đồng ý", confirmClick);

        if (cancelClick != null) {
            builder.setNegativeButton("Đóng", cancelClick);
        } else {
            builder.setPositiveButton("Đồng ý", confirmClick);
        }

        return builder.show();
    }

    public static void fullScreen(Window window) {
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE;

        window.getDecorView().setSystemUiVisibility(flags);
        final View decorView = window.getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                try{
                    decorView.setSystemUiVisibility(flags);
                }catch(Exception e){
                }
            }
        });
    }
}
