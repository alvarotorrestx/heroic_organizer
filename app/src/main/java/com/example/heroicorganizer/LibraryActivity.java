package com.example.heroicorganizer;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.heroicorganizer.model.LibraryComic;
import com.example.heroicorganizer.ApiKeyProvider;



public class LibraryActivity extends AppCompatActivity {

    private EditText searchInput;
    private Button searchButton;
    private Button loadMoreButton;
    private Spinner publisherFilter;
    private Spinner yearFilter;
    private List<Comic> comicCache = new ArrayList<>();
    private List<LibraryComic> userLibrary = new ArrayList<>();
    private int offset = 0;
    private final int limit = 20; // Load 20 results per page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library); // Ensure this layout exists

        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        loadMoreButton = findViewById(R.id.loadMoreButton);
        publisherFilter = findViewById(R.id.publisherFilter);
        yearFilter = findViewById(R.id.yearFilter);

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString();
            if (!query.isEmpty()) {
                offset = 0; // Reset pagination
                searchComics(query, false);
            }
        });

        loadMoreButton.setOnClickListener(v -> searchComics(searchInput.getText().toString(), true));
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
                        comicCache.clear(); // Overwrite previous search results
                    }

                    comicCache.addAll(comics);
                    offset += limit; // Move to next batch for pagination

                    // Log retrieved comics for debugging
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
