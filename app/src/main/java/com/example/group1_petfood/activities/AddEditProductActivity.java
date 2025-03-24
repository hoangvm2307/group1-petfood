package com.example.group1_petfood.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.group1_petfood.R;
import com.example.group1_petfood.controllers.CategoryController;
import com.example.group1_petfood.controllers.ProductController;
import com.example.group1_petfood.models.Category;
import com.example.group1_petfood.models.Product;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.List;

public class AddEditProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView productImage;
    private ImageButton btnAddImage;
    private EditText etName, etBrand, etDescription, etPrice, etStock, etImageUrl;
    private Button btnSave;
    private ProductController productController;
    private Product product;
    private int selectedCategoryId = -1;
    private MaterialAutoCompleteTextView etCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        productImage = findViewById(R.id.productImage);
        btnAddImage = findViewById(R.id.btnAddImage);
        etName = findViewById(R.id.etName);
        etBrand = findViewById(R.id.etBrand);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etStock = findViewById(R.id.etStock);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnSave = findViewById(R.id.btnSave);
        etCategory = findViewById(R.id.etCategory);
        CategoryController categoryController = new CategoryController(this);
        List<Category> categories = categoryController.getAllCategories();
        String[] categoryNames = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            categoryNames[i] = categories.get(i).getName();
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, categoryNames);
        etCategory.setAdapter(categoryAdapter);
        etCategory.setOnClickListener(v -> etCategory.showDropDown());
        etCategory.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategoryId = categories.get(position).getId();
        });

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
                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                    Glide.with(this)
                            .load(Uri.parse(product.getImageUrl()))
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.placeholder_image)
                            .into(productImage);
                } else {
                    Glide.with(this).load(R.drawable.placeholder_image).into(productImage);
                }
                for (Category category : categories) {
                    if (category.getId() == product.getCategoryId()) {
                        etCategory.setText(category.getName(), false);
                        selectedCategoryId = category.getId();
                        break;
                    }
                }
            }
        }

        btnAddImage.setOnClickListener(v -> openFileChooser());
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION; // Chỉ yêu cầu quyền đọc
            getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
            Glide.with(this).load(imageUri).into(productImage);
        }
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String brand = etBrand.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        // Kiểm tra đầu vào hợp lệ
        if (name.isEmpty() || brand.isEmpty() || description.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || selectedCategoryId == -1) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int stock = Integer.parseInt(stockStr);
        String imageUrl = (imageUri != null) ? imageUri.toString() : (product != null ? product.getImageUrl() : "");

        if (product == null) {
            // Thêm sản phẩm mới
            Product newProduct = new Product(name, brand, description, price, stock, imageUrl, selectedCategoryId);
            boolean isAdded = productController.addProduct(newProduct);
            Toast.makeText(this, isAdded ? "Thêm sản phẩm thành công" : "Lỗi khi thêm sản phẩm", Toast.LENGTH_SHORT).show();
            if (isAdded) finish();
        } else {
            // Cập nhật sản phẩm
            product.setName(name);
            product.setBrand(brand);
            product.setDescription(description);
            product.setPrice(price);
            product.setStockQuantity(stock);
            product.setImageUrl(imageUrl);
            product.setCategoryId(selectedCategoryId);

            boolean isUpdated = productController.updateProduct(product);
            Toast.makeText(this, isUpdated ? "Cập nhật sản phẩm thành công" : "Lỗi khi cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
            if (isUpdated) finish();
        }
    }
}
