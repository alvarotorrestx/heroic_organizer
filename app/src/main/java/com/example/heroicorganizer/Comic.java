package com.example.heroicorganizer;

public class Comic {
    private String title;
    private String issueNumber;
    private String publisher;

    public Comic(String title, String issueNumber, String publisher) {
        this.title = title;
        this.issueNumber = issueNumber;
        this.publisher = publisher;
    }

    public String getTitle() { return title; }
    public String getIssueNumber() { return issueNumber; }
    public String getPublisher() { return publisher; }
}
