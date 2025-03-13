package com.example.group1_petfood.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.group1_petfood.R;
import com.example.group1_petfood.adapters.OrderAdapter;
import com.example.group1_petfood.controllers.OrderController;
import com.example.group1_petfood.models.Order;
import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private OrderController orderController;
    private EditText searchEditText; // Search input field
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        // Khởi tạo OrderController
        orderController = new OrderController(this);

        // Ánh xạ view
        searchEditText = findViewById(R.id.searchEditText);
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);

        // Thiết lập RecyclerView
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        loadOrders();

        // Xử lý sự kiện tìm kiếm đơn hàng
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Tải danh sách đơn hàng
    private void loadOrders() {
        orderList = orderController.getAllOrders();
        orderAdapter = new OrderAdapter(orderList);
        recyclerViewOrders.setAdapter(orderAdapter);
    }

    // Lọc danh sách đơn hàng theo từ khóa
    private void filterOrders(String query) {
        List<Order> filteredList = new ArrayList<>();
        for (Order order : orderList) {
            if (order.getPaymentMethod().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(order);
            }
        }
            orderAdapter.updateList(filteredList);
    }
}
