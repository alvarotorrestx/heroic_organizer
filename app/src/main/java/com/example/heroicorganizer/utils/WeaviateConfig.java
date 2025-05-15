package com.example.heroicorganizer.utils;

import android.util.Log;
import com.google.gson.Gson;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaviateConfig {
    private static void createWeaviateSchema() {
        OkHttpClient client = new OkHttpClient();

        // Use gson to create a clean json call
        Gson gson = new Gson();

        Map<String, Object> schemaBody = new HashMap<>();
        schemaBody.put("class", "Comic");
        schemaBody.put("vectorizer", "img2vec-neural");
        schemaBody.put("vectorIndexType", "hnsw");

        Map<String, Object> moduleConfig = new HashMap<>();
        moduleConfig.put("img2vec-neural", Map.of("imageFields", List.of("image")));
        schemaBody.put("moduleConfig", moduleConfig);

        List<Map<String, Object>> properties = new ArrayList<>();
        properties.add(Map.of(
                "name", "image",
                "dataType", List.of("blob"),
                "description", "The image to load"
        ));
        properties.add(Map.of(
                "name", "title",
                "dataType", List.of("text"),
                "description", "The comic title"
        ));
        schemaBody.put("properties", properties);

        // Convert to JSON
        String json = gson.toJson(schemaBody);

        // Send a post request with the json body
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/v1/schema")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("WeaviateSchema", "Failed to create schema: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("WeaviateSchema", "Schema successfully created!");
                } else {
                    Log.e("WeaviateSchema", "Schema creation failed: " + response.code() + " - " + response.message());
                }

                // Helps avoid memory leaks
                response.close();
            }
        });
    }

    public static void checkWeaviateSchema() {
        OkHttpClient client = new OkHttpClient();

        // Send a get request to schema url
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/v1/schema")
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("WeaviateSchema", "Failed to fetch schema: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray classes = json.getJSONArray("classes");

                        // Iterate through json response object
                        boolean comicClassExists = false;
                        for (int i = 0; i < classes.length(); i++) {
                            JSONObject classObj = classes.getJSONObject(i);

                            // If the "Comic" class schema exists - stop
                            if (classObj.getString("class").equals("Comic")) {
                                comicClassExists = true;
                                break;
                            }
                        }

                        // If the "Comic" class schema doesn't exist - create it
                        if (!comicClassExists) {
                            WeaviateConfig.createWeaviateSchema();
                        } else {
                            Log.d("WeaviateSchema", "Comic schema already exists.");
                        }
                    } else {
                        Log.e("WeaviateSchema", "Error checking schema: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e("WeaviateSchema", "Error checking schema: " + e.getMessage());
                }

                // Helps avoid memory leaks
                response.close();
            }
        });
    }
}
