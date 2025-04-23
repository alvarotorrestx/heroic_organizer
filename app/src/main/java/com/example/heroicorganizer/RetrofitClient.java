package com.example.heroicorganizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://comicvine.gamespot.com/api/";
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            String apiKey = getApiKey();
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("API Key not found! Please check local.properties.");
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static String getApiKey() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("local.properties")) {
            properties.load(fis);
            return properties.getProperty("comicvine.api_key");
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Handle missing key gracefully
        }
    }
}
