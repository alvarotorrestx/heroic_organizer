package com.example.heroicorganizer.ui.locator;

public class Store {
    private String name;
    private String address;
    private String placeId;
    private double latitude;
    private double longitude;

    public Store(String name, String address, String placeId, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPlaceId() { return placeId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}

