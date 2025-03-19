    package com.example.group1_petfood.activities;

    import android.content.Intent;
    import android.graphics.Color;
    import android.graphics.Paint;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;
    import androidx.drawerlayout.widget.DrawerLayout;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;
    import com.example.group1_petfood.R;
    import com.example.group1_petfood.adapters.RelatedProductsAdapter;
    import com.example.group1_petfood.controllers.CartController;
    import com.example.group1_petfood.controllers.ProductController;
    import com.example.group1_petfood.models.Product;
    import com.example.group1_petfood.utils.ToolbarHelper;
    import com.google.android.material.chip.Chip;
    import com.google.android.material.navigation.NavigationView;

    import java.util.List;

    public class UserProductDetailActivity extends AppCompatActivity {
        private static final String TAG = "UserProductDetailActivity";
        private ImageView productImage;
        private Button btnIncreaseQuantity, btnDecreaseQuantity;
        private TextView productName, productPrice, quantity, productDescription;
        private TextView productCode, productBrand, productWeight, imageCaption;
        private RecyclerView rvRelatedProducts;
        private RelatedProductsAdapter adapter;
        private ProductController productController;
        private Button btnAddToCart;
        private CartController cartController;
        private Product product;
        private Chip txtStockStatus;
        private TextView productOriginalPrice, productDiscount;
        private ImageButton btnNextImage;

        // Toolbar components
        private ToolbarHelper toolbarHelper;
        private DrawerLayout drawerLayout;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_product_detail_user);

            // Initialize Views
            initViews();

            // Initialize Controllers
            productController = new ProductController(this);
            cartController = new CartController(this);

            // Setup Toolbar
            setupToolbar();

            // Load Product Details
            loadProductDetails();

            // Setup RecyclerView
            setupRelatedProducts();

            // Setup Click Listeners
            setupClickListeners();
        }

        private void setupToolbar() {
            try {
                drawerLayout = findViewById(R.id.drawer_layout);
                toolbarHelper = new ToolbarHelper(this, drawerLayout, cartController);

                // Setup navigation if NavigationView exists
                NavigationView navigationView = findViewById(R.id.nav_view);
                if (navigationView != null) {
                    toolbarHelper.setupNavigationDrawer(navigationView);
                }
                toolbarHelper.updateCartBadge();
            } catch (Exception e) {
                Log.e(TAG, "Error setting up toolbar: " + e.getMessage());
            }
        }

        private void initViews() {
            // Assign all UI elements
            productImage = findViewById(R.id.productImage);
            productName = findViewById(R.id.productName);
            productPrice = findViewById(R.id.productPrice);
            quantity = findViewById(R.id.txtQuantity);
            txtStockStatus = findViewById(R.id.txtStockStatus);
            rvRelatedProducts = findViewById(R.id.rvRelatedProducts);
            btnAddToCart = findViewById(R.id.btnAddToCart);
            btnIncreaseQuantity = findViewById(R.id.btnIncreaseQuantity);
            btnDecreaseQuantity = findViewById(R.id.btnDecreaseQuantity);
            // New fields for extended information
            productDescription = findViewById(R.id.productDescription);
            productCode = findViewById(R.id.productCode);
            productBrand = findViewById(R.id.productBrand);
            productWeight = findViewById(R.id.productWeight);
            productOriginalPrice = findViewById(R.id.productOriginalPrice);


            // Optional fields that might not exist in all layouts
            try {
                productDiscount = findViewById(R.id.productDiscount);
            } catch (Exception e) {
                Log.d(TAG, "Optional views not found: " + e.getMessage());
            }
        }

        private void setupClickListeners() {
            // Tăng số lượng
            btnIncreaseQuantity.setOnClickListener(v -> {
                int currentQuantity = Integer.parseInt(quantity.getText().toString());
                currentQuantity++;
                quantity.setText(String.valueOf(currentQuantity));
            });

            // Giảm số lượng
            btnDecreaseQuantity.setOnClickListener(v -> {
                int currentQuantity = Integer.parseInt(quantity.getText().toString());
                if (currentQuantity > 1) {
                    currentQuantity--;
                    quantity.setText(String.valueOf(currentQuantity));
                }
            });

            // Add to cart button
            btnAddToCart.setOnClickListener(v -> addToCart());

            // Image navigation button if exists
            if (btnNextImage != null) {
                btnNextImage.setOnClickListener(v -> {
                    // Handle multiple product images if implemented
                    Toast.makeText(this, "Tính năng xem nhiều ảnh đang được phát triển", Toast.LENGTH_SHORT).show();
                });
            }
        }

        private void loadProductDetails() {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("product_id")) {
                int productId = intent.getIntExtra("product_id", -1);
                if (productId != -1) {
                    product = productController.getProductById(productId);
                    if (product != null) {
                        displayProductInfo();
                    } else {
                        Toast.makeText(this, "Không thể tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        }

        private void displayProductInfo() {
            // Hiển thị tên sản phẩm
            productName.setText(product.getName());



            // Tính giá gốc (giả sử giá hiển thị là đã giảm 7%)
            double originalPrice = product.getPrice();
            if (productOriginalPrice != null) {
                productOriginalPrice.setText(String.format("%,.0f₫", originalPrice));
                productOriginalPrice.setPaintFlags(productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            // Hiển thị giá sản phẩm (định dạng với dấu phân cách hàng nghìn)
            double salePrice = originalPrice * 0.8;
            productPrice.setText(String.format("%,.0f₫", salePrice));
            // Hiển thị % giảm giá nếu view tồn tại
            if (productDiscount != null) {
                productDiscount.setText("-7%");
            }

            // Hiển thị thông tin mô tả
            if (productDescription != null && product.getDescription() != null) {
                productDescription.setText(product.getDescription());
            }

            // Hiển thị mã sản phẩm - tạo mã sản phẩm từ ID
            if (productCode != null) {
                String code = "KSDPBG" + product.getId() + "CTH";
                productCode.setText("Mã sản phẩm: " + code);
            }

            // Hiển thị thương hiệu
            if (productBrand != null) {
                productBrand.setText("Thương hiệu: " + product.getBrand());
            }

            // Hiển thị trọng lượng (trích xuất từ mô tả hoặc mặc định)
            if (productWeight != null) {
                String weight = extractWeightFromDescription(product.getDescription());
                productWeight.setText("Trọng lượng: " + weight);
            }

            // Hiển thị caption cho hình ảnh nếu có
            if (imageCaption != null) {
                imageCaption.setText("Thức ăn dinh dưỡng thú cưng công thức Pháp");
            }

            // Hiển thị trạng thái tồn kho
            updateStockStatus(product.getStockQuantity());

            // Hiển thị hình ảnh sản phẩm
            displayProductImage();
        }

        private String extractWeightFromDescription(String description) {
            if (description != null && description.contains("Trọng lượng:")) {
                int start = description.indexOf("Trọng lượng:") + 12;
                int end = description.indexOf(".", start);
                if (end == -1) end = description.length();

                if (start < end && start < description.length()) {
                    return description.substring(start, end).trim();
                }
            }
            return "400g - 20kg"; // Giá trị mặc định
        }

        private void displayProductImage() {
            // Lấy URL hình ảnh từ sản phẩm
            String imageUrl = product.getImageUrl();
            Log.d(TAG, "Hiển thị hình ảnh sản phẩm: " + imageUrl);

            // Kiểm tra imageUrl có tồn tại không
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Tìm ID tài nguyên dựa trên tên
                int imageResId = getResources().getIdentifier(imageUrl, "drawable", getPackageName());

                if (imageResId != 0) {
                    // Nếu tìm thấy, hiển thị hình ảnh
                    productImage.setImageResource(imageResId);
                    Log.d(TAG, "Đã tìm thấy hình ảnh: " + imageUrl + ", ResID: " + imageResId);
                } else {
                    // Nếu không tìm thấy, hiển thị hình ảnh mặc định
                    productImage.setImageResource(R.drawable.slide_1_img);
                    Log.d(TAG, "Không tìm thấy hình ảnh: " + imageUrl);
                }
            } else {
                // Nếu URL hình ảnh trống, hiển thị hình ảnh mặc định
                productImage.setImageResource(R.drawable.slide_1_img);
                Log.d(TAG, "URL hình ảnh trống hoặc null");
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
                int quantityValue = Integer.parseInt(quantity.getText().toString());
                boolean success = cartController.addToCart(product.getId(), quantityValue);

                if (success) {
                    Toast.makeText(this, product.getName() + " đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();

                    // Cập nhật badge giỏ hàng nếu có toolbarHelper
                    if (toolbarHelper != null) {
                        toolbarHelper.updateCartBadge();
                    }
                } else {
                    Toast.makeText(this, "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Thông tin sản phẩm không khả dụng", Toast.LENGTH_SHORT).show();
            }
        }

        private void updateStockStatus(int stockQuantity) {
            if (stockQuantity > 0) {
                if (txtStockStatus instanceof Chip) {
                    // Nếu là Chip
                    txtStockStatus.setText("Còn hàng");
                    txtStockStatus.setTextColor(Color.WHITE);
                    txtStockStatus.setChipBackgroundColorResource(android.R.color.holo_green_dark);
                } else if (txtStockStatus instanceof TextView) {
                    // Nếu là TextView thông thường
                    txtStockStatus.setText("Còn hàng");
                    txtStockStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                }
            } else {
                if (txtStockStatus instanceof Chip) {
                    // Nếu là Chip
                    txtStockStatus.setText("Hết hàng");
                    txtStockStatus.setTextColor(Color.WHITE);
                    txtStockStatus.setChipBackgroundColorResource(android.R.color.holo_red_dark);
                } else if (txtStockStatus instanceof TextView) {
                    // Nếu là TextView thông thường
                    txtStockStatus.setText("Hết hàng");
                    txtStockStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                }
            }
        }
    }