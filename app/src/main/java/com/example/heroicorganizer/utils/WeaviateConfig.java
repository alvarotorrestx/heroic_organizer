package com.example.heroicorganizer.utils;

import android.util.Log;
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class WeaviateConfig {
    private static void createWeaviateSchema() {
        OkHttpClient client = new OkHttpClient();

        // Use gson to create a clean json call
        Gson gson = new Gson();

        // Comic class creation
        Map<String, Object> comicSchema = new HashMap<>();
        comicSchema.put("class", "Comic");
        comicSchema.put("vectorizer", "img2vec-neural");
        comicSchema.put("vectorIndexType", "hnsw");

        Map<String, Object> comicModuleConfig = new HashMap<>();
        comicModuleConfig.put("img2vec-neural", Map.of("imageFields", List.of("image")));
        comicSchema.put("moduleConfig", comicModuleConfig);


        // Set properties of Comic - For Solo Comic and/or Parent Comics
        List<Map<String, Object>> parentComicProperties = new ArrayList<>();
        parentComicProperties.add(Map.of(
                "name", "comic_id",
                "dataType", List.of("text"),
                "description", "Comic ID"
        ));
        parentComicProperties.add(Map.of(
                "name", "title",
                "dataType", List.of("text"),
                "description", "The comic title"
        ));
        parentComicProperties.add(Map.of(
                "name", "publisher_names",
                "dataType", List.of("text"),
                "description", "Publisher names"
        ));
        parentComicProperties.add(Map.of(
                "name", "comic_variants",
                "dataType",  List.of("ComicVariant"),
                "description", "All variants of the comic"
        ));
        parentComicProperties.add(Map.of(
                "name", "image",
                "dataType", List.of("blob"),
                "description", "The image to load"
        ));
        parentComicProperties.add(Map.of(
                "name", "issue_number",
                "dataType", List.of("text"),
                "description", "The issue number"
        ));
        parentComicProperties.add(Map.of(
                "name", "cover_artist",
                "dataType", List.of("text"),
                "description", "Cover artist"
        ));
        parentComicProperties.add(Map.of(
                "name", "author",
                "dataType", List.of("text"),
                "description", "Comic author"
        ));
        parentComicProperties.add(Map.of(
                "name", "date_published",
                "dataType", List.of("text"),
                "description", "Date of release"
        ));
        parentComicProperties.add(Map.of(
                "name", "upc",
                "dataType", List.of("text"),
                "description", "UPC code"
        ));
        parentComicProperties.add(Map.of(
                "name", "description",
                "dataType", List.of("text"),
                "description", "Comic description"
        ));
        comicSchema.put("properties", parentComicProperties);

        // ComicVariant class creation
        Map<String, Object> comicVariantSchema = new HashMap<>();
        comicVariantSchema.put("class", "ComicVariant");
        comicVariantSchema.put("vectorizer", "img2vec-neural");
        comicVariantSchema.put("vectorIndexType", "hnsw");

        Map<String, Object> moduleConfig = new HashMap<>();
        moduleConfig.put("img2vec-neural", Map.of("imageFields", List.of("image")));
        comicVariantSchema.put("moduleConfig", moduleConfig);

        // Properties that each Comic Variant will hold
        // For Comic Variants that belong to a parent comic
        List<Map<String, Object>> comicVariantProperties = new ArrayList<>();
        comicVariantProperties.add(Map.of(
                "name", "image",
                "dataType", List.of("blob"),
                "description", "The image to load"
        ));
        comicVariantProperties.add(Map.of(
                "name", "title",
                "dataType", List.of("text"),
                "description", "The comic title"
        ));
        comicVariantProperties.add(Map.of(
                "name", "publisher_names",
                "dataType", List.of("text"),
                "description", "Publisher names"
        ));
        comicVariantProperties.add(Map.of(
                "name", "issue_number",
                "dataType", List.of("text"),
                "description", "The issue number"
        ));
        comicVariantProperties.add(Map.of(
                "name", "variant_id",
                "dataType", List.of("text"),
                "description", "Variant ID"
        ));
        comicVariantProperties.add(Map.of(
                "name", "cover_artist",
                "dataType", List.of("text"),
                "description", "Cover artist"
        ));
        comicVariantProperties.add(Map.of(
                "name", "author",
                "dataType", List.of("text"),
                "description", "Comic author"
        ));
        comicVariantProperties.add(Map.of(
                "name", "date_published",
                "dataType", List.of("text"),
                "description", "Date of release"
        ));
        comicVariantProperties.add(Map.of(
                "name", "upc",
                "dataType", List.of("text"),
                "description", "UPC code"
        ));
        comicVariantProperties.add(Map.of(
                "name", "description",
                "dataType", List.of("text"),
                "description", "Comic description"
        ));
        comicVariantSchema.put("properties", comicVariantProperties);

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

                        // If either class object is missing
                        // First delete both schemas and then create both
                        if (!comicExists || !comicVariantExists) {
                            deleteSchemas();
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

    private static void deleteSchemas() {
        OkHttpClient client = new OkHttpClient();

        Request delComic = new Request.Builder()
                .url("http://10.0.2.2:8080/v1/schema/Comic")
                .delete()
                .addHeader("Content-Type", "application/json")
                .build();

        Request delComicVariant = new Request.Builder()
                .url("http://10.0.2.2:8080/v1/schema/ComicVariant")
                .delete()
                .addHeader("Content-Type", "application/json")
                .build();

        // Booleans that will update on completed deletes
        AtomicBoolean comicDeleted = new AtomicBoolean(false);
        AtomicBoolean variantDeleted = new AtomicBoolean(false);

        // Once deletes are complete, create the schemas
        Runnable tryRecreateSchema = () -> {
            if (comicDeleted.get() && variantDeleted.get()) {
                // Create schemas
                createWeaviateSchema();
                // Add reference from Comic into the ComicVariant properties
                addParentComicReference();
            }
        };

        // Delete Comic call
        client.newCall(delComic).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("WeaviateSchema", "Comic schema deleted.");
                }
                comicDeleted.set(true);
                tryRecreateSchema.run();
                response.close();
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("WeaviateSchema", "Failed to delete Comic: " + e.getMessage());
                comicDeleted.set(true);
                tryRecreateSchema.run();
            }
        });

        // Delete ComicVariant call
        client.newCall(delComicVariant).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("WeaviateSchema", "ComicVariant schema deleted.");
                }
                variantDeleted.set(true);
                tryRecreateSchema.run();
                response.close();
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("WeaviateSchema", "Failed to delete ComicVariant: " + e.getMessage());
                variantDeleted.set(true);
                tryRecreateSchema.run();
            }
        });
    }

    // Method created to add parent_comic relation between Comic class
    // Needs to be added after schema creation due to reference error on creation
    private static void addParentComicReference() {
        OkHttpClient client = new OkHttpClient();

        Gson gson = new Gson();

        Map<String, Object> parentComicProperty = new HashMap<>();
        parentComicProperty.put("name", "parent_comic");
        parentComicProperty.put("dataType", List.of("Comic"));
        parentComicProperty.put("description", "The main comic this variant belongs to");

        String json = gson.toJson(parentComicProperty);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        // Post method to push update to ComicVariant class properties
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/v1/schema/ComicVariant/properties")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("WeaviateSchema", "Failed to add parent_comic: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("WeaviateSchema", "Successfully added parent_comic to ComicVariant.");
                } else {
                    Log.e("WeaviateSchema", "Failed to add parent_comic: " + response.code() + " - " + response.message());
                }
                response.close();
            }
        });
    }

}
