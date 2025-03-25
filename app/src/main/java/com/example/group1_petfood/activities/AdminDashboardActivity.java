package com.example.group1_petfood.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.group1_petfood.fragments.AdminChatFragment;
import com.example.group1_petfood.models.Order;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "AdminDashboard";

    // Dashboard UI Components
    private TextView productCountTextView, userCountTextView, categoryCountTextView, orderCountTextView;
    private TextView totalRevenueTextView, avgRevenueTextView, welcomeTextView, dateTextView;
    private Button refreshButton;
    private RecyclerView recentOrdersRecyclerView;
    private BarChart revenueChart; // Biểu đồ doanh thu

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

        // Button
        refreshButton = findViewById(R.id.refreshButton);

        // Orders RecyclerView
        recentOrdersRecyclerView = findViewById(R.id.recentOrdersRecyclerView);
        recentOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Biểu đồ doanh thu
        revenueChart = findViewById(R.id.revenueChart);

        // DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        // Ngày hiện tại
        TextView dateTimeTextView = findViewById(R.id.dateTimeTextView);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
        String currentDate = dateFormat.format(new Date());
        dateTimeTextView.setText(currentDate);
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

        findViewById(R.id.chatButton).setOnClickListener(v -> {
            openAdminChat();
        });
    }
    private void openAdminChat() {
        AdminChatFragment chatDialog = new AdminChatFragment();
        chatDialog.show(getSupportFragmentManager(), "AdminChatDialog");
    }
    private void updateDashboardData() {
        updateStats();
        loadRecentOrders();
        updateRevenueInfo();
        setupRevenueChart();
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

    private void loadRecentOrders() {
        recentOrders = orderController.getAllOrders();
        if (recentOrders != null && !recentOrders.isEmpty()) {
            int limit = Math.min(recentOrders.size(), 5);
            List<Order> limitedOrders = recentOrders.subList(0, limit);

            orderAdapter = new OrderAdapter(limitedOrders);
            recentOrdersRecyclerView.setAdapter(orderAdapter);

            // Ẩn thông báo không có đơn hàng
            TextView noOrdersTextView = findViewById(R.id.noOrdersTextView);
            if (noOrdersTextView != null) {
                noOrdersTextView.setVisibility(View.GONE);
            }
        } else {
            // Hiển thị thông báo không có đơn hàng
            TextView noOrdersTextView = findViewById(R.id.noOrdersTextView);
            if (noOrdersTextView != null) {
                noOrdersTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateRevenueInfo() {
        // Tính tổng doanh thu từ các đơn hàng
        double totalRevenue = calculateTotalRevenue();
        double avgDailyRevenue = totalRevenue / 7; // Tính trung bình 7 ngày

        totalRevenueTextView.setText(formatCurrency(totalRevenue));
        avgRevenueTextView.setText(formatCurrency(avgDailyRevenue));
    }

    private double calculateTotalRevenue() {
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

    private void setupRevenueChart() {
        // Lấy dữ liệu doanh thu theo ngày trong 7 ngày gần nhất
        Map<String, Double> weeklyRevenueData = getWeeklyRevenueData();

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> xAxisLabels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Double> entry : weeklyRevenueData.entrySet()) {
            barEntries.add(new BarEntry(index, entry.getValue().floatValue()));
            xAxisLabels.add(entry.getKey());
            index++;
        }

        // Tạo dataset
        BarDataSet dataSet = new BarDataSet(barEntries, "Doanh thu (VNĐ)");
        dataSet.setColor(Color.rgb(65, 105, 225)); // Màu xanh dương
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        // Tạo BarData
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        // Cấu hình biểu đồ
        revenueChart.setData(barData);
        revenueChart.getDescription().setEnabled(false);
        revenueChart.setDrawGridBackground(false);
        revenueChart.setDrawBorders(false);
        revenueChart.setDrawValueAboveBar(true);

        // Cấu hình trục X
        XAxis xAxis = revenueChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));

        // Cấu hình trục Y
        YAxis leftAxis = revenueChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setAxisMinimum(0f);

        // Ẩn trục Y bên phải
        revenueChart.getAxisRight().setEnabled(false);

        // Cấu hình legend
        revenueChart.getLegend().setEnabled(true);

        // Vô hiệu hóa zoom
        revenueChart.setScaleEnabled(false);
        revenueChart.setDoubleTapToZoomEnabled(false);

        // Cập nhật biểu đồ
        revenueChart.invalidate();
    }

    private Map<String, Double> getWeeklyRevenueData() {
        Map<String, Double> dailyRevenue = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

        // Tạo map cho 7 ngày gần nhất
        Calendar calendar = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            Calendar tempCal = (Calendar) calendar.clone();
            tempCal.add(Calendar.DAY_OF_MONTH, -i);
            String dateKey = dateFormat.format(tempCal.getTime());
            String displayDate = displayFormat.format(tempCal.getTime());
            dailyRevenue.put(displayDate, 0.0);
        }

        // Thêm doanh thu từ các đơn hàng
        if (recentOrders != null) {
            for (Order order : recentOrders) {
                try {
                    // Lấy ngày tạo đơn hàng
                    Date orderDate = dateFormat.parse(order.getCreatedAt().substring(0, 10));
                    if (orderDate != null) {
                        String displayDate = displayFormat.format(orderDate);
                        // Kiểm tra nếu ngày này nằm trong 7 ngày gần nhất
                        if (dailyRevenue.containsKey(displayDate)) {
                            double currentAmount = dailyRevenue.get(displayDate);
                            dailyRevenue.put(displayDate, currentAmount + order.getTotalAmount());
                        }
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing date: " + e.getMessage());
                }
            }
        }

        return dailyRevenue;
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