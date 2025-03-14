package com.example.group1_petfood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.group1_petfood.R;
import com.example.group1_petfood.controllers.CartController;
import com.example.group1_petfood.controllers.UserController;
import com.example.group1_petfood.models.User;
import com.example.group1_petfood.utils.ToolbarHelper;
import com.example.group1_petfood.utils.UserUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPhone, etAddress;
    private Button btnSaveChanges, btnCancel, btnChangePhoto;
    private ImageView profileImageView;
    private UserController userController;
    private User currentUser;
    private ToolbarHelper toolbarHelper;
    private DrawerLayout drawerLayout;
    private CartController cartController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeControllers();
        initializeViews();
        setupNavigationDrawer();
        setupListeners();
        loadCurrentUserProfile();
    }

    private void initializeControllers() {
        userController = new UserController(this);
        cartController = new CartController(this);
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbarHelper = new ToolbarHelper(this, drawerLayout, cartController);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);

        profileImageView = findViewById(R.id.profileImageView);
    }

    private void setupNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        toolbarHelper.setupNavigationDrawer(navigationView);
    }

    private void setupListeners() {
        btnSaveChanges.setOnClickListener(v -> saveProfileChanges());

        btnCancel.setOnClickListener(v -> {
            // Quay về trang Profile mà không lưu thay đổi
            finish();
        });

        btnChangePhoto.setOnClickListener(v -> {
            // Tính năng này có thể phát triển sau
            Toast.makeText(this, "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCurrentUserProfile() {
        int userId = UserUtils.getCurrentUserId(this);
        currentUser = userController.getUserById(userId);

        if (currentUser != null) {
            etFullName.setText(currentUser.getFullName());
            etEmail.setText(currentUser.getEmail());
            etPhone.setText(currentUser.getPhone());
            etAddress.setText(currentUser.getAddress());
        } else {
            Toast.makeText(this, "Không thể tải thông tin người dùng.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfileChanges() {
        if (currentUser == null) {
            Toast.makeText(this, "Chưa tải được thông tin người dùng.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy dữ liệu từ form
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Kiểm tra dữ liệu
        if (fullName.isEmpty()) {
            etFullName.setError("Họ và tên không được để trống");
            etFullName.requestFocus();
            return;
        }

        // Cập nhật thông tin người dùng
        currentUser.setFullName(fullName);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        // Lưu vào cơ sở dữ liệu
        boolean success = userController.updateUser(currentUser);

        if (success) {
            Toast.makeText(this, "Cập nhật thông tin thành công.", Toast.LENGTH_SHORT).show();

            // Quay về trang Profile
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Không thể cập nhật thông tin.", Toast.LENGTH_SHORT).show();
        }
    }
}