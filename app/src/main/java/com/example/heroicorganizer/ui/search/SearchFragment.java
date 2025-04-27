package com.example.heroicorganizer.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import com.example.heroicorganizer.*;
import com.example.heroicorganizer.model.LibraryComic;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText searchInput;
    private Button searchButton;
    private Button loadMoreButton;
    private Spinner publisherFilter;
    private Spinner yearFilter;
    private List<Comic> comicCache = new ArrayList<>();
    private List<LibraryComic> userLibrary = new ArrayList<>();
    private int offset = 0;
    private final int limit = 20;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        searchButton = view.findViewById(R.id.searchButton);
        loadMoreButton = view.findViewById(R.id.loadMoreButton);
        publisherFilter = view.findViewById(R.id.publisherFilter);
        yearFilter = view.findViewById(R.id.yearFilter);

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString();
            if (!query.isEmpty()) {
                offset = 0;
                searchComics(query, false);
            }
        });

        loadMoreButton.setOnClickListener(v -> searchComics(searchInput.getText().toString(), true));

        return view;
    }

    private void searchComics(String query, boolean isLoadMore) {
        ComicVineApi api = RetrofitClient.getInstance().create(ComicVineApi.class);
        String apiKey = ApiKeyProvider.getApiKey();
        String selectedPublisher = publisherFilter.getSelectedItem().toString();
        int selectedYear = Integer.parseInt(yearFilter.getSelectedItem().toString());

        api.searchComics(apiKey, query, "json", limit, offset, selectedPublisher, selectedYear).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Comic> comics = response.body().getResults();

                    if (!isLoadMore) {
                        comicCache.clear();
                    }

                    comicCache.addAll(comics);
                    offset += limit;

                    for (Comic comic : comics) {
                        Log.d("SearchResults", "Title: " + comic.getTitle());
                    }
                } else {
                    Log.e("APIError", "Failed to fetch comics: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("APIError", "API request failed", t);
            }
        });
    }

    private void addComicToLibrary(Comic selectedComic) {
        LibraryComic newComic = new LibraryComic(
                selectedComic.getTitle(),
                selectedComic.getIssueNumber(),
                selectedComic.getPublisher()
        );
        userLibrary.add(newComic);
        Log.d("Library", "Comic added: " + newComic.getTitle());
    }
}
