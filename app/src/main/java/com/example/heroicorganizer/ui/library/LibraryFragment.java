package com.example.heroicorganizer.ui.library;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.model.Comic;
import com.example.heroicorganizer.model.ApiResponse;
import com.example.heroicorganizer.utils.ComicVineConfig;
import com.example.heroicorganizer.api.ComicVineApi;
import com.bumptech.glide.Glide;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.heroicorganizer.api.RetrofitClient;


public class LibraryFragment extends Fragment {

    private GridLayout folderContainer;
    private int offset = 0;
    private final int limit = 20;

    public LibraryFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        folderContainer = view.findViewById(R.id.folderContainer);

        // ✅ Display "Loading..." while API call is in progress
        TextView loadingText = new TextView(requireContext());
        loadingText.setText("Loading...");
        loadingText.setTextColor(getResources().getColor(android.R.color.white));
        loadingText.setTextSize(18);
        loadingText.setGravity(View.TEXT_ALIGNMENT_CENTER);
        folderContainer.addView(loadingText);

        // ✅ Fetch Comics using Retrofit
        searchComics("batman");
    }

    private void searchComics(String query) {
        String apiKey = ComicVineConfig.getApiKey(requireContext());
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e("API_ERROR", "API Key is missing!");
            return;
        }

        ComicVineApi api = RetrofitClient.getInstance().create(ComicVineApi.class);
        api.searchComics(apiKey, query, "json", limit, offset).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("API_DEBUG", "Response Code: " + response.code());
                folderContainer.removeAllViews(); // Remove "Loading..."

                if (response.isSuccessful() && response.body() != null) {
                    List<Comic> comics = response.body().getResults();
                    Log.d("API_DEBUG", "Comics Found: " + comics.size());

                    if (!comics.isEmpty()) {
                        populateComics(comics);
                        offset += limit; // ✅ Handle pagination automatically
                    } else {
                        displayNoResults();
                    }
                } else {
                    Log.e("API_ERROR", "Failed to fetch comics: " + response.message());
                    displayError(response);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", "API Call Failed: " + t.getMessage());
                displayNoResults();
            }
        });
    }

    private void populateComics(List<Comic> comics) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (Comic comic : comics) {
            View comicCard = inflater.inflate(R.layout.comic_card, folderContainer, false);
            ImageView coverImage = comicCard.findViewById(R.id.comicCoverImage);
            TextView comicTitle = comicCard.findViewById(R.id.comicTitle);
            TextView issueNumber = comicCard.findViewById(R.id.issueNumber);

            comicTitle.setText(comic.getTitle());
            issueNumber.setText("Issue #" + comic.getIssueNumber());

            if (comic.getCoverImage() != null && !comic.getCoverImage().isEmpty()) {
                Glide.with(requireContext()).load(comic.getCoverImage()).into(coverImage);
            } else {
                coverImage.setBackgroundColor(Color.parseColor("#FFBB86FC"));
            }

            folderContainer.addView(comicCard);
        }
    }

    private void displayNoResults() {
        TextView noResults = new TextView(requireContext());
        noResults.setText("No results found.");
        noResults.setTextColor(getResources().getColor(android.R.color.white));
        noResults.setTextSize(18);
        noResults.setGravity(View.TEXT_ALIGNMENT_CENTER);
        folderContainer.addView(noResults);
    }

    private void displayError(Response<ApiResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
            Log.e("API_ERROR", "Error Body: " + errorBody);
        } catch (Exception e) {
            Log.e("API_ERROR", "Error reading response body", e);
        }

        displayNoResults();
    }
}
