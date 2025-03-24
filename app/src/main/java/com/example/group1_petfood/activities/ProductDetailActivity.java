package com.example.group1_petfood.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.group1_petfood.R;
import com.example.group1_petfood.controllers.CategoryController;
import com.example.group1_petfood.models.Product;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView productImage;
    private TextView productCategory, productName, productBrand, productPrice, productStock, productDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chi tiết sản phẩm");

        productImage = findViewById(R.id.productImage);
        productCategory = findViewById(R.id.productCategory);
        productName = findViewById(R.id.productName);
        productBrand = findViewById(R.id.productBrand);
        productPrice = findViewById(R.id.productPrice);
        productStock = findViewById(R.id.productStock);
        productDescription = findViewById(R.id.productDescription);

        if (getIntent().hasExtra("product")) {
            Product product = (Product) getIntent().getSerializableExtra("product");

            // Hiển thị thông tin sản phẩm
            productName.setText(product.getName());
            productBrand.setText("Thương hiệu: " + product.getBrand());
            productPrice.setText(String.format("Giá: %,.0f₫", product.getPrice()));
            productStock.setText("Số lượng còn lại: " + product.getStockQuantity());
            productDescription.setText(product.getDescription());

            // Lấy tên danh mục từ ID
            CategoryController categoryController = new CategoryController(this);
            String categoryName = categoryController.getCategoryNameById(product.getCategoryId());
            productCategory.setText("Danh mục: " + categoryName);

            // Hiển thị hình ảnh sản phẩm
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(product.getImageUrl());
                    Log.d("ProductDetailActivity", "Loading image: " + imageUri.toString());
                    Glide.with(this)
                            .load(imageUri)
                            .placeholder(R.drawable.sample_product)
                            .error(R.drawable.sample_product)
                            .into(productImage);
                } catch (Exception e) {
                    Log.e("ProductDetailActivity", "Error loading image: " + e.getMessage());
                    productImage.setImageResource(R.drawable.sample_product);
                }
            } else {
                productImage.setImageResource(R.drawable.sample_product);
            }
        }
    }
}
