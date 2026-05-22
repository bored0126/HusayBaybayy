package com.example.husaybaybay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.husaybaybay.R;
import com.example.husaybaybay.util.AnimationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvError, tvGoToRegister;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        progressBar = findViewById(R.id.progressBar);

        // Apply touch feedback animations
        AnimationHelper.applyScaleAnimation(btnLogin);
        AnimationHelper.applyScaleAnimation(tvGoToRegister);

        // Always set up click listeners first (prevents freeze if auto-login fails)
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            setLoading(true);
            checkUserRoleAndNavigate(currentUser.getUid());
        } else {
            // Apply entrance animations if no user session
            View llLogoSection = findViewById(R.id.llLogoSection);
            View llLoginCard = findViewById(R.id.llLoginCard);
            if (llLogoSection != null && llLoginCard != null) {
                llLogoSection.setAlpha(0f);
                llLogoSection.setTranslationY(50f);
                llLogoSection.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100).start();

                llLoginCard.setAlpha(0f);
                llLoginCard.setTranslationY(80f);
                llLoginCard.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(300).start();
            }
        }
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email)) {
            showError("Ilagay ang iyong email.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showError("Ilagay ang iyong password.");
            return;
        }
        if (password.length() < 6) {
            showError("Ang password ay dapat may 6 na karakter.");
            return;
        }

        setLoading(true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRoleAndNavigate(user.getUid());
                        }
                    } else {
                        setLoading(false);
                        String errorMsg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Hindi makapag-login.";
                        Log.e(TAG, "Login failed: " + errorMsg);
                        // Translate common Firebase errors to Filipino
                        if (errorMsg.contains("password is invalid") || errorMsg.contains("INVALID_LOGIN_CREDENTIALS")) {
                            showError("Mali ang email o password. Subukan muli.");
                        } else if (errorMsg.contains("no user record")) {
                            showError("Walang account na may ganitong email.");
                        } else if (errorMsg.contains("badly formatted")) {
                            showError("Hindi wastong format ng email.");
                        } else {
                            showError("Hindi makapag-login: " + errorMsg);
                        }
                    }
                });
    }

    private void checkUserRoleAndNavigate(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    setLoading(false);
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        Intent intent;
                        if ("admin".equals(role)) {
                            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, HomeActivity.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // User exists in Auth but not in Firestore
                        // Create the Firestore document for them, then navigate
                        Log.w(TAG, "User has no Firestore doc, creating one as player...");
                        createMissingUserDoc(uid);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore read failed: " + e.getMessage(), e);
                    setLoading(false);
                    // Firestore read failed (likely permissions issue)
                    // Still navigate as player since they ARE authenticated
                    navigateToHome();
                });
    }

    /**
     * Creates a Firestore user document for accounts that were created
     * directly in Firebase Console (they won't have a Firestore doc)..
     */
    private void createMissingUserDoc(String uid) {
        FirebaseUser user = mAuth.getCurrentUser();
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user != null && user.getDisplayName() != null ? user.getDisplayName() : "Player");
        userData.put("email", user != null ? user.getEmail() : "");
        userData.put("role", "player");
        userData.put("createdAt", com.google.firebase.Timestamp.now());
        userData.put("game1LastScore", 0);
        userData.put("game1HighScore", 0);
        userData.put("game2WordsCompleted", 0);
        userData.put("game3WordsCompleted", 0);

        db.collection("users").document(uid).set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Created missing user doc for " + uid);
                    navigateToHome();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to create user doc: " + e.getMessage());
                    // Navigate anyway — they're authenticated
                    navigateToHome();
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);
        if (loading) {
            tvError.setVisibility(View.GONE);
        }
    }
}
