package com.example.group1_petfood.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.adapters.LocationAdapter;
import com.example.group1_petfood.controllers.CartController;
import com.example.group1_petfood.fragments.CartDialogFragment;
import com.example.group1_petfood.models.StoreLocation;
import com.example.group1_petfood.utils.ToolbarHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class GgmapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final LatLng LOCATION = new LatLng(10.841254037526637, 106.80986153890741);
    private final String ADDRESS = "FPT University HCMC 7 Đ. D1, Long Thạnh Mỹ, Thủ Đức, Hồ Chí Minh 700000";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton menuButton;
    private AutoCompleteTextView spinnerDistrict;
    private RecyclerView recyclerViewLocations;
    private LocationAdapter locationAdapter;
    private List<StoreLocation> allLocations;
    private ToolbarHelper toolbarHelper;
    private CartController cartController;
    private List<StoreLocation> filteredLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ggmap);

        cartController = new CartController(this);

        toolbarHelper = new ToolbarHelper(this, drawerLayout, cartController);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuButton = findViewById(R.id.menuButton);
        findViewById(R.id.cartButton).setOnClickListener(v -> openCartActivity());
        toolbarHelper.updateCartBadge();
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        spinnerDistrict = findViewById(R.id.spinner_district);
        recyclerViewLocations = findViewById(R.id.recycler_view_locations);

        recyclerViewLocations.setLayoutManager(new LinearLayoutManager(this));
        allLocations = getSampleLocations();
        filteredLocations = new ArrayList<>(allLocations);
        locationAdapter = new LocationAdapter(filteredLocations, this);
        recyclerViewLocations.setAdapter(locationAdapter);

        List<String> districtList = new ArrayList<>();
        districtList.add("Tất cả");  // "All"
        districtList.add("Quận 1");
        districtList.add("Quận 2");
        districtList.add("Quận 3");
        districtList.add("Quận 4");
        districtList.add("Quận 5");
        districtList.add("Quận 7");
        districtList.add("Quận 9");
        districtList.add("Quận 10");
        districtList.add("Quận Bình Thạnh");
        districtList.add("Quận Gò Vấp");
        districtList.add("Thủ Đức");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districtList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(adapter);

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = parent.getItemAtPosition(position).toString();
                Toast.makeText(GgmapActivity.this, "Selected: " + selectedDistrict, Toast.LENGTH_SHORT).show();
                filterLocations(selectedDistrict);
                // TODO: Filter locations based on selected district and update RecyclerView
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        TextView tvAddress = findViewById(R.id.tvAddress);
        tvAddress.setOnClickListener(v -> openGoogleMaps());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    // Navigate to MainActivity
                    Intent intent = new Intent(GgmapActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (itemId == R.id.nav_about) {
                    Toast.makeText(GgmapActivity.this, "Giới thiệu về Keos clicked", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_products) {
                    Toast.makeText(GgmapActivity.this, "Sản phẩm clicked", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_blog) {
                    Toast.makeText(GgmapActivity.this, "Blog clicked", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_contact) {
                    Toast.makeText(GgmapActivity.this, "Liên hệ clicked", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_distribution) {
                    Toast.makeText(GgmapActivity.this, "Hệ thống phân phối clicked", Toast.LENGTH_SHORT).show();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void filterLocations(String district) {
        if (district.equals("Tất cả")) {
            filteredLocations.clear();
            filteredLocations.addAll(allLocations);
        } else {
            filteredLocations.clear();
            for (StoreLocation location : allLocations) {
                if (location.getAddress().contains(district)) {
                    filteredLocations.add(location);
                }
            }
        }
        locationAdapter.updateList(filteredLocations);
    }

    private List<StoreLocation> getSampleLocations() {
        List<StoreLocation> locations = new ArrayList<>();

        StoreLocation location1 = new StoreLocation();
        location1.setName("Pet Mart");
        location1.setAddress("347 Đ. Nguyễn Trãi, Phường 7, Quận 5, Hồ Chí Minh 749798, Việt Nam");
        location1.setLatitude(10.755781741612902);
        location1.setLongitude(106.67498877482528);
        locations.add(location1);

        StoreLocation location2 = new StoreLocation();
        location2.setName("MoAn PetShop");
        location2.setAddress("84 Đ. An Điềm, Phường 11, Quận 5, Hồ Chí Minh 749464, Việt Nam");
        location2.setLatitude(10.751773615625968);
        location2.setLongitude(106.66490234247415);
        locations.add(location2);

        StoreLocation location3 = new StoreLocation();
        location3.setName("Pet Lover");
        location3.setAddress("1183A Đ. 3 Tháng 2, Phường 7, Quận 11, Hồ Chí Minh, Việt Nam");
        location3.setLatitude(10.760272593579655);
        location3.setLongitude(106.65341791178298);
        locations.add(location3);

        StoreLocation location4 = new StoreLocation();
        location4.setName("KPET");
        location4.setAddress("256/4 Tân Phước, Phường 6, Quận 10, Hồ Chí Minh, Việt Nam");
        location4.setLatitude(10.762182031188233);
        location4.setLongitude(106.6627381136378);
        locations.add(location4);

        StoreLocation location5 = new StoreLocation();
        location5.setName("Emday - Pet Store");
        location5.setAddress("382/33 Nguyễn Thị Minh Khai, Phường 5, Quận 3, Hồ Chí Minh 72400, Việt Nam");
        location5.setLatitude(10.772677207220356);
        location5.setLongitude(106.68703507131055);
        locations.add(location5);

        return locations;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openCartActivity() {
        CartDialogFragment cartFragment = new CartDialogFragment();
        cartFragment.show(getSupportFragmentManager(), "CartDialog");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(LOCATION).title("Main store location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCATION, 15));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    private void openGoogleMaps() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(ADDRESS));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

}