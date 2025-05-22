package com.example.heroicorganizer.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MetronApiConfig {
    private static String username = "";
    private static String password = "";
    private static boolean isLoaded = false;

    public static void load(Context context) {
        if (isLoaded) return;

        try {
            InputStream is = context.getAssets().open("metron-api.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(json);

            username = obj.getString("username");
            password = obj.getString("password");
            isLoaded = true;
        } catch (Exception e) {
            Log.e("MetronApiConfig", "Failed to load API config", e);
        }
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }
}
