package com.example.group1_petfood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.group1_petfood.R;
import com.example.group1_petfood.controllers.CartController;
import com.example.group1_petfood.controllers.UserController;
import com.example.group1_petfood.models.User;
import com.example.group1_petfood.utils.ToolbarHelper;
import com.example.group1_petfood.utils.UserUtils;
import com.google.android.material.navigation.NavigationView;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etAddress;
    private Button btnSaveChanges;
    private UserController userController;
    private User currentUser;
    private ToolbarHelper toolbarHelper;
    private DrawerLayout drawerLayout;
    private CartController cartController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        userController = new UserController(this);
        toolbarHelper = new ToolbarHelper(this, drawerLayout, cartController);
        toolbarHelper.setupNavigationDrawer(navigationView);

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        loadCurrentUserProfile();

        btnSaveChanges.setOnClickListener(v -> saveProfileChanges());
    }

    private void loadCurrentUserProfile() {
        int userId = UserUtils.getCurrentUserId(this);
        currentUser = userController.getUserById(userId);

        if (currentUser != null) {
            etFullName.setText(currentUser.getFullName());
            etPhone.setText(currentUser.getPhone());
            etAddress.setText(currentUser.getAddress());
        } else {
            Toast.makeText(this, "Failed to load user profile.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfileChanges() {
        if (currentUser == null) {
            Toast.makeText(this, "User not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setFullName(etFullName.getText().toString().trim());
        currentUser.setPhone(etPhone.getText().toString().trim());
        currentUser.setAddress(etAddress.getText().toString().trim());

        boolean success = userController.updateUser(currentUser);

        if (success) {
            Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
        }
    }
}