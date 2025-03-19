package com.example.group1_petfood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group1_petfood.R;
import com.example.group1_petfood.controllers.UserController;
import com.example.group1_petfood.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailRegister, passwordRegister, confirmPassword, lastName, firstName, birthDate;
    private RadioGroup genderGroup;
    private TextView tvAlreadyAccount;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Firebase Auth (giữ nguyên để tương thích với mã hiện tại)
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo UserController để lưu người dùng vào SQLite
        userController = new UserController(this);

        // Ánh xạ view
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        confirmPassword = findViewById(R.id.confirmPassword);
        usernameRegister = findViewById(R.id.usernameRegister); // Cần thêm vào layout
        fullNameRegister = findViewById(R.id.fullNameRegister); // Cần thêm vào layout
        btnRegister = findViewById(R.id.btnRegister);
        tvAlreadyAccount = findViewById(R.id.tvAlreadyAccount);
        lastName = findViewById(R.id.lastName);
        firstName = findViewById(R.id.firstName);
        birthDate = findViewById(R.id.birthDate);
        genderGroup = findViewById(R.id.genderGroup);


        btnRegister.setOnClickListener(v -> registerUser());
        tvAlreadyAccount.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private void registerUser() {
        String email = emailRegister.getText().toString();
        String password = passwordRegister.getText().toString();
        String confirmPass = confirmPassword.getText().toString();
        String gender = "";
        String lastNameStr = lastName.getText().toString().trim();
        String firstNameStr = firstName.getText().toString().trim();
        String birthDateStr = birthDate.getText().toString().trim();
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        String genderStr = selectedGenderId == R.id.radioMale ? "Nam" : "Nữ";


        // Kiểm tra dữ liệu nhập vào
        if (TextUtils.isEmpty(email)) {
            emailRegister.setError("Vui lòng nhập email");
            emailRegister.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailRegister.setError("Vui lòng nhập email hợp lệ");
            emailRegister.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            usernameRegister.setError("Vui lòng nhập tên đăng nhập");
            usernameRegister.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(fullName)) {
            fullNameRegister.setError("Vui lòng nhập họ và tên");
            fullNameRegister.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordRegister.setError("Vui lòng nhập mật khẩu");
            passwordRegister.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordRegister.setError("Mật khẩu phải có ít nhất 6 ký tự");
            passwordRegister.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPass)) {
            confirmPassword.setError("Vui lòng xác nhận mật khẩu");
            confirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPass)) {
            confirmPassword.setError("Mật khẩu không khớp");
            confirmPassword.requestFocus();
            return;
        }

        // Kiểm tra email đã tồn tại chưa
        if (userController.isEmailExists(email)) {
            emailRegister.setError("Email này đã được đăng ký");
            emailRegister.requestFocus();
            return;
        }

        // Hiển thị progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Tạo đối tượng User mới
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setUsername(username);
        newUser.setFullName(fullName);
        // Thêm một số thông tin mặc định hoặc để trống
        newUser.setPhone("");
        newUser.setAddress("");

        // Lưu thông tin vào cơ sở dữ liệu SQLite
        long userId = userController.registerUser(newUser);

        if (userId != -1) {
            // Đăng ký thành công trong SQLite, tiếp tục đăng ký trên Firebase
            registerOnFirebase(email, password);
        } else {
            // Đăng ký thất bại
            progressBar.setVisibility(View.GONE);
            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerOnFirebase(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful()) {
//                FirebaseUser user = mAuth.getCurrentUser();
//                if (user != null) {
//                    String userId = user.getUid();
//                    databaseReference.child(userId).setValue(email);
//
//                }
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    User newUser = new User(lastNameStr, firstNameStr, genderStr, birthDateStr, email);
                    databaseReference.child(userId).setValue(newUser);
                }
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    class User {
        public String lastName, firstName, gender, birthDate, email;

        public User(String lastName, String firstName, String gender, String birthDate, String email) {
            this.lastName = lastName;
            this.firstName = firstName;
            this.gender = gender;
            this.birthDate = birthDate;
            this.email = email;
        }
    }
}