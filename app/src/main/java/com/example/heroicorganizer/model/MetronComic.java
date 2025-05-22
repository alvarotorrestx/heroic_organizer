package com.example.heroicorganizer.model;

public class MetronComic {
    private String title;
    private String issueNumber;
    private String coverDate;
    private String releaseDate;
    private String imageUrl;

    public MetronComic(String title, String issueNumber, String coverDate, String releaseDate,  String imageUrl) {
        this.title = title;
        this.issueNumber = issueNumber;
        this.coverDate = coverDate;
        this.releaseDate = releaseDate;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getTitle() {
        return title;
    }
    public String getIssueNumber() {
        return issueNumber;
    }
    public String getCoverDate() {
        return coverDate;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }
    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }
    public void setCoverDate(String coverDate) {
        this.coverDate = coverDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
