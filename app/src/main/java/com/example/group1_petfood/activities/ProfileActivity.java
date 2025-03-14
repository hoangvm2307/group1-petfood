package com.example.group1_petfood.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.group1_petfood.controllers.CartController;
import com.example.group1_petfood.controllers.UserController;
import com.example.group1_petfood.models.User;
import com.example.group1_petfood.utils.ToolbarHelper;
import com.example.group1_petfood.utils.UserUtils;

import com.example.group1_petfood.R;
import com.google.android.material.navigation.NavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvFullName, tvEmail, tvPhone, tvAddress;
    private Button btnEditProfile, btnLogout;
    private UserController userController;
    private ToolbarHelper toolbarHelper;
    private DrawerLayout drawerLayout;
    private CartController cartController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        drawerLayout = findViewById(R.id.drawer_layout);
        userController = new UserController(this);
        //-------------
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbarHelper = new ToolbarHelper(this, drawerLayout, cartController);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toolbarHelper.setupNavigationDrawer(navigationView);

        tvUsername = findViewById(R.id.tvUsername);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);

        loadUserProfile();

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logoutUser());

    }

    private void loadUserProfile() {
        int userId = UserUtils.getCurrentUserId(this);
        User currentUser = userController.getUserById(userId);

        if (currentUser != null) {
            setStyledText(tvUsername, "Username: ", currentUser.getUsername());
            setStyledText(tvFullName, "Full Name: ", currentUser.getFullName());
            setStyledText(tvEmail, "Email: ", currentUser.getEmail());
            setStyledText(tvPhone, "Phone: ", currentUser.getPhone());
            setStyledText(tvAddress, "Address: ", currentUser.getAddress());
        } else {
            Toast.makeText(this, "Failed to load user profile.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setStyledText(TextView textView, String label, String value) {
        SpannableString spannableString = new SpannableString(label + value);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.custom_green)),
                0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }

    private void logoutUser() {
        SharedPreferences preferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}