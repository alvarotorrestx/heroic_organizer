package com.example.heroicorganizer.ui.wishlist;

public class WishlistItem {
    public int imageResId;
    public String title;
    public String issueNum;
    public String variant;
    public String releaseDate;
    public String cutoffDate;

    public WishlistItem(int imageResId, String title, String issueNum, String variant, String releaseDate, String cutoffDate) {
        this.imageResId = imageResId;
        this.title = title;
        this.issueNum = issueNum;
        this.variant = variant;
        this.releaseDate = releaseDate;
        this.cutoffDate = cutoffDate;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

}
