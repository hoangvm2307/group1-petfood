package com.example.group1_petfood.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.group1_petfood.R;
import com.example.group1_petfood.adapters.BannerAdapter;
import com.example.group1_petfood.adapters.CategoryAdapter;
import com.example.group1_petfood.adapters.ProductAdapter;
import com.example.group1_petfood.controllers.CartController;
import com.example.group1_petfood.controllers.CategoryController;
import com.example.group1_petfood.controllers.ProductController;
import com.example.group1_petfood.database.DatabaseHelper;
import com.example.group1_petfood.database.DatabaseInitializer;
import com.example.group1_petfood.utils.ToolbarHelper;
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
    private DatabaseInitializer dbInitializer;
    private CartController cartController;
    private ToolbarHelper toolbarHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeControllers();
        initializeViews();

        // Sử dụng ToolbarHelper
        toolbarHelper = new ToolbarHelper(this, drawerLayout, cartController);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toolbarHelper.setupNavigationDrawer(navigationView);

        setupBanner();
        setupRecyclerViews();
        initializeDatabase();
        toolbarHelper.updateCartBadge();

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại số lượng trong giỏ hàng mỗi khi quay lại MainActivity
        toolbarHelper.updateCartBadge();
    }

    private void initializeDatabase() {
        try {
            // Khởi tạo DatabaseHelper
            dbHelper = new DatabaseHelper(this);

            // Sử dụng DatabaseInitializer để tạo và nạp dữ liệu mẫu
            dbInitializer = new DatabaseInitializer(this);
            dbInitializer.initializeDatabase();

            Log.d(TAG, "Cơ sở dữ liệu đã được khởi tạo thành công");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi tạo cơ sở dữ liệu", e);
        }
    }

    private void initializeControllers() {
        categoryController = new CategoryController(this);
        productController = new ProductController(this);
        cartController = new CartController(this);
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        bannerViewPager = findViewById(R.id.bannerViewPager);
        bannerIndicator = findViewById(R.id.bannerIndicator);
        quickActionsRecyclerView = findViewById(R.id.quickActionsRecyclerView);
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDB();
            Log.d(TAG, "Database connection closed");
        }
    }
}