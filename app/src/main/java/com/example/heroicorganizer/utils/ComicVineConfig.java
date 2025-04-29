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
            // Open JSON file from assets
            InputStream is = context.getAssets().open(CONFIG_FILE);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            // Convert byte stream to string using UTF-8 encoding
            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonStr);

            // Extract API key safely with fallback mechanism
            String apiKey = jsonObject.optString("api_key", "");
            if (apiKey.isEmpty()) {
                Log.e("ComicVineConfig", "API Key is missing in JSON file!");
            } else {
                Log.d("ComicVineConfig", "API Key successfully retrieved.");
            }

            return apiKey;

        } catch (Exception e) {
            Log.e("ComicVineConfig", "Failed to load API key from JSON file", e);
            return null;
        }
    }
}
