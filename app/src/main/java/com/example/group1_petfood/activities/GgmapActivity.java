package com.example.group1_petfood.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.group1_petfood.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GgmapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final LatLng LOCATION = new LatLng(10.841254037526637, 106.80986153890741);
    private final String ADDRESS = "FPT University HCMC 7 Đ. D1, Long Thạnh Mỹ, Thủ Đức, Hồ Chí Minh 700000";
//    private LocationManager locationManager;
//    private Marker userMarker;
//    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ggmap);
        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        // Set up address click listener
        TextView tvAddress = findViewById(R.id.tvAddress);
        tvAddress.setOnClickListener(v -> openGoogleMaps());
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add marker and move camera
        mMap.addMarker(new MarkerOptions().position(LOCATION).title("Physical store location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCATION, 15));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }
        //Method chuyen sang app google map
    private void openGoogleMaps() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(ADDRESS));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }
//    private void getUserLocation() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
//            return;
//        }
//        // Get last known location
//        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if (lastKnownLocation != null) {
//            updateUserLocation(lastKnownLocation);
//        }
//        // Request real-time location updates
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, new LocationListener() {
//            @Override
//            public void onLocationChanged(@NonNull Location location) {
//                updateUserLocation(location);
//            }
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {}
//            @Override
//            public void onProviderEnabled(String provider) {}
//            @Override
//            public void onProviderDisabled(String provider) {}
//        });
//    }
//
//    private void updateUserLocation(Location location) {
//        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//        if (userMarker == null) {
//            userMarker = mMap.addMarker(new MarkerOptions().position(userLatLng).title("Your Location"));
//        } else {
//            userMarker.setPosition(userLatLng);
//        }
//
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
//    }

}