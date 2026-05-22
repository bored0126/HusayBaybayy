package com.example.husaybaybay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.husaybaybay.R;
import com.example.husaybaybay.util.AnimationHelper;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String ADMIN_SECRET_CODE = "husaybaybay2026";

    private EditText etName, etEmail, etPassword, etConfirmPassword, etAdminCode;
    private SwitchMaterial switchAdmin;
    private LinearLayout llAdminCode;
    private Button btnRegister;
    private TextView tvError, tvGoToLogin, tvSectionLabel;
    private ProgressBar progressBar;
    private Spinner spinnerSection;

    private List<String> sectionsList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etAdminCode = findViewById(R.id.etAdminCode);
        switchAdmin = findViewById(R.id.switchAdmin);
        llAdminCode = findViewById(R.id.llAdminCode);
        btnRegister = findViewById(R.id.btnRegister);
        tvError = findViewById(R.id.tvError);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        tvSectionLabel = findViewById(R.id.tvSectionLabel);
        progressBar = findViewById(R.id.progressBar);
        spinnerSection = findViewById(R.id.spinnerSection);

        // Apply touch feedback animations
        AnimationHelper.applyScaleAnimation(btnRegister);
        AnimationHelper.applyScaleAnimation(tvGoToLogin);

        // Apply entrance animations
        View llLogoSection = findViewById(R.id.llLogoSection);
        View llRegisterCard = findViewById(R.id.llRegisterCard);
        if (llLogoSection != null && llRegisterCard != null) {
            llLogoSection.setAlpha(0f);
            llLogoSection.setTranslationY(50f);
            llLogoSection.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100).start();

            llRegisterCard.setAlpha(0f);
            llRegisterCard.setTranslationY(80f);
            llRegisterCard.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(300).start();
        }

        loadSections();

        // Toggle admin code visibility
        switchAdmin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llAdminCode.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            tvSectionLabel.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            spinnerSection.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });

        btnRegister.setOnClickListener(v -> attemptRegister());

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadSections() {
        db.collection("sections").orderBy("name").addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                showError("Hindi makuha ang mga seksyon: " + error.getMessage());
                return;
            }

            if (snapshots != null) {
                tvError.setVisibility(View.GONE);
                sectionsList.clear();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    String name = doc.getString("name");
                    if (name != null) sectionsList.add(name);
                }

                if (sectionsList.isEmpty()) {
                    sectionsList.add("Walang Seksyon");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        R.layout.item_spinner_selected, sectionsList);
                adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
                spinnerSection.setAdapter(adapter);
            }
        });
    }

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        boolean isAdmin = switchAdmin.isChecked();
        String adminCode = etAdminCode.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            showError("Ilagay ang iyong pangalan.");
            return;
        }
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
        if (!password.equals(confirmPassword)) {
            showError("Hindi magkatugma ang mga password.");
            return;
        }
        if (isAdmin && !adminCode.equals(ADMIN_SECRET_CODE)) {
            showError("Mali ang admin code. Subukan muli.");
            return;
        }
        if (!isAdmin && sectionsList.get(0).equals("Walang Seksyon")) {
            showError("Wala pang seksyong ginawa ang admin.");
            return;
        }

        setLoading(true);

        String role = isAdmin ? "admin" : "player";
        String section = isAdmin ? "Admin" : spinnerSection.getSelectedItem().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user.getUid(), name, email, role, section);
                        }
                    } else {
                        setLoading(false);
                        String errorMsg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Hindi makapag-register.";
                        if (errorMsg.contains("email address is already")) {
                            showError("May account na ang email na ito.");
                        } else if (errorMsg.contains("badly formatted")) {
                            showError("Hindi wastong format ng email.");
                        } else if (errorMsg.contains("weak password")) {
                            showError("Masyadong mahina ang password.");
                        } else {
                            showError("Hindi makapag-register: " + errorMsg);
                        }
                    }
                });
    }

    private void saveUserToFirestore(String uid, String name, String email, String role, String section) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("role", role);
        userData.put("section", section);
        userData.put("createdAt", com.google.firebase.Timestamp.now());

        // Initialize scores for players
        if ("player".equals(role)) {
            userData.put("game1LastScore", 0);
            userData.put("game1HighScore", 0);
            userData.put("game2WordsCompleted", 0);
            userData.put("game3WordsCompleted", 0);
        }

        db.collection("users").document(uid).set(userData)
                .addOnSuccessListener(aVoid -> {
                    setLoading(false);
                    // Sign out because Firebase automatically logs in the newly registered user.
                    mAuth.signOut();
                    
                    Toast.makeText(RegisterActivity.this, "Matagumpay na nakagawa ng account! Mangyaring mag-login.", Toast.LENGTH_LONG).show();
                    
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showError("Hindi ma-save ang account. Subukan muli.");
                });
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
        etName.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);
        etConfirmPassword.setEnabled(!loading);
        etAdminCode.setEnabled(!loading);
        switchAdmin.setEnabled(!loading);
        if (loading) {
            tvError.setVisibility(View.GONE);
        }
    }
}
