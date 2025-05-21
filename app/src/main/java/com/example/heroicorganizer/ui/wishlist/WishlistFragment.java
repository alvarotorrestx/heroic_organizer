package com.example.heroicorganizer.ui.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heroicorganizer.R;
import com.example.heroicorganizer.ui.SortingFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        List<WishlistItem> itemList = WishlistData.getItemList();

        WishlistAdapter adapter = new WishlistAdapter(itemList);
        recyclerView.setAdapter(adapter);

        Button btnFilter = rootView.findViewById(R.id.btn_open_filter);

        // Sets a dummy fab floating button to prevent crash
        // Can be adapted
        FloatingActionButton dummyFab = rootView.findViewById(R.id.dummy_fab);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortingFragment dialogFrag = SortingFragment.newInstance();
                dialogFrag.setParentFab(dummyFab);
                dialogFrag.show(getParentFragmentManager(), dialogFrag.getTag());
            }
        });



        return rootView;
    }
}
