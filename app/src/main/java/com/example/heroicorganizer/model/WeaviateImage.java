package com.example.heroicorganizer.model;

public class WeaviateImage {
    private String image;
    private String title;

    public WeaviateImage(String image, String title) {
        this.image = image;
        this.title = title;
    }

    // Getters
    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    // Setters
    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
