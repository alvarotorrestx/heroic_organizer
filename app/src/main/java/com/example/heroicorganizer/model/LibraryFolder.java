package com.example.heroicorganizer.model;

import java.util.HashMap;
import java.util.Map;

public class LibraryFolder {
    // Folder fields
    private String id;
    private String name;
    private String description;
    private String coverImage;
    private String colorTag;

    private static final String DEFAULT_COLOR_TAG = "#FFBB86FC";

    public LibraryFolder() {}

    public LibraryFolder(String id, String name, String description, String coverImage, String colorTag) {

        if (id == null || name == null) {
            throw new IllegalArgumentException("id and name are required.");
        }

        // If no image, fallback to colorTag
        if (coverImage == null && colorTag == null) {
            throw new IllegalArgumentException("Either coverImage or colorTag must be provided.");
        }

        this.id = id;
        this.name = name;
        this.description = description;
        this.coverImage = coverImage;
        this.colorTag = colorTag != null ? colorTag : DEFAULT_COLOR_TAG;
    }

    // Convert folder object for Firestore presenter
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("description", description);
        map.put("coverImage", coverImage);
        map.put("colorTag", colorTag);
        return map;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getColorTag() {
        return colorTag;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public void setColorTag(String colorTag) {
        this.colorTag = colorTag;
    }
}
