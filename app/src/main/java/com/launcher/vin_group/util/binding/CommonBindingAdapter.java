package com.launcher.vin_group.util.binding;

import android.databinding.BindingAdapter;
import android.view.View;


public class CommonBindingAdapter {

    @BindingAdapter(value = "show")
    public static void setShow(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter(value = "visible")
    public static void setVisible(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @BindingAdapter(value = "active")
    public static void setActive(View view, boolean active) {
        view.setEnabled(active);
        view.setAlpha(active ? 1.0f : 0.3f);
    }
}