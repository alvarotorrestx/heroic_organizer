package com.example.heroicorganizer.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.heroicorganizer.*;
import com.example.heroicorganizer.callback.LibraryFolderCallback;
import com.example.heroicorganizer.model.LibraryComic;
import com.example.heroicorganizer.model.LibraryFolder;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.presenter.LibraryFolderPresenter;
import com.example.heroicorganizer.ui.ToastMsg;
import com.example.heroicorganizer.utils.ComicVineConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText searchQuery = view.findViewById(R.id.searchQuery);
        final Button searchComics = view.findViewById(R.id.searchComics);

        User currentUser = new User();
        currentUser.setUid(FirebaseAuth.getInstance().getUid());

        searchComics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchQuery.getText().toString().trim();
                if (query.isEmpty()) {
                    ToastMsg.show(requireContext(), "Please enter a search term");
                    return;
                }

                String baseUrl = "https://comicvine.gamespot.com/api/search/";
                String apiKey = ComicVineConfig.getApiKey(requireContext());
                if (apiKey == null || apiKey.isEmpty()) {
                    ToastMsg.show(requireContext(), "API Key missing");
                    return;
                }
                String finalUrl = baseUrl + "?api_key=" + apiKey + "&query=" + query + "&format=json";

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(finalUrl)
                        .addHeader("User-Agent", "HeroicOrganizerApp/1.0")
                        .build();

                Log.d("SearchComics", query);
                Log.d("SearchComics", request.toString());
                Log.d("SearchComics", finalUrl);

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        Log.e("SearchComics", "API Request Failed", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = response.body().string();

                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            JsonElement jsonElement = JsonParser.parseString(jsonResponse);
                            String prettyJson = gson.toJson(jsonElement);

                            Log.d("SearchComics", "Search Results: " + prettyJson);
                        } else {
                            Log.e("SearchComics", "API Response Failed: " + response.message());
                        }
                    }
                });
            }
        });
    }
}
