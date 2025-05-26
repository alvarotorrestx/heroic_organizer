package com.example.heroicorganizer.ui.locator;

public class Store {
    private String name;
    private String address;
    private String placeId;
    private double latitude;
    private double longitude;
    private String phone;
    private String hours;
    private String notableComics;

    // Updated constructor to include phone, hours, and notable comics
    public Store(String name, String address, String placeId, double latitude, double longitude, String phone, String hours, String notableComics) {
        this.name = name;
        this.address = address;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.hours = hours;
        this.notableComics = notableComics;
    }

    // Getters for all fields
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPlaceId() { return placeId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getPhone() { return phone; }
    public String getHours() { return hours; }
    public String getNotableComics() { return notableComics; }
}

