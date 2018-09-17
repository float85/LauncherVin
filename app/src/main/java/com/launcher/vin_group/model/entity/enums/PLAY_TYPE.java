package com.launcher.vin_group.model.entity.enums;

import com.google.gson.annotations.SerializedName;



public enum PLAY_TYPE {
    @SerializedName("1")
    VIDEO(1),
    @SerializedName("2")
    LIVE_STREAM(2),
    @SerializedName("3")
    IMAGE(3),
    @SerializedName("4")
    SCROLL_TEXT(4);

    private final int value;

    public int getValue() {
        return value;
    }

    PLAY_TYPE(int value) {
        this.value = value;
    }

    public static PLAY_TYPE findByValue(int value) {
        for (PLAY_TYPE play_type : values()) {
            if (play_type.value == value) return play_type;
        }

        return null;
    }
}
