package com.example.group1_petfood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.adapters.OrderAdapter;
import com.example.group1_petfood.controllers.CategoryController;
import com.example.group1_petfood.controllers.OrderController;
import com.example.group1_petfood.controllers.ProductController;
import com.example.group1_petfood.controllers.UserController;
import com.example.group1_petfood.models.Order;
import com.example.group1_petfood.utils.AccessControl;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "AdminDashboard";

    // Dashboard UI Components
    private TextView productCountTextView, userCountTextView, categoryCountTextView, orderCountTextView;
    private TextView totalRevenueTextView, avgRevenueTextView, welcomeTextView, dateTextView;
    private Button refreshButton;
    private RecyclerView recentOrdersRecyclerView;

    // Controllers
    private ProductController productController;
    private UserController userController;
    private CategoryController categoryController;
    private OrderController orderController;

    // Adapters and Data
    private OrderAdapter orderAdapter;
    private List<Order> recentOrders;

    // Navigation
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Kiểm tra quyền truy cập
//        if (!AccessControl.requireAdmin(this)) {
//            finish();
//            return;
//        }

        // Khởi tạo controllers
        initializeControllers();

        // Khởi tạo UI
        initializeViews();

        // Thiết lập navigation drawer
        setupNavigationDrawer();

        // Cập nhật dữ liệu
        updateDashboardData();

        // Thiết lập sự kiện
        setupEventListeners();
    }

    private void initializeControllers() {
        productController = new ProductController(this);
        userController = new UserController(this);
        categoryController = new CategoryController(this);
        orderController = new OrderController(this);
    }

    private void initializeViews() {
        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bảng điều khiển quản trị");
        }

        // Dashboard stats
        productCountTextView = findViewById(R.id.productCountTextView);
        userCountTextView = findViewById(R.id.userCountTextView);
        categoryCountTextView = findViewById(R.id.categoryCountTextView);
        orderCountTextView = findViewById(R.id.orderCountTextView);

        // Revenue info
        totalRevenueTextView = findViewById(R.id.totalRevenueTextView);
        avgRevenueTextView = findViewById(R.id.avgRevenueTextView);

//        // Welcome info
//        welcomeTextView = findViewById(R.id.welcomeTextView);
//        dateTextView = findViewById(R.id.dateTextView);

        // Button
        refreshButton = findViewById(R.id.refreshButton);

        // Orders RecyclerView
        recentOrdersRecyclerView = findViewById(R.id.recentOrdersRecyclerView);
        recentOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set active item
        navigationView.setCheckedItem(R.id.nav_dashboard);
    }

    private void setupEventListeners() {
        refreshButton.setOnClickListener(v -> {
            updateDashboardData();
            Toast.makeText(this, "Dữ liệu đã được cập nhật", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateDashboardData() {
        updateStats();
//        updateWelcomeInfo();
        loadRecentOrders();
        updateRevenueInfo();
    }

    private void updateStats() {
        int productCount = productController.getProductCount();
        int userCount = userController.getUserCount();
        int categoryCount = categoryController.getCategoryCount();
        int orderCount = orderController.getOrderCount();

        productCountTextView.setText(String.valueOf(productCount));
        userCountTextView.setText(String.valueOf(userCount));
        categoryCountTextView.setText(String.valueOf(categoryCount));
        orderCountTextView.setText(String.valueOf(orderCount));

        Log.d(TAG, "Product Count: " + productCount);
        Log.d(TAG, "User Count: " + userCount);
        Log.d(TAG, "Category Count: " + categoryCount);
        Log.d(TAG, "Order Count: " + orderCount);
    }

    private void updateWelcomeInfo() {
        // Cập nhật tên người dùng và ngày
        welcomeTextView.setText("Xin chào, Quản trị viên");

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
        String currentDate = dateFormat.format(new Date());
        dateTextView.setText(currentDate);
    }

    private void loadRecentOrders() {
        recentOrders = orderController.getAllOrders();
        if (recentOrders != null && !recentOrders.isEmpty()) {

            int limit = Math.min(recentOrders.size(), 5);
            List<Order> limitedOrders = recentOrders.subList(0, limit);

            orderAdapter = new OrderAdapter(limitedOrders);
            recentOrdersRecyclerView.setAdapter(orderAdapter);
        }
    }

    private void updateRevenueInfo() {
        // Trong thực tế, dữ liệu này sẽ được lấy từ cơ sở dữ liệu
        // Ở đây tạm thời hiển thị dữ liệu giả
        double totalRevenue = calculateTotalRevenue();
        double avgDailyRevenue = totalRevenue / 7; // Tính trung bình 7 ngày

        totalRevenueTextView.setText(formatCurrency(totalRevenue));
        avgRevenueTextView.setText(formatCurrency(avgDailyRevenue));
    }

    private double calculateTotalRevenue() {
        // Tính tổng doanh thu từ các đơn hàng
        double total = 0;
        if (recentOrders != null) {
            for (Order order : recentOrders) {
                total += order.getTotalAmount();
            }
        }
        return total;
    }

    private String formatCurrency(double amount) {
        return String.format(Locale.getDefault(), "%,.0f₫", amount);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Xử lý các menu item trong navigation drawer
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Chuyển đến MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_products) {
            // Chuyển đến AdminProductActivity
            Intent intent = new Intent(this, AdminProductActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_categories) {
            // Chuyển đến CategoryListActivity
            Intent intent = new Intent(this, CategoryListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_orders) {
            // Chuyển đến OrderListActivity
            Intent intent = new Intent(this, OrderListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_users) {
            // Chuyển đến UserManagementActivity
            Intent intent = new Intent(this, UserManagementActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            // Chuyển đến ProfileActivity
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            // Đăng xuất
            logout();
        }

        // Đóng drawer sau khi xử lý
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        // Xóa thông tin đăng nhập
        getSharedPreferences("user_pref", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật dữ liệu khi quay lại màn hình
        updateDashboardData();
    }
}