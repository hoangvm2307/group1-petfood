package com.example.group1_petfood.utils;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.group1_petfood.R;
import com.example.group1_petfood.activities.GgmapActivity;
import com.example.group1_petfood.controllers.CartController;
import com.example.group1_petfood.fragments.CartDialogFragment;
import com.google.android.material.navigation.NavigationView;

/**
 * Helper class để quản lý Toolbar và các chức năng liên quan
 */
public class ToolbarHelper {
    private static final String TAG = "ToolbarHelper";
    private final AppCompatActivity activity;
    private final CartController cartController;
    private final DrawerLayout drawerLayout;
    private TextView cartBadge;
    private ImageButton menuButton;
    private ImageButton cartButton;
    private ImageButton locationButton;
    private ImageButton profileButton;

    public ToolbarHelper(AppCompatActivity activity, DrawerLayout drawerLayout, CartController cartController) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.cartController = cartController;
        initializeViews();
        setupToolbar();
        setupClickListeners();
    }

    private void initializeViews() {
        menuButton = activity.findViewById(R.id.menuButton);
        cartButton = activity.findViewById(R.id.cartButton);
        locationButton = activity.findViewById(R.id.locationButton);
        profileButton = activity.findViewById(R.id.profileButton);
        cartBadge = activity.findViewById(R.id.cartBadge);
    }

    private void setupToolbar() {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupClickListeners() {
        menuButton.setOnClickListener(v -> drawerLayout.open());
        cartButton.setOnClickListener(v -> openCartActivity());
        locationButton.setOnClickListener(v -> openGgmapActivity());
        // Thêm listener cho profileButton nếu cần
    }

    public void setupNavigationDrawer(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Xử lý các item trong navigation menu
            if (id == R.id.nav_product_management) {
                Intent intent = new Intent(activity, com.example.group1_petfood.activities.AdminProductActivity.class);
                activity.startActivity(intent);
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void openCartActivity() {
        CartDialogFragment cartFragment = new CartDialogFragment();
        cartFragment.show(activity.getSupportFragmentManager(), "CartDialog");
    }

    private void openGgmapActivity() {
        Intent intent = new Intent(activity, GgmapActivity.class);
        activity.startActivity(intent);
    }

    public void updateCartBadge() {
        try {
            // Lấy số lượng sản phẩm trong giỏ hàng
            int itemCount = cartController.getCartItemCount();

            if (cartBadge != null) {
                cartBadge.setText(String.valueOf(itemCount));

                // Hiển thị badge nếu có sản phẩm trong giỏ hàng, ẩn nếu không có
                if (itemCount > 0) {
                    cartBadge.setVisibility(View.VISIBLE);

                    // Đặt background màu đỏ cho badge
                    cartBadge.setBackgroundResource(R.drawable.cart_badge_background);

                    // Đặt padding để hiển thị tốt hơn
                    int padding = (int) (4 * activity.getResources().getDisplayMetrics().density);
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
}