package com.example.group1_petfood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.group1_petfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailRegister, passwordRegister, confirmPassword, lastName, firstName, birthDate;
    private RadioGroup genderGroup;
    private TextView tvAlreadyAccount;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        confirmPassword = findViewById(R.id.confirmPassword);
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


        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
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