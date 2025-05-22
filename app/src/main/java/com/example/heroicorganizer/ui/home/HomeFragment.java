package com.example.heroicorganizer.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heroicorganizer.R;
import com.example.heroicorganizer.callback.WeaviateUploadCallback;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.LibraryComic;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.model.WeaviateImage;
import com.example.heroicorganizer.presenter.WeaviatePresenter;
import com.example.heroicorganizer.ui.ToastMsg;
import com.example.heroicorganizer.ui.wishlist.WishlistAdapter;
import com.example.heroicorganizer.ui.wishlist.WishlistData;
import com.example.heroicorganizer.ui.wishlist.WishlistItem;
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

    private final String TAG = "HomeFragment";

    public HomeFragment() {}

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

        viewMoreUpcomingBtn.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.nav_wishlist);
        });

        viewMoreRecentBtn.setOnClickListener(v -> {
            isRecentExpanded = !isRecentExpanded;
            updateRecentDisplay();
        });

        loadWishlistItems();
        loadRecentLibraryItems();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWishlistItems();
        loadRecentLibraryItems();
    }

    private void loadWishlistItems() {
        List<WishlistItem> items = WishlistData.itemList;

        for (WishlistItem item : items) {
            Log.d("WishlistItem", item.title + " " + item.issueNum);
        }

        WishlistAdapter adapter = new WishlistAdapter(items);
        upcomingRecycler.setAdapter(adapter);
    }

    private void loadRecentLibraryItems() {
        List<LibraryComic> recentItems = RecentComicsData.getComicList();

        for (LibraryComic item : recentItems) {
            Log.d("ComicItem", item.getTitle() + " " + item.getIssue());
        }

        RecentAdapter adapter = new RecentAdapter(recentItems, requireContext());
        recentRecycler.setAdapter(adapter);
    }

    private void updateRecentDisplay() {
//        List<LibraryComic> displayList;
//        if (!isRecentExpanded) {
//            displayList = allRecentComics.size() > 2 ? allRecentComics.subList(0, 2) : allRecentComics;
//            viewMoreRecentBtn.setText("View More");
//        } else {
//            displayList = allRecentComics;
//            viewMoreRecentBtn.setText("Show Less");
//        }
//        recentRecycler.setAdapter(new RecentAdapter(displayList, requireContext()));

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.example1);
//        String base64 = WeaviatePresenter.toBase64(bitmap);
//
//        WeaviateImage testImage = new WeaviateImage(base64, "", "Magik", "", "#4", "Rose Besch", "", "Apr/23/2025", "", "A Magik Comic.");
//        WeaviatePresenter.uploadWeaviateImage("Comic", "", testImage, new WeaviateUploadCallback() {
//            @Override
//            public void onSuccess(String message) {
//                Log.d("WeaviateImage", message);
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                Log.e("WeaviateImage", errorMessage);
//            }
//        });

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.example2);
//        String base64 = WeaviatePresenter.toBase64(bitmap);
//
//        WeaviateImage testImage = new WeaviateImage(base64, "Psylocke", "", "#8", "MG-#4", "Puppeteer Lee", "", "Jun/18/2025", "", "A Psylocke Comic.", "6e7e489f-6853-40e8-9c1c-40aac5684ce5");
//        WeaviatePresenter.uploadWeaviateImage("ComicVariant", testImage.getParentComic(), testImage, new WeaviateUploadCallback() {
//            @Override
//            public void onSuccess(String message) {
//                Log.d("WeaviateImage", message);
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                Log.e("WeaviateImage", errorMessage);
//            }
//        });

    }
}
