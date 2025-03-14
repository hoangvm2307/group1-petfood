package com.example.group1_petfood.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.group1_petfood.R;
import com.example.group1_petfood.adapters.RelatedProductsAdapter;
import com.example.group1_petfood.controllers.CartController;
import com.example.group1_petfood.controllers.ProductController;
import com.example.group1_petfood.models.Product;
import com.google.android.material.chip.Chip;
import com.squareup.picasso.Picasso;
import java.util.List;

public class UserProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productPrice, quantity;
    private RecyclerView rvRelatedProducts;
    private RelatedProductsAdapter adapter;
    private ProductController productController;
    private Button btnAddToCart;
    private CartController cartController;
    private Product product;
    private Chip txtStockStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_user);

        // Initialize Views
        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        rvRelatedProducts = findViewById(R.id.rvRelatedProducts);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        quantity = findViewById(R.id.txtQuantity);
        txtStockStatus = findViewById(R.id.txtStockStatus);

        // Initialize Controller
        productController = new ProductController(this);
        cartController = new CartController(this);

        // Load Product Details
        loadProductDetails();

        // Setup RecyclerView
        setupRelatedProducts();
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }

    private void loadProductDetails() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("product_id")){
            int productId = intent.getIntExtra("product_id", -1);
        if (productId != -1) {
            product = productController.getProductById(productId);
            if (product != null) {
                productName.setText(product.getName());
                productPrice.setText(String.valueOf(product.getPrice()));

                // Load Image using Picasso
                Picasso.get()
                        .load(product.getImageUrl()) // Ensure Product has a valid image URL
                        .placeholder(R.drawable.placeholder_image) // Placeholder while loading
                        .error(R.drawable.placeholder_image) // Fallback image if error occurs
                        .into(productImage);
                updateStockStatus(product.getStockQuantity());

            }
        }
        }
    }

    private void setupRelatedProducts() {
        List<Product> relatedProducts = productController.getFeaturedProducts();

        if (relatedProducts != null && !relatedProducts.isEmpty()) {
            rvRelatedProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvRelatedProducts.setHasFixedSize(true); // Optimizes performance

            // Correct constructor usage (pass context)
            adapter = new RelatedProductsAdapter(this, relatedProducts);
            rvRelatedProducts.setAdapter(adapter);
        }
    }
    private void addToCart() {
        if (product != null) {
          cartController.addToCart(product.getId(), Integer.parseInt(quantity.getText().toString()));
            Toast.makeText(this, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product details not available", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateStockStatus(int stockQuantity) {

        if (stockQuantity > 0) {
            txtStockStatus.setText("Còn hàng");
            txtStockStatus.setTextColor(Color.WHITE); // Text color remains readable
            txtStockStatus.setChipBackgroundColorResource(android.R.color.holo_green_dark);
        } else {
            txtStockStatus.setText("Hết hàng");
            txtStockStatus.setTextColor(Color.WHITE);
            txtStockStatus.setChipBackgroundColorResource(android.R.color.holo_red_dark);
        }
    }
}
