package com.example.heroicorganizer.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.example.heroicorganizer.*;
import com.example.heroicorganizer.callback.ComicVineCallback;
import com.example.heroicorganizer.model.ComicVine;
import com.example.heroicorganizer.model.ComicVineDisplay;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.presenter.ComicVinePresenter;
import com.example.heroicorganizer.ui.ToastMsg;
import com.example.heroicorganizer.utils.ComicVineConfig;
import com.example.heroicorganizer.utils.ViewStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
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
                comicResultsContainer.removeAllViews();
                comicResultsContainer.addView(ViewStatus.SetStatus(requireContext(), "Loading..."));

                ComicVinePresenter.searchComics(requireContext(), query, new ComicVineCallback() {
                    @Override
                    public void onSuccess(List<ComicVineDisplay> results) {
                        requireActivity().runOnUiThread(() -> {
                            //Removes Loading...
                            comicResultsContainer.removeAllViews();

                            // TODO: Temporary limit on search result until we have a better design or pagination
                            int limit = Math.min(results.size(), 10);
                            for (int i = 0; i < limit; i++) {
                                ComicVineDisplay comic = results.get(i);

                                View comicCard = inflater.inflate(R.layout.item_card, comicResultsContainer, false);

                                // Display details on initial search response comic
                                ImageView coverImage = comicCard.findViewById(R.id.itemCoverImage);
                                TextView comicTitle = comicCard.findViewById(R.id.itemTitle);
                                TextView comicDeck = comicCard.findViewById(R.id.itemSubtitle);

                                comicTitle.setText(comic.name);
                                comicDeck.setText(comic.deck);

                                Glide.with(requireContext())
                                        .load(comic.imageUrl)
                                        .into(coverImage);

                                // Bundle responses to push to the ComicDetailFragment
                                comicCard.setOnClickListener(v -> {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("id", comic.id);
                                    bundle.putString("title", comic.name);
                                    bundle.putString("deck", comic.deck);
                                    bundle.putString("description", comic.description);
                                    bundle.putString("image", comic.imageUrl);
                                    bundle.putString("publishers", comic.publisher);
                                    bundle.putString("issueNumber", comic.issueNumber);

                                    // navigate to sub-level fragment logic
                                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                                    navController.navigate(R.id.nav_add_to_library, bundle);
                                });

                                comicResultsContainer.addView(comicCard);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        requireActivity().runOnUiThread(() -> {
                            comicResultsContainer.removeAllViews();
                            comicResultsContainer.addView(ViewStatus.SetStatus(requireContext(), "No results found for " + query + "."));
                            ToastMsg.show(requireContext(), errorMessage);
                        });
                    }
                });
            }
        });
    }
}
