package com.example.group1_petfood.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group1_petfood.R;
import com.example.group1_petfood.controllers.ProductController;
import com.example.group1_petfood.models.Product;

public class AddEditProductActivity extends AppCompatActivity {
    private EditText etName, etBrand, etDescription, etPrice, etStock, etImageUrl;
    private Button btnSave;
    private ProductController productController;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        // Ánh xạ view
        etName = findViewById(R.id.etName);
        etBrand = findViewById(R.id.etBrand);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etStock = findViewById(R.id.etStock);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnSave = findViewById(R.id.btnSave);

        productController = new ProductController(this);

        int productId = getIntent().getIntExtra("product_id", -1);
        if (productId != -1) {
            product = productController.getProductById(productId);
            if (product != null) {
                etName.setText(product.getName());
                etBrand.setText(product.getBrand());
                etDescription.setText(product.getDescription());
                etPrice.setText(String.valueOf(product.getPrice()));
                etStock.setText(String.valueOf(product.getStockQuantity()));
                etImageUrl.setText(product.getImageUrl());
            }
        }

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = etName.getText().toString();
        String brand = etBrand.getText().toString();
        String description = etDescription.getText().toString();
        double price = Double.parseDouble(etPrice.getText().toString());
        int stock = Integer.parseInt(etStock.getText().toString());
        String imageUrl = etImageUrl.getText().toString();

        if (product == null) {
            Product newProduct = new Product();
            newProduct.setName(name);
            newProduct.setBrand(brand);
            newProduct.setDescription(description);
            newProduct.setPrice(price);
            newProduct.setStockQuantity(stock);
            newProduct.setImageUrl(imageUrl);

            boolean isAdded = productController.addProduct(newProduct);
            if (isAdded) {
                Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi thêm sản phẩm", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Cập nhật sản phẩm
            product.setName(name);
            product.setBrand(brand);
            product.setDescription(description);
            product.setPrice(price);
            product.setStockQuantity(stock);
            product.setImageUrl(imageUrl);

            boolean isUpdated = productController.updateProduct(product);
            if (isUpdated) {
                Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
            }
        }
    }
}