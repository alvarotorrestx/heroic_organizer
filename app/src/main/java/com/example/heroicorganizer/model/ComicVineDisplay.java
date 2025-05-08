package com.example.heroicorganizer.model;

public class ComicVineDisplay {
    public String id;
    public String name;
    public String deck;
    public String description;
    public String imageUrl;
    public String publisher;
    public String issueNumber;

    public ComicVineDisplay(String id, String name, String deck, String description, String imageUrl, String publisher, String issueNumber) {
        this.id = id;
        this.name = name;
        this.deck = deck;
        this.description = description;
        this.imageUrl = imageUrl;
        this.publisher = publisher;
        this.issueNumber = issueNumber;
    }
}
