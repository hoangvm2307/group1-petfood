    package com.example.group1_petfood.activities;

    import android.app.PendingIntent;
    import android.content.Intent;
    import android.content.IntentSender;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.util.Log;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.example.group1_petfood.R;
    import com.google.android.gms.auth.api.identity.BeginSignInRequest;
    import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
    import com.google.android.gms.auth.api.identity.Identity;
    import com.google.android.gms.auth.api.identity.SignInClient;
    import com.google.android.gms.auth.api.identity.SignInCredential;
    import com.google.android.gms.common.api.ApiException;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.AuthCredential;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.auth.GoogleAuthProvider;

    public class LoginActivity extends AppCompatActivity {
        private static final int RC_SIGN_IN = 9001;
        private EditText emailLogin, passwordLogin;
        private Button btnLogin;
        private TextView tvRegister, tvForgotPassword;
        private com.google.android.gms.common.SignInButton btnGoogleSignIn;

        private FirebaseAuth mAuth;
        private SignInClient signInClient;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_login);

            mAuth = FirebaseAuth.getInstance();
            signInClient = Identity.getSignInClient(this);

            // Ánh xạ UI components
            emailLogin = findViewById(R.id.emailLogin);
            passwordLogin = findViewById(R.id.passwordLogin);
            btnLogin = findViewById(R.id.btnLogin);
            tvRegister = findViewById(R.id.tvRegister);
            tvForgotPassword = findViewById(R.id.tvForgotPassword);
            btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);

            btnLogin.setOnClickListener(v -> loginUser());
            tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
            tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
            btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        }

        // Xử lý đăng nhập bằng Email & Mật khẩu
        private void loginUser() {
            String email = emailLogin.getText().toString().trim();
            String password = passwordLogin.getText().toString().trim();

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

        // Xử lý đăng nhập bằng Google
        private void signInWithGoogle() {
            GetSignInIntentRequest signInIntentRequest = GetSignInIntentRequest.builder()
                    .setServerClientId("735213204387-io3qss1skm1rmi7nrcrbr8rs79cpj0ip.apps.googleusercontent.com") // Lấy từ `google-services.json`
                    .build();

            signInClient.getSignInIntent(signInIntentRequest).addOnSuccessListener(pendingIntent -> {
                try {
                    startIntentSenderForResult(pendingIntent.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Lỗi khi mở Google Sign-In", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(LoginActivity.this, "Không thể đăng nhập bằng Google", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_SIGN_IN) {
                try {
                    SignInCredential credential = signInClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken != null) {
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Đăng nhập Google thất bại!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } catch (ApiException e) {
                    Log.e("Google SignIn", "Lỗi đăng nhập Google", e);
                }
            }
        }
    }