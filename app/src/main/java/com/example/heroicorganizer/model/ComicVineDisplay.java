package com.example.heroicorganizer.model;

import java.util.List;

public class ComicVineDisplay {
    public String id;
    public String name;
    public String deck;
    public String description;
    public String imageUrl;
    public String publisher;
    public String issueNumber;
    public List<String> teams;

    public ComicVineDisplay(String id, String name, String deck, String description, String imageUrl, String publisher, String issueNumber, List<String> teams) {
        this.id = id;
        this.name = name;
        this.deck = deck;
        this.description = description;
        this.imageUrl = imageUrl;
        this.publisher = publisher;
        this.issueNumber = issueNumber;
        this.teams = teams;
    }
}