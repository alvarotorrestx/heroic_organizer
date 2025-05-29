package com.example.heroicorganizer.presenter;

import android.content.Context;
import com.example.heroicorganizer.callback.ComicVineCallback;
import com.example.heroicorganizer.callback.ComicVineTeamsCallback;
import com.example.heroicorganizer.model.CharacterDetail;
import com.example.heroicorganizer.model.ComicVine;
import com.example.heroicorganizer.model.ComicVineDisplay;
import com.example.heroicorganizer.utils.ComicVineConfig;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

                List<ComicVine.Result> filteredCharacters = new ArrayList<>();
                for (ComicVine.Result result : apiResponse.results) {
                    String publisher = result.publisher != null ? result.publisher.name.toLowerCase() : "";
                    if ((publisher.contains("marvel") || publisher.contains("dc")) && "character".equals(result.resource_type)) {
                        filteredCharacters.add(result);
                    }
                }

                if (filteredCharacters.isEmpty()) {
                    String querySearch = call.request().url().queryParameter("query");
                    callback.onFailure("No results found for " + (querySearch != null ? querySearch : "your search") + ".");
                    return;
                }

                AtomicInteger completedCount = new AtomicInteger(0);
                List<ComicVineDisplay> filteredResponse = Collections.synchronizedList(new ArrayList<>());

                for (ComicVine.Result result : filteredCharacters) {
                    ComicVineDisplay display = result.toDisplay();

                    getCharacterTeams(result.api_detail_url, apiKey, new ComicVineTeamsCallback() {
                        @Override
                        public void onSuccess(List<String> teamNames) {
                            display.teams = teamNames;
                            filteredResponse.add(display);

                            if (completedCount.incrementAndGet() == filteredCharacters.size()) {
                                callback.onSuccess(filteredResponse);
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            display.teams = new ArrayList<>();
                            filteredResponse.add(display);

                            if (completedCount.incrementAndGet() == filteredCharacters.size()) {
                                callback.onSuccess(filteredResponse);
                            }
                        }
                    });
                }

            }
        });
    }

    public static void getCharacterTeams(String detailUrl, String apiKey, ComicVineTeamsCallback callback) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl url = HttpUrl.parse(detailUrl).newBuilder()
                .addQueryParameter("api_key", apiKey)
                .addQueryParameter("format", "json")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "HeroicOrganizerApp/1.0")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Team fetch failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Team fetch failed: " + response.message());
                    return;
                }

                String json = response.body().string();
                Gson gson = new Gson();
                CharacterDetail detail = gson.fromJson(json, CharacterDetail.class);

                if (detail != null && detail.results != null && detail.results.teams != null) {
                    List<String> teamNames = new ArrayList<>();
                    for (CharacterDetail.Team team : detail.results.teams) {
                        teamNames.add(team.name);
                    }
                    callback.onSuccess(teamNames);
                } else {
                    callback.onSuccess(new ArrayList<>());
                }
            }
        });
    }
}
