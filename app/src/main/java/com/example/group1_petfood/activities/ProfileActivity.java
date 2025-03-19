package com.example.group1_petfood.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.adapters.UserOrderAdapter;
import com.example.group1_petfood.controllers.CartController;
import com.example.group1_petfood.controllers.OrderController;
import com.example.group1_petfood.controllers.UserController;
import com.example.group1_petfood.models.Order;
import com.example.group1_petfood.models.User;
import com.example.group1_petfood.utils.ToolbarHelper;
import com.example.group1_petfood.utils.UserUtils;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements UserOrderAdapter.OnOrderClickListener {

    private TextView tvUsername, tvFullName, tvEmail, tvPhone, tvAddress;
    private Button btnEditProfile, btnLogout, btnViewAllOrders;
    private RecyclerView recyclerViewOrders;
    private TextView tvNoOrders;
    private LinearLayout layoutChangePassword;

    private UserController userController;
    private OrderController orderController;
    private ToolbarHelper toolbarHelper;
    private DrawerLayout drawerLayout;
    private CartController cartController;


    private static final int MAX_ORDERS_TO_SHOW = 3; // Số lượng đơn hàng hiển thị trên trang profile

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeControllers();
        initializeViews();
        setupNavigationDrawer();
        setupListeners();
        loadUserProfile();
        loadUserOrders();
    }

    private void initializeControllers() {
        userController = new UserController(this);
        orderController = new OrderController(this);
        cartController = new CartController(this);

    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbarHelper = new ToolbarHelper(this, drawerLayout, cartController);
        toolbarHelper.updateCartBadge();
        tvUsername = findViewById(R.id.tvUsername);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewAllOrders = findViewById(R.id.btnViewAllOrders);
        layoutChangePassword = findViewById(R.id.layoutChangePassword);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        tvNoOrders = findViewById(R.id.tvNoOrders);

        // Thiết lập RecyclerView
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        toolbarHelper.setupNavigationDrawer(navigationView);
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logoutUser());

        btnViewAllOrders.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, OrderListActivity.class);
            startActivity(intent);
        });

        layoutChangePassword.setOnClickListener(v -> {
            // Hiện tại chưa có activity đổi mật khẩu, có thể thêm sau
            Toast.makeText(this, "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserProfile() {
        int userId = UserUtils.getCurrentUserId(this);
        User currentUser = userController.getUserById(userId);

        if (currentUser != null) {
            tvFullName.setText(currentUser.getFullName());
            tvUsername.setText("@" + currentUser.getUsername());
            setStyledText(tvEmail, "Email: ", currentUser.getEmail());
            setStyledText(tvPhone, "Phone: ", currentUser.getPhone());
            setStyledText(tvAddress, "Address: ", currentUser.getAddress());
        } else {
            Toast.makeText(this, "Failed to load user profile.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserOrders() {
        int userId = UserUtils.getCurrentUserId(this);
        List<Order> allOrders = orderController.getAllOrders();

        // Lọc ra những đơn hàng của người dùng hiện tại
        List<Order> userOrders = new ArrayList<>();
        for (Order order : allOrders) {
            if (order.getUserId() == userId) {
                userOrders.add(order);
            }
        }

        if (userOrders.isEmpty()) {
            // Hiển thị thông báo không có đơn hàng
            recyclerViewOrders.setVisibility(View.GONE);
            tvNoOrders.setVisibility(View.VISIBLE);
            btnViewAllOrders.setVisibility(View.GONE);
        } else {
            recyclerViewOrders.setVisibility(View.VISIBLE);
            tvNoOrders.setVisibility(View.GONE);

            // Nếu có nhiều hơn số lượng tối đa cần hiển thị, chỉ lấy một số lượng nhất định
            List<Order> ordersToShow = userOrders;
            if (userOrders.size() > MAX_ORDERS_TO_SHOW) {
                ordersToShow = userOrders.subList(0, MAX_ORDERS_TO_SHOW);
                btnViewAllOrders.setVisibility(View.VISIBLE);
            } else {
                btnViewAllOrders.setVisibility(View.GONE);
            }

            // Thiết lập adapter cho RecyclerView
            UserOrderAdapter adapter = new UserOrderAdapter(ordersToShow, this, this);
            recyclerViewOrders.setAdapter(adapter);
        }
    }

    private void setStyledText(TextView textView, String label, String value) {
        // Nếu giá trị rỗng, hiển thị "Not provided"
        if (value == null || value.isEmpty()) {
            value = "Not provided";
        }

        SpannableString spannableString = new SpannableString(label + value);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.custom_green)),
                0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }

    private void logoutUser() {
        SharedPreferences preferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onOrderClick(Order order) {
        // Có thể chuyển đến màn hình chi tiết đơn hàng khi người dùng nhấn vào một đơn hàng
        Toast.makeText(this, "Đơn hàng #" + order.getId(), Toast.LENGTH_SHORT).show();
        // Bạn có thể thêm Activity chi tiết đơn hàng sau
    }
}