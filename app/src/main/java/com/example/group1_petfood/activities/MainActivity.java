package com.example.group1_petfood.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

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
import com.example.group1_petfood.controllers.CartController;
import com.example.group1_petfood.controllers.CategoryController;
import com.example.group1_petfood.controllers.ProductController;
import com.example.group1_petfood.database.DatabaseHelper;
import com.example.group1_petfood.database.DatabaseInitializer;
import com.example.group1_petfood.fragments.CartDialogFragment;
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
    private TextView cartBadge;
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
        initializeDatabase();
        updateCartBadge();

        loadData();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại số lượng trong giỏ hàng mỗi khi quay lại MainActivity
        updateCartBadge();
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

    private void openCartActivity() {
        // Hiển thị CartDialogFragment thay vì BottomSheetDialogFragment
        CartDialogFragment cartFragment = new CartDialogFragment();
        cartFragment.show(getSupportFragmentManager(), "CartDialog");
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
        cartBadge = findViewById(R.id.cartBadge);
        findViewById(R.id.menuButton).setOnClickListener(v -> drawerLayout.open());
//        findViewById(R.id.chatButton).setOnClickListener(v -> openChat());
        findViewById(R.id.callButton).setOnClickListener(v -> openCartActivity());
        findViewById(R.id.cartButton).setOnClickListener(v -> openCartActivity());
    }
    private void updateCartBadge() {
        try {
            // Lấy số lượng sản phẩm trong giỏ hàng
            int itemCount = cartController.getCartItemCount();

            // Tìm TextView hiển thị số lượng trong MainActivity
            TextView cartBadge =  findViewById(R.id.cartBadge);
            if (cartBadge != null) {
                cartBadge.setText(String.valueOf(itemCount));

                // Hiển thị badge nếu có sản phẩm trong giỏ hàng, ẩn nếu không có
                if (itemCount > 0) {
                    cartBadge.setVisibility(View.VISIBLE);

                    // Đặt background màu đỏ cho badge
                    cartBadge.setBackgroundResource(R.drawable.cart_badge_background);

                    // Đặt padding để hiển thị tốt hơn
                    int padding = (int) (4 * this.getResources().getDisplayMetrics().density);
                    cartBadge.setPadding(padding, 0, padding, 0);

                    // Đặt style cho text
                    cartBadge.setTextColor(Color.WHITE);
                    cartBadge.setTypeface(null, Typeface.BOLD);
                    cartBadge.setGravity(Gravity.CENTER);
                } else {
                    cartBadge.setVisibility(View.GONE);
                }
            }

            Log.d(TAG, "Đã cập nhật badge giỏ hàng: " + itemCount + " sản phẩm");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi cập nhật badge giỏ hàng: " + e.getMessage());
        }
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
