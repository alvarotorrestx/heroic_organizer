package com.example.heroicorganizer.model;

public class WeaviateImage {
    private String image;
    private String comic_id;
    private String title;
    private String publishers;
    private String issue_number;
    private String variant_id;
    private String cover_artist;
    private String author;
    private String date_published;
    private String upc;
    private String description;
    private String parent_comic;

    // Comic Constructor
    public WeaviateImage(String image, String comic_id, String title, String publishers, String issue_number, String cover_artist, String author, String date_published, String upc, String description) {
        this.image = image;
        this.comic_id = comic_id;
        this.title = title;
        this.publishers = publishers;
        this.issue_number = issue_number;
        this.cover_artist = cover_artist;
        this.author = author;
        this.date_published = date_published;
        this.upc = upc;
        this.description = description;
    }

    // ComicVariant Constructor
    public WeaviateImage(String image, String title, String publishers, String issue_number, String variant_id, String cover_artist, String author, String date_published, String upc, String description, String parent_comic) {
        this.image = image;
        this.title = title;
        this.publishers = publishers;
        this.issue_number = issue_number;
        this.variant_id = variant_id;
        this.cover_artist = cover_artist;
        this.author = author;
        this.date_published = date_published;
        this.upc = upc;
        this.description = description;
        this.parent_comic = parent_comic;
    }

    // Getters
    public String getImage() {
        return image;
    }
    public String getComicId() {
        return comic_id;
    }

    public String getTitle() {
        return title;
    }

    public String getPublishers() {
        return publishers;
    }

    public String getIssueNumber() {
        return issue_number;
    }

    public String getVariantId() {
        return variant_id;
    }

    public String getCoverArtist() {
        return cover_artist;
    }

    public String getAuthor() {
        return author;
    }

    public String getDatePublished() {
        return date_published;
    }

    public String getUpc() {
        return upc;
    }

    public String getDescription() {
        return description;
    }

    public String getParentComic() {
        return parent_comic;
    }

    // Setters
    public void setImage(String image) {
        this.image = image;
    }
    public void setComicId(String id) {
        this.comic_id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setPublishers(String publishers) {
        this.publishers = publishers;
    }
    public void setIssueNumber(String issue_number) {
        this.issue_number = issue_number;
    }
    public void setVariantId(String variant_id) {
        this.variant_id = variant_id;
    }
    public void setCoverArtist(String cover_artist) {
        this.cover_artist = cover_artist;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setDatePublished(String date_published) {
        this.date_published = date_published;
    }
    public void setUpc(String upc) {
        this.upc = upc;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setParentComic(String parent_comic) {
        this.parent_comic = parent_comic;
    }
}
