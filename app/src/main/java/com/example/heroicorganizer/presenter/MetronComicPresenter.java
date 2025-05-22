package com.example.heroicorganizer.presenter;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import com.example.heroicorganizer.callback.MetronComicCallback;
import com.example.heroicorganizer.model.MetronComic;
import com.example.heroicorganizer.utils.MetronApiConfig;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MetronComicPresenter {
    public static void apiCall(Context context, MetronComicCallback callback) {

        // Get today's date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar today = Calendar.getInstance();
        String startDate = sdf.format(today.getTime());
        // Add 2 weeks to today's date
        today.add(Calendar.DAY_OF_YEAR, 14);
        String endDate = sdf.format(today.getTime());

        // Compile query url for upcoming comics search
        String url = "https://metron.cloud/api/issue/?store_date_range_after=" + startDate +
                "&store_date_range_before=" + endDate;

        // Load credentials from api assets
        MetronApiConfig.load(context);

        String username = MetronApiConfig.getUsername();
        String password = MetronApiConfig.getPassword();

        // For authentication headers
        String credentials = username + ":" + password;
        String base64Credentials = Base64.encodeToString(credentials.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "HeroicOrganizerApp/1.0")
                .header("Authorization", "Basic " + base64Credentials)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();

                try {
                    JSONObject root = new JSONObject(responseBody);
                    JSONArray resultsArray = root.getJSONArray("results");

                    List<MetronComic> results = new ArrayList<>();

                    for (int i = 0; i < 4; i++) {
                        JSONObject obj = resultsArray.getJSONObject(i);
                        JSONObject seriesObj = obj.getJSONObject("series");

                        String title = seriesObj.optString("name");
                        String issueNumber = obj.optString("number");
                        String coverDate = obj.optString("cover_date");
                        String releaseDate = obj.optString("store_date");
                        String imageUrl = obj.optString("image");

                        MetronComic comic = new MetronComic(title, issueNumber, coverDate, releaseDate, imageUrl);
                        results.add(comic);
                    }

                    callback.onSuccess(results);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFailure("Parsing error: " + e.getMessage());
                }
            }
        });
    }
}
