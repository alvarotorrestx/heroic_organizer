package com.example.heroicorganizer.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ComicVineConfig {

    private static final String CONFIG_FILE = "comic-vine.json";

    public static String getApiKey(Context context) {
        try {
            InputStream is = context.getAssets().open(CONFIG_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonStr);
            return jsonObject.getString("api_key");

        } catch (Exception e) {
            Log.e("ComicVineConfig", "Failed to load API key", e);
            return null;
        }
    }
}
