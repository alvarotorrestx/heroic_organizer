package com.example.heroicorganizer.ui.search;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.heroicorganizer.*;
import com.example.heroicorganizer.callback.LibraryFolderCallback;
import com.example.heroicorganizer.model.LibraryComic;
import com.example.heroicorganizer.model.LibraryFolder;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.presenter.LibraryFolderPresenter;
import com.example.heroicorganizer.ui.ToastMsg;
import com.example.heroicorganizer.ui.comic.ComicDetailFragment;
import com.example.heroicorganizer.utils.ComicVineConfig;
import com.example.heroicorganizer.utils.ViewStatus;
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

                final GridLayout comicResultsContainer = view.findViewById(R.id.comicResultsContainer);

                LayoutInflater inflater = LayoutInflater.from(getContext());

                // Shows "Loading... message to user (UX)
                comicResultsContainer.addView(ViewStatus.SetStatus(requireContext(), "Loading..."));

                String baseUrl = "https://comicvine.gamespot.com/api/search/";
                String apiKey = ComicVineConfig.getApiKey(requireContext());
                if (apiKey == null || apiKey.isEmpty()) {
                    // Removes Loading...
                    comicResultsContainer.removeAllViews();
                    ToastMsg.show(requireContext(), "API Key missing");
                    return;
                }
                String finalUrl = baseUrl + "?api_key=" + apiKey + "&query=" + query + "&format=json";

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(finalUrl)
                        .addHeader("User-Agent", "HeroicOrganizerApp/1.0")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        requireActivity().runOnUiThread(() -> {
                            // Removes Loading...
                            comicResultsContainer.removeAllViews();
                            Log.e("SearchComics", "API Request Failed", e);
                            ToastMsg.show(requireContext(), "API Request Failed");
                        });
                    }


                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String jsonResponse = response.body().string();

                        if (response.isSuccessful() && response.body() != null) {
                            Gson gson = new Gson();
                            ApiResponse apiResponse = gson.fromJson(jsonResponse, ApiResponse.class);

                            requireActivity().runOnUiThread(() -> {
                                // Removes Loading...
                                comicResultsContainer.removeAllViews();

                                // TODO: Temporary limit on search result until we have a better design or pagination
                                int limit = Math.min(apiResponse.results.size(), 10);
                                for (int i = 0; i < limit; i++) {
                                    View comicCard = inflater.inflate(R.layout.comic_card, comicResultsContainer, false);

                                    // Display details on initial search response comic
                                    ImageView coverImage = comicCard.findViewById(R.id.comicResultCoverImage);
                                    TextView comicTitle = comicCard.findViewById(R.id.comicResultName);
                                    TextView comicDeck = comicCard.findViewById(R.id.comicResultDeck);

                                    comicTitle.setText(apiResponse.results.get(i).name);
                                    comicDeck.setText(apiResponse.results.get(i).deck);

                                    Glide.with(requireContext())
                                            .load(apiResponse.results.get(i).image.screen_url)
                                            .into(coverImage);

                                    comicResultsContainer.addView(comicCard);

                                    // Bundle responses to push to the ComicDetailFragment
                                    Result comic = apiResponse.results.get(i);

                                    comicCard.setOnClickListener(v -> {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("id", comic.id != null ? comic.id : "");
                                        bundle.putString("title", comic.name != null ? comic.name : "");
                                        bundle.putString("deck", comic.deck != null ? comic.deck : "");
                                        bundle.putString("description", comic.description != null ? comic.description : "");
                                        bundle.putString("image", comic.image != null ? comic.image.screen_url : "");
                                        bundle.putString("publishers", comic.publisher != null ? comic.publisher.name : "Unknown");
                                        bundle.putString("issueNumber", comic.first_appeared_in_issue != null ? comic.first_appeared_in_issue.issue_number : "Unknown");

                                        // Navigate to ComicDetailFragment with the Bundle
                                        ComicDetailFragment comicDetailFragment = new ComicDetailFragment();
                                        comicDetailFragment.setArguments(bundle);

                                        requireActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.search_fragment_container, comicDetailFragment)
                                                .addToBackStack(null)
                                                .commit();
                                    });
                                }
                            });
                        } else {
                            Log.e("SearchComics", "API Response Failed: " + response.message());
                            ToastMsg.show(requireContext(), "API Response Failed");
                        }
                    }
                });
            }
        });
    }

    // TODO: Temporary spot for testing purposes
    private static class ApiResponse {
        List<Result> results;
    }

    private static class Result {
        String id;
        String name;
        Image image;
        String deck;
        String description;
        Publisher publisher;
        FirstAppeared first_appeared_in_issue;
    }

    private static class Image {
        String screen_url;
    }
    private static class Publisher {
        String name;
    }

    private static class FirstAppeared {
        String issue_number;
    }
}
