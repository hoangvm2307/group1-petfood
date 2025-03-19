package com.example.group1_petfood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.adapters.AdminProductAdapter;
import com.example.group1_petfood.controllers.ProductController;
import com.example.group1_petfood.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AdminProductActivity extends AppCompatActivity implements AdminProductAdapter.OnProductActionListener {
    private RecyclerView recyclerView;
    private AdminProductAdapter adapter;
    private ProductController productController;
    private FloatingActionButton btnAddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);

        productController = new ProductController(this);

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
        boolean isDeleted = productController.deleteProduct(product.getId());
        if (isDeleted) {
            loadProducts();
        }
    }
}