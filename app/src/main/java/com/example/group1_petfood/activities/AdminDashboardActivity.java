package com.example.group1_petfood.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.adapters.OrderAdapter;
import com.example.group1_petfood.controllers.CategoryController;
import com.example.group1_petfood.controllers.OrderController;
import com.example.group1_petfood.controllers.ProductController;
import com.example.group1_petfood.controllers.UserController;
import com.example.group1_petfood.models.Order;

import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView productCountTextView, userCountTextView, categoryCountTextView, orderCountTextView;
    private ProductController productController;
    private UserController userController;
    private CategoryController categoryController;
    private OrderController orderController;
    private Button refreshButton;
    private RecyclerView recentOrdersRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> recentOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        productCountTextView = findViewById(R.id.productCountTextView);
        userCountTextView = findViewById(R.id.userCountTextView);
        categoryCountTextView = findViewById(R.id.categoryCountTextView);
        orderCountTextView = findViewById(R.id.orderCountTextView);
        refreshButton = findViewById(R.id.refreshButton);

        productController = new ProductController(this);
        userController = new UserController(this);
        categoryController = new CategoryController(this);
        orderController = new OrderController(this);

        updateCounts();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCounts();
            }
        });
        recentOrdersRecyclerView = findViewById(R.id.recentOrdersRecyclerView);
        recentOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderController = new OrderController(this);
        loadRecentOrders();
    }

    private void updateCounts() {
        int productCount = productController.getProductCount();
        int userCount = userController.getUserCount();
        int categoryCount = categoryController.getCategoryCount();
        int orderCount = orderController.getOrderCount();

        productCountTextView.setText(String.valueOf(productCount));
        userCountTextView.setText(String.valueOf(userCount));
        categoryCountTextView.setText(String.valueOf(categoryCount));
        orderCountTextView.setText(String.valueOf(orderCount));

        Log.d("AdminDashboard", "Product Count: " + productCount);
        Log.d("AdminDashboard", "User Count: " + userCount);
        Log.d("AdminDashboard", "Category Count: " + categoryCount);
        Log.d("AdminDashboard", "Order Count: " + orderCount);
    }
    private void loadRecentOrders() {
        recentOrders = orderController.getAllOrders();
        orderAdapter = new OrderAdapter(recentOrders);
        recentOrdersRecyclerView.setAdapter(orderAdapter);
    }
}