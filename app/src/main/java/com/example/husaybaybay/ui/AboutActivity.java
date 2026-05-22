package com.example.husaybaybay.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.husaybaybay.R;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        android.widget.ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}