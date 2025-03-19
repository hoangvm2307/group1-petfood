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
import com.example.group1_petfood.models.UserRole;
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
    private static final String KEY_USER_ROLE = "user_role";
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

        mAuth = FirebaseAuth.getInstance();
        signInClient = Identity.getSignInClient(this);
        userController = new UserController(this);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        progressBar = findViewById(R.id.progressBar);

        userController.createDefaultAdminIfNeeded();

        if (isLoggedIn()) {
            redirectBasedOnRole();
            return;
        }

        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
    }

    private void loginUser() {
        String email = emailLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        User user = userController.checkLogin(email, password);

        if (user != null) {
            saveLoginState(user.getId(), user.getRole());
            redirectBasedOnRole();
        } else {
            loginWithFirebase(email, password);
        }
    }

    private void loginWithFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    if (!isLoggedIn()) {
                        User dbUser = userController.checkLogin(email, password);
                        if (dbUser != null) {
                            saveLoginState(dbUser.getId(), dbUser.getRole());
                        } else {
                            saveLoginState(1, UserRole.CUSTOMER);
                        }
                    }
                    redirectBasedOnRole();
                }
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                    if (!userController.isEmailExists(email)) {
                                        User newUser = new User();
                                        newUser.setEmail(email);
                                        newUser.setUsername(email.split("@")[0]);
                                        newUser.setFullName(credential.getDisplayName() != null ?
                                                credential.getDisplayName() : email.split("@")[0]);
                                        newUser.setPassword("google_login"); // Mật khẩu giả

                                        long userId = userController.registerUser(newUser);
                                        if (userId != -1) {
                                            saveLoginState((int) userId, UserRole.CUSTOMER);
                                        }
                                    } else {
                                        User existingUser = userController.checkLogin(email, "google_login");
                                        if (existingUser != null) {

                                            saveLoginState(existingUser.getId(), existingUser.getRole());
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

    private void saveLoginState(int userId, UserRole role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ROLE, role.getRoleName());
        editor.apply();
    }
    private void redirectBasedOnRole() {
        progressBar.setVisibility(View.GONE);

        String roleStr = sharedPreferences.getString(KEY_USER_ROLE, UserRole.CUSTOMER.getRoleName());
        UserRole role = UserRole.fromString(roleStr);

        Intent intent;

        switch (role) {
            case ADMIN:
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                break;
            case STAFF:
                intent = new Intent(LoginActivity.this, AdminProductActivity.class);
                break;
            default:
                intent = new Intent(LoginActivity.this, MainActivity.class);
                break;
        }

        startActivity(intent);
        finish();
    }
    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}