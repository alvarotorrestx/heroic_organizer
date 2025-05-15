package com.example.heroicorganizer.ui.wishlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.heroicorganizer.R;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<WishlistItem> items;

    public WishlistAdapter(List<WishlistItem> items) {
        this.items = items;
    }

    @Override
    public WishlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_card, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WishlistViewHolder holder, int position) {
        WishlistItem item = items.get(position);
        holder.cardImage.setImageResource(item.imageResId);
        holder.cardTitle.setText(item.title + " " + item.issueNum);
        holder.cardVariant.setText(item.variant);
        holder.cardReleaseDate.setText("Release date: " + item.releaseDate);
        holder.cardCutoffDate.setText("Cut-off date: " + item.cutoffDate);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImage;
        TextView cardTitle, cardVariant, cardReleaseDate, cardCutoffDate;

        WishlistViewHolder(View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.cardImage);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardVariant = itemView.findViewById(R.id.cardVariant);
            cardReleaseDate = itemView.findViewById(R.id.cardReleaseDate);
            cardCutoffDate = itemView.findViewById(R.id.cardCutoffDate);
        }
    }
}
