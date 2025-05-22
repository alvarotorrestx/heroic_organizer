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
        //loadRecentLibraryItems();
        RecentComicsData.loadComicList(requireContext());
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
//        List<LibraryComic> recentItems = RecentComicsData.getComicList();
//
//        for (LibraryComic item : recentItems) {
//            Log.d("ComicItem", item.getTitle() + " " + item.getIssue());
//        }
//
//        RecentAdapter adapter = new RecentAdapter(recentItems, requireContext());
//        recentRecycler.setAdapter(adapter);
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

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.batman_beyond_return_of_the_joker);
//        String base64 = WeaviatePresenter.toBase64(bitmap);
//
//        WeaviateImage testImage = new WeaviateImage(base64, "", "Batman : Return of the Joker", "", "#1", "Craig Rousseau, Rob Leigh", "Darren Vincenzo", "Dec 13, 2000", "76194122581400111", "We've all learned that in the future Gotham City of BATMAN BEYOND, young Terry McGinnis has assumed the mantle of the bat. As the new Batman and protector of Gotham, he's had to face many strange new villains and even more bizarre challenges. However, nothing could have prepared him for the return of original Batman's greatest foe, the Joker! What happens when the Clown Prince of Crime lets loose in a future that seems all new and ready for the taking? More importantly: how has he seemingly returned from the dead?");
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

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.absolute_carnage);
//        String base64 = WeaviatePresenter.toBase64(bitmap);
//
//        WeaviateImage testImage = new WeaviateImage(base64, "", "Absolute Carnage", "", "#1", "Ryan Stegman, J.P. Mayer, Frank Martin", "Donny Cates, Clay McLeod Chapman", "Aug 7, 2019", "75960609413400111", "After turning Venom's world upside down a year ago, Donny Cates and Ryan Stegman are about to put the Sinister Symbiote through hell again, only this time Carnage has come calling, and everyone who's ever worn a symbiote is dead in his sights! He's skirted the periphery of the Marvel Universe for months, but Cletus Kasady at last stands poised to make his grand return to New York in a blistering 60-page story... and he wants to paint the town red!");
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

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.batman_beyond_six_flags);
//        String base64 = WeaviatePresenter.toBase64(bitmap);
//
//        WeaviateImage testImage = new WeaviateImage(base64, "Batman Beyond", "", "#1", "", "", "Hilary J. Bader", "Mar 1, 1999", "", "Six Flags Giveaway Variant - An adaptation of Batman Beyond Season 1, Episode 1: Rebirth, Part One.", "f96a8afc-bf57-4d43-8fbc-043e4da0af57");
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

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.absolute_carnage);
//        String base64 = WeaviatePresenter.toBase64(bitmap);
//
//        WeaviateImage testImage = new WeaviateImage(base64, "Absolute Carnage", "", "#1", "", "Ryan Stegman, J.P. Mayer, Frank Martin", "Donny Cates, Clay McLeod Chapman", "Aug 7, 2019", "75960609413400111", "After turning Venom's world upside down a year ago, Donny Cates and Ryan Stegman are about to put the Sinister Symbiote through hell again, only this time Carnage has come calling, and everyone who's ever worn a symbiote is dead in his sights! He's skirted the periphery of the Marvel Universe for months, but Cletus Kasady at last stands poised to make his grand return to New York in a blistering 60-page story... and he wants to paint the town red!", "5b487042-73e4-43e0-8029-d6d3d6cbf3d1");
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
