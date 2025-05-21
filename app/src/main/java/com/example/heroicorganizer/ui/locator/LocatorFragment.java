package com.example.heroicorganizer.ui.locator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.heroicorganizer.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class LocatorFragment extends Fragment implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    private GoogleMap googleMap;
    private double userLatitude, userLongitude;
    private ArrayList<Store> allStores = new ArrayList<>();

    public LocatorFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locator, container, false);

        // Request location permissions if needed
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Initialize Google Maps
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        populateStores();
        getUserLocation(view);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation(getView());
        } else {
            Log.e("LocatorFragment", "Location permission denied.");
        }
    }

    private void getUserLocation(View view) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    userLatitude = location.getLatitude();
                    userLongitude = location.getLongitude();
                    findNearbyStores(view);
                } else {
                    Log.e("LocatorFragment", "Location not found—requesting update.");
                    requestNewLocationData();
                }
            }).addOnFailureListener(e -> Log.e("LocatorFragment", "Error getting location: " + e.getMessage()));
        } else {
            Log.e("LocatorFragment", "Location permission missing—requesting permission.");
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void requestNewLocationData() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(2000)
                .setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, requireActivity().getMainLooper());
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) return;
            Location location = locationResult.getLastLocation();
            userLatitude = location.getLatitude();
            userLongitude = location.getLongitude();
            findNearbyStores(getView());
        }
    };

    private void populateStores() {
        allStores.add(new Store("Heroic Superstore", "123 Main St, Atlanta, GA", 33.7490, -84.3880));
        allStores.add(new Store("Guardian's Depot", "456 Elm St, Los Angeles, CA", 34.0522, -118.2437));
        allStores.add(new Store("Avenger's Supplies", "789 Oak St, New York, NY", 40.7128, -74.0060));
        allStores.add(new Store("Elite Gear Shop", "135 Maple Rd, Miami, FL", 25.7617, -80.1918));
        allStores.add(new Store("Superhero HQ", "902 Broadway, San Francisco, CA", 37.7749, -122.4194));
    }

    private void findNearbyStores(View view) {
        ArrayList<Store> nearbyStores = new ArrayList<>();
        for (Store store : allStores) {
            double distance = calculateDistance(userLatitude, userLongitude, store.getLatitude(), store.getLongitude());
            Log.d("LocatorFragment", "Store: " + store.getName() + " | Distance: " + distance + " miles");
            if (distance <= 50) { // Filter stores within 50 miles
                nearbyStores.add(store);
            }
        }
        displayNearbyStores(view, nearbyStores);
    }

    private void displayNearbyStores(View view, ArrayList<Store> stores) {
        ListView storeList = view.findViewById(R.id.storeList);
        ArrayList<String> storeDetails = new ArrayList<>();

        for (Store store : stores) {
            storeDetails.add(store.getName() + "\n" + store.getAddress());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, storeDetails);
        storeList.setAdapter(adapter);

        Log.d("LocatorFragment", "Displaying " + stores.size() + " stores in list.");
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 0.621371; // Convert km to miles
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        for (Store store : allStores) {
            LatLng storeLocation = new LatLng(store.getLatitude(), store.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(storeLocation).title(store.getName()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}

