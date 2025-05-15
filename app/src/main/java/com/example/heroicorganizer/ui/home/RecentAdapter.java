package com.example.heroicorganizer.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.model.LibraryComic;

import java.util.List;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private List<LibraryComic> recentList;
    private Context context;

    public RecentAdapter(List<LibraryComic> recentList, Context context) {
        this.recentList = recentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wishlist_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LibraryComic comic = recentList.get(position);

        holder.title.setText(comic.getTitle() != null ? comic.getTitle() : "Unknown Title");
        holder.releaseDate.setText("Added: " + comic.getTimestamp());
        holder.cutoffDate.setText("");

        if (comic.getCoverImage() != null && !comic.getCoverImage().isEmpty()) {
            Glide.with(context).load(comic.getCoverImage()).into(holder.coverImage);
        }
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView title, releaseDate, cutoffDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.cardImage);
            title = itemView.findViewById(R.id.cardTitle);
            releaseDate = itemView.findViewById(R.id.cardReleaseDate);
            cutoffDate = itemView.findViewById(R.id.cardCutoffDate);
        }
    }
}
