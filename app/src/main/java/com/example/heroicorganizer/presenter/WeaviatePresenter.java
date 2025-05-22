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

        // Search for image through both ComicVariant and Comic to find closest match (certainty)
        String graphQLQuery =
                "{ " +
                        "  Get { " +
                        "    ComicVariant(nearImage: { image: \"" + convertedImage + "\" }, limit: 3) { " +
                        "      title image variant_id issue_number publisher_names cover_artist author date_published upc description " +
                        "      _additional { certainty } " +
                        "      parent_comic { ... on Comic { title comic_id issue_number } } " +
                        "    } " +
                        "    Comic(nearImage: { image: \"" + convertedImage + "\" }, limit: 3) { " +
                        "      title image issue_number publisher_names cover_artist author date_published upc description " +
                        "      comic_variants { ... on ComicVariant { variant_id issue_number title } } " +
                        "      _additional { certainty } " +
                        "    } " +
                        "  } " +
                        "}";

        JsonObject payload = new JsonObject();
        payload.addProperty("query", graphQLQuery);

        RequestBody requestBody = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/v1/graphql")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Search failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Server error: " + response.code());
                    response.close();
                    return;
                }

                String res = response.body().string();
                Log.d("WeaviateSearch", "Raw JSON response: " + res);
                response.close();

                try {
                    JSONObject json = new JSONObject(res);
                    JSONObject data = json.getJSONObject("data").getJSONObject("Get");

                    JSONArray variantResults = data.optJSONArray("ComicVariant");
                    JSONArray comicResults = data.optJSONArray("Comic");

                    JSONObject bestMatch = null;
                    double highestCertainty = -1;
                    boolean isVariant = false;

                    // If Weaviate finds the highest certainty result to be of ComicVariant return the variant
                    if (variantResults != null) {
                        for (int i = 0; i < variantResults.length(); i++) {
                            JSONObject result = variantResults.getJSONObject(i);
                            double certainty = result.getJSONObject("_additional").optDouble("certainty", 0);
                            Log.d("WeaviateSearch", "ComicVariant Match " + i + ": " + result.optString("title") + " (certainty: " + certainty + ")");
                            if (certainty > highestCertainty) {
                                highestCertainty = certainty;
                                bestMatch = result;
                                isVariant = true;
                            }
                        }
                    }

                    // If Weaviate finds the highest certainty result to be of Comic return the comic
                    if (comicResults != null) {
                        for (int i = 0; i < comicResults.length(); i++) {
                            JSONObject result = comicResults.getJSONObject(i);
                            double certainty = result.getJSONObject("_additional").optDouble("certainty", 0);
                            Log.d("WeaviateSearch", "Comic Match " + i + ": " + result.optString("title") + " (certainty: " + certainty + ")");
                            if (certainty > highestCertainty) {
                                highestCertainty = certainty;
                                bestMatch = result;
                                isVariant = false;
                            }
                        }
                    }

                    if (bestMatch != null) {
                        WeaviateSearchResult resultModel = new WeaviateSearchResult();

                        resultModel.setTitle(bestMatch.getString("title"));
                        resultModel.setImage(bestMatch.getString("image"));
                        resultModel.setIssueNumber(bestMatch.optString("issue_number", ""));
                        resultModel.setPublisherNames(bestMatch.optString("publisher_names", ""));
                        resultModel.setCoverArtist(bestMatch.optString("cover_artist", ""));
                        resultModel.setAuthor(bestMatch.optString("author", ""));
                        resultModel.setDatePublished(bestMatch.optString("date_published", ""));
                        resultModel.setUpc(bestMatch.optString("upc", ""));
                        resultModel.setDescription(bestMatch.optString("description", ""));

                        if (isVariant) {
                            resultModel.setVariantId(bestMatch.optString("variant_id", ""));
                            if (bestMatch.has("parent_comic") && !bestMatch.isNull("parent_comic")) {
                                JSONArray parentArray = bestMatch.getJSONArray("parent_comic");
                                if (parentArray.length() > 0) {
                                    JSONObject parent = parentArray.getJSONObject(0);
                                    resultModel.setParentComicTitle(parent.optString("title", ""));
                                    resultModel.setParentComicId(parent.optString("comic_id", ""));
                                    resultModel.setParentComicIssueNumber(parent.optString("issue_number", ""));
                                }
                            }
                        } else {
                            JSONArray variants = bestMatch.optJSONArray("comic_variants");
                            List<String> variantList = new ArrayList<>();
                            if (variants != null) {
                                for (int i = 0; i < variants.length(); i++) {
                                    JSONObject v = variants.getJSONObject(i);
                                    variantList.add(v.optString("variant_id", ""));
                                    variantList.add(v.optString("issue_number", ""));
                                    variantList.add(v.optString("title", ""));
                                }
                            }
                            resultModel.setVariants(variantList);
                        }

                        Log.d("WeaviateSearch", "Final best match: " + resultModel.getTitle() + " (isVariant: " + isVariant + ")");
                        callback.onSuccess(resultModel);
                    } else {
                        callback.onFailure("No matches found.");
                    }
                } catch (JSONException e) {
                    Log.e("WeaviateSearch", "JSON parsing failed: " + e.getMessage());
                    callback.onFailure("Failed to parse JSON.");
                }
            }
        });
    }
}
