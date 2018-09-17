package com.launcher.vin_group.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.launcher.vin_group.model.api.adapter.BooleanAsInAdapter;
import com.launcher.vin_group.model.api.adapter.StringAsLongAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JsonUtil {
    private static Gson instance;

    public static Gson getInstance() {
        if (instance == null) {
            TypeAdapter<Boolean> booleanAsIntAdapter = new BooleanAsInAdapter();
            TypeAdapter<Long> stringAsLongAdapter = new StringAsLongAdapter();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                    .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
                    .registerTypeAdapter(Long.class, stringAsLongAdapter)
                    .registerTypeAdapter(long.class, stringAsLongAdapter)
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            instance = gson;
        }
        return instance;
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static String joinString(Iterable objects) {
        return TextUtils.join(",", objects);
    }
}
