package com.example.group1_petfood.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group1_petfood.R;
import com.example.group1_petfood.controllers.UserController;
import com.example.group1_petfood.models.User;
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
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final String PREF_NAME = "user_pref";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private EditText emailLogin, passwordLogin;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private ProgressBar progressBar;
    private com.google.android.gms.common.SignInButton btnGoogleSignIn;

    private FirebaseAuth mAuth;
    private SignInClient signInClient;
    private UserController userController;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        signInClient = Identity.getSignInClient(this);

        // Khởi tạo UserController
        userController = new UserController(this);

        // Khởi tạo SharedPreferences để lưu trạng thái đăng nhập
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Kiểm tra người dùng đã đăng nhập chưa
        if (isLoggedIn()) {
            // Chuyển đến MainActivity nếu người dùng đã đăng nhập
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Ánh xạ UI components
        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        progressBar = findViewById(R.id.progressBar); // Thêm ProgressBar vào layout

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

        // Hiển thị progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Kiểm tra đăng nhập trong cơ sở dữ liệu SQLite
        User user = userController.checkLogin(email, password);

        if (user != null) {
            // Đăng nhập thành công trong SQLite
            saveLoginState(user.getId());

            // Đăng nhập Firebase để duy trì tính nhất quán
            loginWithFirebase(email, password);
        } else {
            // Nếu không tìm thấy trong SQLite, thử đăng nhập bằng Firebase
            loginWithFirebase(email, password);
        }
    }

    // Đăng nhập bằng Firebase
    private void loginWithFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Lưu trạng thái đăng nhập nếu chưa lưu từ SQLite
                    if (!isLoggedIn()) {
                        saveLoginState(1); // Giả sử ID mặc định là 1 nếu không tìm thấy trong SQLite
                    }

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xử lý đăng nhập bằng Google
    private void signInWithGoogle() {
        progressBar.setVisibility(View.VISIBLE);

        GetSignInIntentRequest signInIntentRequest = GetSignInIntentRequest.builder()
                .setServerClientId("735213204387-io3qss1skm1rmi7nrcrbr8rs79cpj0ip.apps.googleusercontent.com") // Lấy từ `google-services.json`
                .build();

        signInClient.getSignInIntent(signInIntentRequest).addOnSuccessListener(pendingIntent -> {
            try {
                startIntentSenderForResult(pendingIntent.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "Lỗi khi mở Google Sign-In", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
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
                String email = credential.getId();

                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, task -> {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    // Đăng nhập Firebase thành công
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                    // Kiểm tra và tạo tài khoản trong SQLite nếu chưa có
                                    if (!userController.isEmailExists(email)) {
                                        User newUser = new User();
                                        newUser.setEmail(email);
                                        newUser.setUsername(email.split("@")[0]); // Tạo username từ email
                                        newUser.setFullName(credential.getDisplayName() != null ?
                                                credential.getDisplayName() : email.split("@")[0]);
                                        newUser.setPassword("google_login"); // Mật khẩu giả

                                        long userId = userController.registerUser(newUser);
                                        if (userId != -1) {
                                            saveLoginState((int) userId);
                                        }
                                    } else {
                                        // Nếu đã tồn tại, lấy thông tin người dùng
                                        User existingUser = userController.checkLogin(email, "google_login");
                                        if (existingUser != null) {
                                            saveLoginState(existingUser.getId());
                                        }
                                    }

                                    Toast.makeText(LoginActivity.this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Đăng nhập Google thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } catch (ApiException e) {
                progressBar.setVisibility(View.GONE);
                Log.e("Google SignIn", "Lỗi đăng nhập Google", e);
            }
        }
    }

    // Lưu trạng thái đăng nhập
    private void saveLoginState(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // Kiểm tra trạng thái đăng nhập
    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}