package com.example.heroicorganizer.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MarvelApiConfig {

    private static final String CONFIG_FILE = "marvel-api.json";
    private static final String KEY_PUBLIC = "KEY_PUBLIC";
    private static final String KEY_PRIVATE = "KEY_PRIVATE";

    private String publicKey = "";
    private String privateKey= "";
    private String timeStamp= "";
    private String md5Hash= "";


    public MarvelApiConfig(Context context) {
        JSONObject config = getApiKey(context);
        this.publicKey = config.optString(KEY_PUBLIC, "");
        this.privateKey = config.optString(KEY_PRIVATE, "");
        this.timeStamp = String.valueOf(System.currentTimeMillis());
        this.md5Hash = generateMd5(timeStamp + privateKey + publicKey);
    }

    public static JSONObject getApiKey(Context context) {
        try {
            InputStream is = context.getAssets().open(CONFIG_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            return new JSONObject(jsonStr);
        } catch (Exception e) {
            Log.e("MarvelApiConfig", "Failed to load Marvel API config", e);
            return new JSONObject();
        }
    }

    public String generateMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(input.getBytes());
            StringBuilder md5 = new StringBuilder();
            for (byte b : md5Bytes) {
                md5.append(String.format("%02x", b & 0xff));
            }
            return md5.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("MarvelApiConfig", "MD5 algorithm not found", e);
            return "";
        }
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getMd5Hash() {
        return md5Hash;
    }
}