    package com.example.group1_petfood.activities;

    import android.content.Intent;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.widget.Button;
    import android.widget.EditText;
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

    public class LoginActivity extends AppCompatActivity {
        private EditText emailLogin, passwordLogin;
        private Button btnLogin;
        private TextView tvRegister, tvForgotPassword;

        private FirebaseAuth mAuth;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_login);

            mAuth = FirebaseAuth.getInstance();

            emailLogin = findViewById(R.id.emailLogin);
            passwordLogin = findViewById(R.id.passwordLogin);
            btnLogin = findViewById(R.id.btnLogin);
            tvRegister = findViewById(R.id.tvRegister);
            tvForgotPassword = findViewById(R.id.tvForgotPassword);

            btnLogin.setOnClickListener(v -> loginUser());

            tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

            tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
        }

        private void loginUser() {
            String email = emailLogin.getText().toString();
            String password = passwordLogin.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }