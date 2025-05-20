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

        // Comic class creation
        Map<String, Object> comicSchema = new HashMap<>();
        comicSchema.put("class", "Comic");
        comicSchema.put("vectorizer", "none");
        comicSchema.put("vectorIndexType", "hnsw");

        // Set properties of Comic
        List<Map<String, Object>> comicProperties = new ArrayList<>();
        comicProperties.add(Map.of(
                "name", "title",
                "dataType", List.of("text"),
                "description", "The comic title"
        ));
        comicProperties.add(Map.of(
                "name", "publisher_names",
                "dataType", List.of("text"),
                "description", "Publisher names"
        ));
        comicSchema.put("properties", comicProperties);

        // ComicVariant class creation
        Map<String, Object> comicVariantSchema = new HashMap<>();
        comicVariantSchema.put("class", "ComicVariant");
        comicVariantSchema.put("vectorizer", "img2vec-neural");
        comicVariantSchema.put("vectorIndexType", "hnsw");

        Map<String, Object> moduleConfig = new HashMap<>();
        moduleConfig.put("img2vec-neural", Map.of("imageFields", List.of("image")));
        comicVariantSchema.put("moduleConfig", moduleConfig);

        // Properties that each Comic/Variant will hold - returned data from scanned comic
        List<Map<String, Object>> comicMetadata = new ArrayList<>();
        comicMetadata.add(Map.of(
                "name", "image",
                "dataType", List.of("blob"),
                "description", "The image to load"
        ));
        comicMetadata.add(Map.of(
                "name", "title",
                "dataType", List.of("text"),
                "description", "The comic title"
        ));
        comicMetadata.add(Map.of(
                "name", "issue_number",
                "dataType", List.of("text"),
                "description", "The issue number"
        ));
        comicMetadata.add(Map.of(
                "name", "variant_id",
                "dataType", List.of("text"),
                "description", "Variant ID"
        ));
        comicMetadata.add(Map.of(
                "name", "cover_artist",
                "dataType", List.of("text"),
                "description", "Cover artist"
        ));
        comicMetadata.add(Map.of(
                "name", "author",
                "dataType", List.of("text"),
                "description", "Comic author"
        ));
        comicMetadata.add(Map.of(
                "name", "date_published",
                "dataType", List.of("text"),
                "description", "Date of release"
        ));
        comicMetadata.add(Map.of(
                "name", "upc",
                "dataType", List.of("text"),
                "description", "UPC code"
        ));
        comicMetadata.add(Map.of(
                "name", "description",
                "dataType", List.of("text"),
                "description", "Comic description"
        ));
        comicVariantSchema.put("properties", comicMetadata);

        // Convert both schemas to JSON and send POST requests
        List<Map<String, Object>> schemas = List.of(comicSchema, comicVariantSchema);

        for (Map<String, Object> schema : schemas) {
            String json = gson.toJson(schema);
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
                        Log.d("WeaviateSchema", "Schema successfully created: " + schema.get("class"));
                    } else {
                        Log.e("WeaviateSchema", "Schema creation failed: " + response.code() + " - " + response.message());
                    }

                    // Helps avoid memory leaks
                    response.close();
                }
            });
        }
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

                        // Booleans for checking if each schema exists
                        boolean comicExists = false;
                        boolean comicVariantExists = false;

                        // Loop through response to find the classes in schema
                        for (int i = 0; i < classes.length(); i++) {
                            JSONObject classObj = classes.getJSONObject(i);
                            String className = classObj.getString("class");

                            if (className.equals("Comic")) {
                                comicExists = true;
                            }

                            if (className.equals("ComicVariant")) {
                                comicVariantExists = true;
                            }
                        }

                        // If either class object is missing, create both
                        if (!comicExists || !comicVariantExists) {
                            WeaviateConfig.createWeaviateSchema();
                        } else {
                            Log.d("WeaviateSchema", "Comic & ComicVariant schemas already exist.");
                        }
                    } else {
                        Log.e("WeaviateSchema", "Error checking schemas: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e("WeaviateSchema", "Error checking schemas: " + e.getMessage());
                }

                // Helps avoid memory leaks
                response.close();
            }
        });
    }
}
