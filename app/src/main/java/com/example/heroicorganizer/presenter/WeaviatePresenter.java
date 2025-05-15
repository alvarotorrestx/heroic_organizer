package com.example.heroicorganizer.presenter;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import com.example.heroicorganizer.model.WeaviateImage;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WeaviatePresenter {
    public static String toBase64(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] imageArray = outputStream.toByteArray();
        return Base64.encodeToString(imageArray, Base64.NO_WRAP);
    }

    public static void uploadWeaviateImage(WeaviateImage weaviateImage) {
        OkHttpClient client = new OkHttpClient();

        // Create image - json body for post using gson
        Gson gson = new Gson();
        Map<String, Object> body = new HashMap<>();
        body.put("class", "Comic");

        Map<String, Object> props = new HashMap<>();
        props.put("image", weaviateImage.getImage());
        props.put("title", weaviateImage.getTitle());

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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("WeaviateUpload", "Image uploaded successfully");
                } else {
                    Log.e("WeaviateUpload", "Upload failed: " + response.code() + " - " + response.message());
                }

                // Helps avoid memory leaks
                response.close();
            }
        });
    }
}
