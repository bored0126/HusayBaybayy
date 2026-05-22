package com.example.husaybaybay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.husaybaybay.R;
import com.example.husaybaybay.util.AnimationHelper;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private Button btnDictionary, btnMaglaro, btnAbout, btnSettings, btnSupportDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnDictionary = findViewById(R.id.btnDictionary);
        btnMaglaro = findViewById(R.id.btnMaglaro);
        btnAbout = findViewById(R.id.btnAbout);
        btnSettings = findViewById(R.id.btnSettings);
        btnSupportDoc = findViewById(R.id.btnSupportDoc);

        // Apply scale animations to buttons
        AnimationHelper.applyScaleAnimation(btnDictionary);
        AnimationHelper.applyScaleAnimation(btnMaglaro);
        AnimationHelper.applyScaleAnimation(btnAbout);
        AnimationHelper.applyScaleAnimation(btnSettings);
        AnimationHelper.applyScaleAnimation(btnSupportDoc);

        btnDictionary.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, DictionaryListActivity.class)));

        btnMaglaro.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, GamesMenuActivity.class)));

        btnAbout.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AboutActivity.class)));

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class)));

        btnSupportDoc.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, PdfViewerActivity.class)));

        // Logout button
        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            AnimationHelper.applyScaleAnimation(btnLogout);
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}