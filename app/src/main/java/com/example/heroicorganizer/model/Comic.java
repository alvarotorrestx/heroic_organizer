package com.example.heroicorganizer.model;

public class Comic {
    private String title;
    private String issueNumber;
    private String coverImage;

    public Comic(String title, String issueNumber, String coverImage) {
        this.title = title;
        this.issueNumber = issueNumber;
        this.coverImage = coverImage;
    }

    // Getters
    public String getTitle() { return title; }
    public String getIssueNumber() { return issueNumber; }
    public String getCoverImage() { return coverImage; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setIssueNumber(String issueNumber) { this.issueNumber = issueNumber; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
}
