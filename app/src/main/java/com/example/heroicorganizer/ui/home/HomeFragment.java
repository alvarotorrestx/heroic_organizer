package com.example.heroicorganizer.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heroicorganizer.R;
import com.example.heroicorganizer.callback.MetronComicCallback;
import com.example.heroicorganizer.callback.RecentComicsCallback;
import com.example.heroicorganizer.callback.WeaviateUploadCallback;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.LibraryComic;
import com.example.heroicorganizer.model.MetronComic;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.model.WeaviateImage;
import com.example.heroicorganizer.presenter.MetronComicPresenter;
import com.example.heroicorganizer.presenter.RecentComicsPresenter;
import com.example.heroicorganizer.presenter.WeaviatePresenter;
import com.example.heroicorganizer.ui.ToastMsg;
import com.example.heroicorganizer.ui.wishlist.WishlistAdapter;
import com.example.heroicorganizer.ui.wishlist.WishlistData;
import com.example.heroicorganizer.ui.wishlist.WishlistItem;
import com.example.heroicorganizer.utils.ViewStatus;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView upcomingRecycler, recentRecycler;
    private Button viewMoreUpcomingBtn, viewMoreRecentBtn;
    private List<LibraryComic> allRecentComics = new ArrayList<>();
    private boolean isRecentExpanded = false;
    private FrameLayout recentContainer;


    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        upcomingRecycler = view.findViewById(R.id.recycler_upcoming);
        recentRecycler = view.findViewById(R.id.recycler_recent);
        viewMoreUpcomingBtn = view.findViewById(R.id.btn_view_more_upcoming);
        viewMoreRecentBtn = view.findViewById(R.id.btn_view_more_recent);

        upcomingRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recentRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        FrameLayout upcomingContainer = view.findViewById(R.id.upcomingContainer);
        recentContainer = view.findViewById(R.id.recentContainer);

        // Clear recycler and show loading for upcoming releases
        upcomingContainer.removeAllViews();
        upcomingContainer.addView(ViewStatus.SetStatus(requireContext(), "Loading..."));


        viewMoreUpcomingBtn.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.nav_wishlist_fade);
        });

        viewMoreRecentBtn.setOnClickListener(v -> {
            isRecentExpanded = !isRecentExpanded;
            updateRecentDisplay();
        });

        // Hidden to show upcoming releases
        //        loadWishlistItems();
        loadRecentComics();

        MetronComicPresenter.apiCall(requireContext(), new MetronComicCallback() {
            @Override
            public void onSuccess(List<MetronComic> results) {
                requireActivity().runOnUiThread(() -> {
                    // Clear loading message and restore recycler with the returned results from Metron api
                    upcomingContainer.removeAllViews();
                    upcomingContainer.addView(upcomingRecycler);

                    upcomingRecycler.setAdapter(new MetronComicAdapter(results));
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                upcomingContainer.removeAllViews();
                upcomingContainer.addView(ViewStatus.SetStatus(requireContext(), "Failed to load upcoming releases."));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        // Hidden to show upcoming releases
//        loadWishlistItems();
        loadRecentComics();
    }

    private void loadWishlistItems() {
        List<WishlistItem> items = WishlistData.itemList;

        for (WishlistItem item : items) {
            Log.d("WishlistItem", item.title + " " + item.issueNum);
        }

        WishlistAdapter adapter = new WishlistAdapter(items);
        upcomingRecycler.setAdapter(adapter);
    }

    private void loadRecentComics() {
        User currentUser = new User();
        currentUser.setUid(FirebaseAuth.getInstance().getUid());

        recentContainer.removeAllViews();
        recentContainer.addView(ViewStatus.SetStatus(requireContext(), "Loading..."));

        RecentComicsPresenter.getRecentComics(currentUser, new RecentComicsCallback() {

            @Override
            public void onSuccess(List<LibraryComic> comics) {
                requireActivity().runOnUiThread(() -> {
                    recentContainer.removeAllViews();

                    if (comics.isEmpty()) {
                        recentContainer.addView(ViewStatus.SetStatus(requireContext(), "No recently saved comics."));
                    } else {
                        allRecentComics = comics;
                        updateRecentDisplay();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    recentContainer.removeAllViews();
                    recentContainer.addView(ViewStatus.SetStatus(requireContext(), "Failed to load recent comics."));
                    Log.e("RecentComics", errorMessage);
                });
            }
        });
    }

    private void updateRecentDisplay() {
        recentContainer.removeAllViews();
        recentContainer.addView(recentRecycler);

        List<LibraryComic> comicsToShow;

        if (isRecentExpanded || allRecentComics.size() <= 4) {
            comicsToShow = allRecentComics;
        } else {
            // By default show up to 4 comics
            comicsToShow = allRecentComics.subList(0, 4);
        }

        RecentAdapter adapter = new RecentAdapter(comicsToShow, requireContext());
        recentRecycler.setAdapter(adapter);

        // If recent comics size is less than 4, hide the View More button
        if (allRecentComics.size() <= 4) {
            viewMoreRecentBtn.setVisibility(View.GONE);
        } else { // Show the View More button -> Will show up to 10 comics
            viewMoreRecentBtn.setVisibility(View.VISIBLE);
            viewMoreRecentBtn.setText(isRecentExpanded ? "View Less" : "View More");
        }
    }

}
