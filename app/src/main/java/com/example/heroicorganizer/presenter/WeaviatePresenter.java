package com.example.heroicorganizer.presenter;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.example.heroicorganizer.callback.WeaviateSearchImageCallback;
import com.example.heroicorganizer.callback.WeaviateUploadCallback;
import com.example.heroicorganizer.model.WeaviateImage;
import com.example.heroicorganizer.model.WeaviateSearchResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class WeaviatePresenter {
    public static String toBase64(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] imageArray = outputStream.toByteArray();
        return Base64.encodeToString(imageArray, Base64.NO_WRAP);
    }

    // classType is "Comic" or "ComicVariant"
    // parentId should reference the existing parent comic if a variant or null if no parent
    public static void uploadWeaviateImage(String classType, String parentId, WeaviateImage weaviateImage, WeaviateUploadCallback callback) {
        if (classType == null || TextUtils.isEmpty(classType) || (!classType.equals("Comic") && !classType.equals("ComicVariant"))) {
            Log.e("WeaviateUpload", "Invalid classType");
            callback.onFailure("Invalid classType");
            return;
        }

        if (classType.equals("ComicVariant") && TextUtils.isEmpty(parentId)) {
            Log.e("WeaviateUpload", "Missing comic parent id of variant.");
            callback.onFailure("Missing comic parent id of variant.");
            return;
        }

        OkHttpClient client = new OkHttpClient();

        // Create image - json body for post using gson
        Gson gson = new Gson();

        // Generate random id for comic
        String uuid = UUID.randomUUID().toString();

        Map<String, Object> body = new HashMap<>();
        body.put("class", classType);
        body.put("id", uuid);

        Map<String, Object> props = new HashMap<>();
        // Comic Props
        if (classType.equals("Comic")) {
            props.put("comic_id", uuid);
            props.put("image", weaviateImage.getImage());
            props.put("title", weaviateImage.getTitle());
            props.put("publisher_names", weaviateImage.getPublishers());
            props.put("issue_number", weaviateImage.getIssueNumber());
            props.put("cover_artist", weaviateImage.getCoverArtist());
            props.put("author", weaviateImage.getAuthor());
            props.put("date_published", weaviateImage.getDatePublished());
            props.put("upc", weaviateImage.getUpc());
            props.put("description", weaviateImage.getDescription());
        } else {
            // Variant Comic Props
            props.put("image", weaviateImage.getImage());
            props.put("title", weaviateImage.getTitle());
            props.put("publisher_names", weaviateImage.getPublishers());
            props.put("issue_number", weaviateImage.getIssueNumber());
            props.put("variant_id", uuid);
            props.put("cover_artist", weaviateImage.getCoverArtist());
            props.put("author", weaviateImage.getAuthor());
            props.put("date_published", weaviateImage.getDatePublished());
            props.put("upc", weaviateImage.getUpc());
            props.put("description", weaviateImage.getDescription());
            // Attaches the parent comic through Weaviate beacon reference
            props.put("parent_comic", List.of(Map.of("beacon", "weaviate://localhost/Comic/" + parentId)));
        }

        body.put("properties", props);

        // Convert to JSON
        String json = gson.toJson(body);

        // Send a post request with the json body
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/v1/objects")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("WeaviateUpload", "Upload failed: " + e.getMessage());
                callback.onFailure("Upload failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("WeaviateUpload", "Image uploaded successfully with ID: " + uuid);

                    // Creates a parent comic reference to the comic variant -> comic_variants
                    if (classType.equals("ComicVariant")) {
                        patchParentComicWithVariant(parentId, uuid);
                    }

                    callback.onSuccess("Image uploaded successfully with ID: " + uuid);
                } else {
                    Log.e("WeaviateUpload", "Upload failed: " + response.code() + " - " + response.message());
                    callback.onFailure("Upload failed: " + response.message());
                }

                // Helps avoid memory leaks
                response.close();
            }
        });
    }

    // TODO: Avoid duplicate comics created/added to parent_comic
    private static void patchParentComicWithVariant(String parentId, String variantId) {
        OkHttpClient client = new OkHttpClient();

        Gson gson = new Gson();

        Map<String, Object> patchBody = new HashMap<>();
        patchBody.put("properties", Map.of("comic_variants", List.of(Map.of("beacon", "weaviate://localhost/ComicVariant/" + variantId))));

        String json = gson.toJson(patchBody);

        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));
        Request patchRequest = new Request.Builder()
                .url("http://10.0.2.2:8080/v1/objects/Comic/" + parentId)
                .patch(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(patchRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("WeaviatePatch", "Failed to patch parent comic: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("WeaviatePatch", "Parent comic patched with new variant.");
                } else {
                    Log.e("WeaviatePatch", "Patch failed: " + response.code() + " - " + response.message());
                }

                response.close();
            }
        });
    }

    public static void searchByImage(Bitmap image, WeaviateSearchImageCallback callback) {
        // Convert the image to base64
        String convertedImage = toBase64(image);

        OkHttpClient client = new OkHttpClient();

        // Search for image through ComicVariant class
        String variantQuery =
                "{ " +
                        "  Get { " +
                        "    ComicVariant(nearImage: { " +
                        "      image: \"" + convertedImage + "\" " +
                        "    }, limit: 3) { " +
                        "      title " +
                        "      image " +
                        "      variant_id " +
                        "      issue_number " +
                        "      publisher_names " +
                        "      cover_artist " +
                        "      author " +
                        "      date_published " +
                        "      upc " +
                        "      description " +
                        "      parent_comic { " +
                        "        title " +
                        "        comic_id " +
                        "        issue_number " +
                        "      }" +
                        "    } " +
                        "  } " +
                        "}";

        JsonObject variantPayload = new JsonObject();
        variantPayload.addProperty("query", variantQuery);

        RequestBody requestBody = RequestBody.create(variantPayload.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/v1/graphql")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("WeaviateSearch", "Search failed: " + e.getMessage());
                callback.onFailure("Search failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    try {
                        Log.d("WeaviateSearch", "Raw JSON response: " + res);
                        JSONObject json = new JSONObject(res);
                        JSONArray results = json
                                .getJSONObject("data")
                                .getJSONObject("Get")
                                .getJSONArray("ComicVariant");

                        if (results.length() > 0) {
                            // Match found in ComicVariant
                            JSONObject match = results.getJSONObject(0);
                            WeaviateSearchResult variantResults = new WeaviateSearchResult();
                            variantResults.setTitle(match.getString("title"));
                            variantResults.setImage(match.getString("image"));
                            variantResults.setIssueNumber(match.getString("issue_number"));
                            variantResults.setPublisherNames(match.getString("publisher_names"));
                            variantResults.setCoverArtist(match.getString("cover_artist"));
                            variantResults.setAuthor(match.getString("author"));
                            variantResults.setDatePublished(match.getString("date_published"));
                            variantResults.setUpc(match.getString("upc"));
                            variantResults.setDescription(match.getString("description"));
                            JSONObject parentComic = match.getJSONObject("parent_comic");
                            variantResults.setParentComicTitle(parentComic.getString("parent_comic_title"));
                            variantResults.setParentComicIssueNumber(parentComic.getString("parent_comic_issue_number"));
                            variantResults.setParentComicId(parentComic.getString("parent_comic_id"));

                            callback.onSuccess(variantResults);
                        } else {
                            // If no matches found in ComicVariant, try Comic
                            searchImageByComic(convertedImage, callback);
                        }
                    } catch (JSONException e) {
                        callback.onFailure("Failed to parse JSON.");
                    }
                } else {
                    callback.onFailure("Server error: " + response.code());
                }

                response.close();
            }
        });
    }

    private static void searchImageByComic(String convertedImage, WeaviateSearchImageCallback callback) {
        OkHttpClient client = new OkHttpClient();

        // Search for image through Comic class
        String comicQuery =
                "{ " +
                        "  Get { " +
                        "    Comic(nearImage: { " +
                        "      image: \"" + convertedImage + "\" " +
                        "    }, limit: 3) { " +
                        "      title " +
                        "      image " +
                        "      variant_id " +
                        "      issue_number " +
                        "      publisher_names " +
                        "      cover_artist " +
                        "      author " +
                        "      date_published " +
                        "      upc " +
                        "      description " +
                        "      comic_variants { " +
                        "        variant_id " +
                        "        issue_number " +
                        "        title " +
                        "      }" +
                        "    } " +
                        "  } " +
                        "}";

        JsonObject comicPayload = new JsonObject();
        comicPayload.addProperty("query", comicQuery);

        RequestBody requestBody = RequestBody.create(comicPayload.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/v1/graphql")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("WeaviateSearch", "Search failed: " + e.getMessage());
                callback.onFailure("Search failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    try {
                        Log.d("WeaviateSearch", "Raw JSON response: " + res);
                        JSONObject json = new JSONObject(res);
                        JSONArray results = json
                                .getJSONObject("data")
                                .getJSONObject("Get")
                                .getJSONArray("Comic");

                        if (results.length() > 0) {
                            // Match found in Comic
                            JSONObject match = results.getJSONObject(0);
                            WeaviateSearchResult comicResults = new WeaviateSearchResult();
                            comicResults.setTitle(match.getString("title"));
                            comicResults.setImage(match.getString("image"));
                            comicResults.setIssueNumber(match.getString("issue_number"));
                            comicResults.setPublisherNames(match.getString("publisher_names"));
                            comicResults.setCoverArtist(match.getString("cover_artist"));
                            comicResults.setAuthor(match.getString("author"));
                            comicResults.setDatePublished(match.getString("date_published"));
                            comicResults.setUpc(match.getString("upc"));
                            comicResults.setDescription(match.getString("description"));

                            JSONArray variants = match.getJSONArray("comic_variants");

                            List<String> foundVariants = new ArrayList<>();

                            for (int i = 0; i < variants.length(); i++) {
                                JSONObject variant = variants.getJSONObject(i);
                                String variantId = variant.optString("variant_id", "");
                                String variantIssueNumber = variant.optString("issue_number", "");
                                String variantTitle = variant.optString("title", "");

                                foundVariants.add(variantId);
                                foundVariants.add(variantIssueNumber);
                                foundVariants.add(variantTitle);
                            }

                            comicResults.setVariants(foundVariants);

                            callback.onSuccess(comicResults);
                        } else {
                            Log.e("WeaviateSearch", "No match found in Comic fallback: " + response.code() + " - " + response.message());
                            callback.onFailure("No match found in Comic fallback: " + response.code());
                        }
                    } catch (JSONException e) {
                        callback.onFailure("Failed to parse JSON.");
                    }
                } else {
                    callback.onFailure("Server error: " + response.code());
                }

                response.close();
            }
        });
    }
}
