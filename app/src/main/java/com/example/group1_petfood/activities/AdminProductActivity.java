package com.example.group1_petfood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.adapters.AdminProductAdapter;
import com.example.group1_petfood.controllers.ProductController;
import com.example.group1_petfood.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminProductActivity extends AppCompatActivity implements AdminProductAdapter.OnProductActionListener {
    private RecyclerView recyclerView;
    private AdminProductAdapter adapter;
    private ProductController productController;
    private TextInputEditText searchEditText;
    private FloatingActionButton btnAddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);

        productController = new ProductController(this);
        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAddProduct = findViewById(R.id.btnAddProduct);
        recyclerView = findViewById(R.id.recyclerViewProducts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadProducts();

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProductActivity.this, AddEditProductActivity.class);
            startActivity(intent);
        });
    }

    private void loadProducts() {
        List<Product> products = productController.getAllProducts();
        adapter = new AdminProductAdapter(products, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onEditProduct(Product product) {
        Intent intent = new Intent(this, AddEditProductActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteProduct(Product product) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc muốn xoá sản phẩm \"" + product.getName() + "\" không?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    boolean isDeleted = productController.deleteProduct(product.getId());
                    if (isDeleted) {
                        Toast.makeText(this, "Sản phẩm đã được xoá", Toast.LENGTH_SHORT).show();
                        loadProducts();
                    } else {
                        Toast.makeText(this, "Xoá thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss())
                .show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productController.getAllProducts()) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        adapter.updateData(filteredList);
    }

}