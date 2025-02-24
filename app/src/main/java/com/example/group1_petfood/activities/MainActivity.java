package com.example.group1_petfood.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.group1_petfood.R;
import com.example.group1_petfood.adapters.BannerAdapter;
import com.example.group1_petfood.adapters.CategoryAdapter;
import com.example.group1_petfood.adapters.ProductAdapter;
import com.example.group1_petfood.controllers.CategoryController;
import com.example.group1_petfood.controllers.ProductController;
import com.example.group1_petfood.database.DatabaseHelper;
import com.example.group1_petfood.models.Category;
import com.example.group1_petfood.models.Product;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private ViewPager2 bannerViewPager;
    private TabLayout bannerIndicator;
    private RecyclerView quickActionsRecyclerView;
    private RecyclerView categoriesRecyclerView;
    private RecyclerView productsRecyclerView;
    private DatabaseHelper dbHelper;
    private CategoryController categoryController;
    private ProductController productController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeControllers();
        setupToolbar();
        setupNavigationDrawer();
        setupBanner();
        setupRecyclerViews();
//        initializeDatabase();
        loadData();
    }

    private void initializeDatabase() {
        try {
            dbHelper = new DatabaseHelper(this);
            // Open database to create tables
            dbHelper.getWritableDatabase();
            Log.d(TAG, "Database initialized successfully");

            // Insert sample data if needed
            insertSampleData();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database", e);
        }
    }

    private void initializeControllers() {
        categoryController = new CategoryController(this);
        productController = new ProductController(this);
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        bannerViewPager = findViewById(R.id.bannerViewPager);
        bannerIndicator = findViewById(R.id.bannerIndicator);
        quickActionsRecyclerView = findViewById(R.id.quickActionsRecyclerView);
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        productsRecyclerView = findViewById(R.id.productsRecyclerView);

        findViewById(R.id.menuButton).setOnClickListener(v -> drawerLayout.open());
//        findViewById(R.id.chatButton).setOnClickListener(v -> openChat());
        findViewById(R.id.callButton).setOnClickListener(v -> makeCall());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation item clicks
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupBanner() {
        BannerAdapter bannerAdapter = new BannerAdapter();
        bannerViewPager.setAdapter(bannerAdapter);

        // Connect ViewPager2 with TabLayout indicator
        new TabLayoutMediator(bannerIndicator, bannerViewPager,
                (tab, position) -> {
                }).attach();

        // Auto scroll banner
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = bannerViewPager.getCurrentItem();
                int totalItems = bannerAdapter.getItemCount();
                int nextItem = (currentItem + 1) % totalItems;
                bannerViewPager.setCurrentItem(nextItem);
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void setupRecyclerViews() {
        // Quick Actions
        quickActionsRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 4));

        // Categories
        categoriesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Products
        productsRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 2));
    }

    private void loadData() {
        // Load categories
        List<Category> categories = categoryController.getAllCategories();
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // Load products
        List<Product> products = productController.getFeaturedProducts();
        ProductAdapter productAdapter = new ProductAdapter(products);
        productsRecyclerView.setAdapter(productAdapter);
    }

//    private void openChat() {
//        // Open chat activity/fragment
//        startActivity(new Intent(this, ChatActivity.class));
//    }
    private void makeCall() {
        // Make phone call to store
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:YOUR_PHONE_NUMBER"));
        startActivity(intent);
    }

    private void insertSampleData() {
        dbHelper.executeInTransaction(db -> {
            try {
                // Insert sample categories
                db.execSQL("INSERT OR IGNORE INTO categories (name, description) VALUES (?, ?)",
                        new Object[]{"Dog Food", "High quality food for dogs"});
                db.execSQL("INSERT OR IGNORE INTO categories (name, description) VALUES (?, ?)",
                        new Object[]{"Cat Food", "Premium food for cats"});

                // Insert sample products
                db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, price, stock_quantity) VALUES (?, ?, ?, ?, ?)",
                        new Object[]{1, "Premium Dog Food", "Royal Canin", 29.99, 100});

                Log.d(TAG, "Sample data inserted successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error inserting sample data", e);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDB();
            Log.d(TAG, "Database connection closed");
        }
    }
}
