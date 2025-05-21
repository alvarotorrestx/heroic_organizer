package com.example.heroicorganizer.ui.locator;

public class Store {
    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public Store(String name, String address, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}

