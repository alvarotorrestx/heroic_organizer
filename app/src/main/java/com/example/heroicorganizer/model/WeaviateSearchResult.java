package com.example.heroicorganizer.model;

import java.util.List;

public class WeaviateSearchResult {
    private String title;
    private String image;
    private String issue_number;
    private String publisher_names;
    private String cover_artist;
    private String author;
    private String date_published;
    private String upc;
    private String description;
    private String parent_comic_title;
    private String parent_comic_id;
    private String parent_comic_issue_number;
    private List<String> variants;
    private String variantId;
    private String variantIssueNumber;
    private String variantTitle;

    public WeaviateSearchResult() {}

    // Getters
    public String getTitle() {
        return title;
    }
    public String getImage() {
        return image;
    }
    public String getIssueNumber() {
        return issue_number;
    }
    public String getPublisherNames() {
        return publisher_names;
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
    public String getParentComicTitle() {
        return parent_comic_title;
    }
    public String getParentComicId() {
        return parent_comic_id;
    }
    public String getParentComicIssueNumber() {
        return parent_comic_issue_number;
    }
    public List<String> getVariants() {
        return variants;
    }
    public String getVariantId() {
        return variantId;
    }
    public String getVariantIssueNumber() {
        return variantIssueNumber;
    }
    public String getVariantTitle() {
        return variantTitle;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setIssueNumber(String issue_number) {
        this.issue_number = issue_number;
    }
    public void setPublisherNames(String publisher_names) {
        this.publisher_names = publisher_names;
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
    public void setParentComicTitle(String parent_comic_title) {
        this.parent_comic_title = parent_comic_title;
    }
    public void setParentComicId(String parent_comic_id) {
        this.parent_comic_id = parent_comic_id;
    }
    public void setParentComicIssueNumber(String parent_comic_issue_number) {
        this.parent_comic_issue_number = parent_comic_issue_number;
    }
    public void setVariants(List<String> variants) {
        this.variants = variants;
    }
    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }
    public void setVariantIssueNumber(String variantIssueNumber) {
        this.variantIssueNumber = variantIssueNumber;
    }
    public void setVariantTitle(String variantTitle) {
        this.variantTitle = variantTitle;
    }
}
