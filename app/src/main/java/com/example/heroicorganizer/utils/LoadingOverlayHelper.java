package com.example.heroicorganizer.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.heroicorganizer.R;

public class LoadingOverlayHelper {

    public static View showLoading(View rootView) {
        ViewGroup root = (ViewGroup) rootView; // rootView is your fragment's root
        LayoutInflater inflater = LayoutInflater.from(rootView.getContext());
        View overlay = inflater.inflate(R.layout.view_loading_overlay, root, false);
        overlay.setTag("loadingOverlay");

        ImageView gifView = overlay.findViewById(R.id.loadingGif);
        Glide.with(rootView.getContext())
                .asGif()
                .load(R.drawable.loading_animation)
                .skipMemoryCache(true)
                .into(gifView);

        root.addView(overlay);
        return overlay;
    }

    public static void hideLoading(View rootView) {
        ViewGroup root = (ViewGroup) rootView;
        View overlay = root.findViewWithTag("loadingOverlay");
        if (overlay != null) {
            ImageView gifView = overlay.findViewById(R.id.loadingGif);
            Glide.with(rootView.getContext()).clear(gifView);
            root.removeView(overlay);
        }
    }
}