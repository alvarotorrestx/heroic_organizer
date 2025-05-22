package com.example.heroicorganizer.ui.home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.model.MetronComic;

import java.util.List;

public class MetronComicAdapter extends RecyclerView.Adapter<MetronComicAdapter.MetronViewHolder> {

    private List<MetronComic> items;

    public MetronComicAdapter(List<MetronComic> items) {
        this.items = items;
    }

    @Override
    public MetronViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_card, parent, false);
        return new MetronViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MetronViewHolder holder, int position) {
        MetronComic item = items.get(position);

        // Load image from url
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .into(holder.cardImage);

        holder.cardTitle.setText(item.getTitle() + " #" + item.getIssueNumber());
        holder.cardVariant.setText("Upcoming Release");
        holder.cardReleaseDate.setText("Release date: " + item.getReleaseDate());
        holder.cardCutoffDate.setText("Cover date: " + item.getCoverDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MetronViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImage;
        TextView cardTitle, cardVariant, cardReleaseDate, cardCutoffDate;

        MetronViewHolder(View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.cardImage);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardVariant = itemView.findViewById(R.id.cardVariant);
            cardReleaseDate = itemView.findViewById(R.id.cardReleaseDate);
            cardCutoffDate = itemView.findViewById(R.id.cardCutoffDate);
        }
    }
}