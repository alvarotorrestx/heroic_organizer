package com.example.heroicorganizer.presenter;

import android.content.Context;
import com.example.heroicorganizer.callback.ComicVineCallback;
import com.example.heroicorganizer.model.ComicVine;
import com.example.heroicorganizer.model.ComicVineDisplay;
import com.example.heroicorganizer.utils.ComicVineConfig;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComicVinePresenter {
    public static void searchComics(Context context, String query, ComicVineCallback callback) {
        String apiKey = ComicVineConfig.getApiKey(context);
        if (apiKey == null || apiKey.isEmpty()) {
            callback.onFailure("API key missing");
            return;
        }

        String url = "https://comicvine.gamespot.com/api/search/?api_key=" + apiKey +
                "&query=" + query + "&format=json";
//        + "&limit=10" - Can add to url to limit results from response

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "HeroicOrganizerApp/1.0")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("API Request Failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("API Response Failed: " + response.message());
                    return;
                }

                String json = response.body().string();
                Gson gson = new Gson();
                ComicVine apiResponse = gson.fromJson(json, ComicVine.class);

                List<ComicVineDisplay> filteredResponse = new ArrayList<>();
                for (ComicVine.Result result : apiResponse.results) {
                    String publisher = result.publisher != null ? result.publisher.name.toLowerCase() : "";
                    if (publisher.contains("marvel") || publisher.contains("dc")) {
                        filteredResponse.add(result.toDisplay());
                    }
                }

                // If no results found after filtering
                if (filteredResponse.isEmpty()) {
                    String querySearch = call.request().url().queryParameter("query");
                    callback.onFailure("No results found for " + (querySearch != null ? querySearch : "your search") + ".");
                    return;
                }

                callback.onSuccess(filteredResponse);
            }
        });
    }
}
