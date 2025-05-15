package com.example.heroicorganizer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryComic {
    private String id;
    private String upc;
    private String barcode;
    private String parentId;
    private String variant;
    private String title;
    private String description;
    private String deck;
    private String publicationYear;
    private String releaseDate;
    private String issue;
    private List<String> publishers;
    private String publisher;
    private List<String> authors;
    private List<String> illustrators;
    private List<String> tags;
    private String folder;
    private String notes;
    private String coverImage;
    private String condition;
    private int quantity;
    private long timestamp;
    public LibraryComic() {
    }

    public LibraryComic(String id, String title, String description, String deck, String publisher, String issue, String coverImage) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deck = deck;
        this.publisher = publisher;
        this.issue = issue;
        this.coverImage = coverImage;
    }

    public LibraryComic(String id, String upc, String barcode, String parentId, String variant, String title,
                        String publicationYear, String releaseDate, String issue, List<String> publishers,
                        List<String> authors, List<String> illustrators, List<String> tags, String folder,
                        String notes, String coverImage, String condition, int quantity) {

        if (id == null || (upc == null && barcode == null) || title == null || issue == null || folder == null) {
            throw new IllegalArgumentException("id, upc, title, and issue are all required.");
        }

        if (upc != null && !upc.matches("\\d{12}")) {
            throw new IllegalArgumentException("UPC must be a 12-digit number.");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        this.id = id;
        this.upc = upc;
        this.barcode = barcode;
        this.parentId = parentId;
        this.variant = variant;
        this.title = title;
        this.publicationYear = publicationYear;
        this.releaseDate = releaseDate;
        this.issue = issue;
        this.publishers = publishers != null ? publishers : new ArrayList<>();
        this.authors = authors != null ? authors : new ArrayList<>();
        this.illustrators = illustrators != null ? illustrators : new ArrayList<>();
        this.tags = tags != null ? tags : new ArrayList<>();
        this.folder = folder;
        this.notes = notes;
        this.coverImage = coverImage;
        this.condition = condition;
        this.quantity = quantity;
    }

    // Convert to a Map for Firestore or database storage
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("upc", upc);
        map.put("barcode", barcode);
        map.put("parentId", parentId);
        map.put("variant", variant);
        map.put("title", title);
        map.put("description", description);
        map.put("deck", deck);
        map.put("publicationYear", publicationYear);
        map.put("releaseDate", releaseDate);
        map.put("issue", issue);
        map.put("publisher", publisher);
        map.put("publishers", publishers);
        map.put("authors", authors);
        map.put("illustrators", illustrators);
        map.put("tags", tags);
        map.put("folder", folder);
        map.put("notes", notes);
        map.put("coverImage", coverImage);
        map.put("condition", condition);
        map.put("quantity", quantity);
        return map;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUpc() {
        return upc;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getParentId() {
        return parentId;
    }

    public String getVariant() {
        return variant;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDeck() {
        return deck;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getIssue() {
        return issue;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public String getPublisher() {
        return publisher;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getIllustrators() {
        return illustrators;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getFolder() {
        return folder;
    }

    public String getNotes() {
        return notes;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getCondition() {
        return condition;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public void setIllustrators(List<String> illustrators) {
        this.illustrators = illustrators;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
