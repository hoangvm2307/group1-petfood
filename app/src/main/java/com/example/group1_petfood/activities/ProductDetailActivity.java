package com.example.group1_petfood.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.group1_petfood.R;
import com.example.group1_petfood.models.Product;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView productImage;
    private TextView productName, productBrand, productPrice, productStock, productDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Ánh xạ view
        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productBrand = findViewById(R.id.productBrand);
        productPrice = findViewById(R.id.productPrice);
        productStock = findViewById(R.id.productStock);
        productDescription = findViewById(R.id.productDescription);

        // Nhận dữ liệu sản phẩm từ Intent
        if (getIntent().hasExtra("product")) {
            Product product = (Product) getIntent().getSerializableExtra("product");

            // Hiển thị dữ liệu lên giao diện
            productName.setText(product.getName());
            productBrand.setText("Thương hiệu: " + product.getBrand());
            productPrice.setText(String.format("Giá: %,.0f₫", product.getPrice()));
            productStock.setText("Số lượng còn lại: " + product.getStockQuantity());
            productDescription.setText(product.getDescription());

            // Hiển thị hình ảnh sản phẩm (nếu có)
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Picasso.get().load(product.getImageUrl()).into(productImage);
            } else {
                productImage.setImageResource(R.drawable.sample_product);
            }
        }
    }
}
