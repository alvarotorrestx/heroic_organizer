package com.example.heroicorganizer.ui.locator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.heroicorganizer.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LocatorFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<Store> allStores = new ArrayList<>();
    private EditText zipCodeInput, radiusInput;
    private Button searchButton;

    private static final String API_KEY = "AIzaSyBhlhhCDvfyAT1t2g3Gzrv0hZJxl5mIboA"; // Replace securely

    public LocatorFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locator, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        zipCodeInput = view.findViewById(R.id.zipCodeInput);
        radiusInput = view.findViewById(R.id.radiusInput);
        searchButton = view.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(v -> searchForStores());

        return view;
    }

    private void searchForStores() {
        String zipCode = zipCodeInput.getText().toString();
        String radius = radiusInput.getText().toString();

        if (zipCode.isEmpty() || radius.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both ZIP code and radius.", Toast.LENGTH_SHORT).show();
            return;
        }

        getCoordinatesFromZip(zipCode, Integer.parseInt(radius));
    }

    private void getCoordinatesFromZip(String zipCode, int radius) {
        new Thread(() -> {
            try {
                String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + zipCode + "&key=" + API_KEY;
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JSONObject response = new JSONObject(json.toString());
                JSONObject location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");

                fetchNearbyStores(latitude, longitude, radius);

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error retrieving location. Check ZIP code.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void fetchNearbyStores(double latitude, double longitude, int radius) {
        new Thread(() -> {
            try {
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                        "location=" + latitude + "," + longitude +
                        "&radius=" + (radius * 1609) + // Convert miles to meters
                        "&type=book_store&keyword=comic&key=" + API_KEY;

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JSONObject response = new JSONObject(json.toString());
                JSONArray results = response.getJSONArray("results");

                allStores.clear();
                for (int i = 0; i < results.length(); i++) {
                    JSONObject store = results.getJSONObject(i);
                    String name = store.getString("name");
                    String address = store.getString("vicinity");
                    String placeId = store.optString("place_id", ""); // Ensure `placeId` is retrieved
                    double storeLat = store.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    double storeLng = store.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                    allStores.add(new Store(name, address, placeId, storeLat, storeLng));
                }

                requireActivity().runOnUiThread(() -> {
                    displayNearbyStores();
                    populateMap();
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error retrieving stores.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void displayNearbyStores() {
        ListView storeList = requireView().findViewById(R.id.storeList);
        ArrayList<String> storeDetails = new ArrayList<>();
        for (Store store : allStores) {
            storeDetails.add(store.getName() + "\n" + store.getAddress());
        }

        storeList.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, storeDetails));
    }

    private void populateMap() {
        if (googleMap == null) return;

        for (Store store : allStores) {
            LatLng storeLocation = new LatLng(store.getLatitude(), store.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(storeLocation).title(store.getName()));
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }
}

