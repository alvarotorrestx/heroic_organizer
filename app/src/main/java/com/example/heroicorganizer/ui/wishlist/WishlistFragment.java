package com.example.heroicorganizer.ui.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heroicorganizer.R;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    public WishlistFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.wishlistRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        List<WishlistItem> itemList = new ArrayList<>();
        itemList.add(new WishlistItem(R.drawable.example4, "Marvel Rivals", "#1", "Peach Momoko", "Apr/2/2025", ""));
        itemList.add(new WishlistItem(R.drawable.example1, "Magik", "#4", "Rose Besch", "Apr/23/2025", "Mar/24/2025"));
        itemList.add(new WishlistItem(R.drawable.example3, "Jeff the Land Shark", "#1", "Todd Nauck Homage", "Jun/18/2025", "May/05/2026"));
        itemList.add(new WishlistItem(R.drawable.example2, "Psylocke", "#8", "Puppeteer Lee", "Jun/18/2025", "May/19/2025"));

        WishlistAdapter adapter = new WishlistAdapter(itemList);
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
