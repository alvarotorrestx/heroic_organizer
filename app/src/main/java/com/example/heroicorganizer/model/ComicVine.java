package com.example.heroicorganizer.model;

import java.util.List;

public class ComicVine {
    public List<Result> results;

    public static class Result {
        public String id;
        public String name;
        public Image image;
        public String deck;
        public String description;
        public Publisher publisher;
        public FirstAppeared first_appeared_in_issue;

        public ComicVineDisplay toDisplay() {
            return new ComicVineDisplay(
                    id != null ? id : "",
                    name != null ? name : "",
                    deck != null ? deck : "",
                    description != null ? description : "",
                    image != null ? image.original_url : "",
                    publisher != null ? publisher.name : "Unknown",
                    first_appeared_in_issue != null ? first_appeared_in_issue.issue_number : "###"
            );
        }
    }

    public static class Image {
        public String original_url;
    }

    public static class Publisher {
        public String name;
    }

    public static class FirstAppeared {
        public String issue_number;
    }
}
