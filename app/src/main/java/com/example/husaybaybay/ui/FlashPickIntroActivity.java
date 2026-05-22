package com.example.husaybaybay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.husaybaybay.R;

public class FlashPickIntroActivity extends AppCompatActivity {

    private Button btnStartFlashPick;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_pick_intro);

        btnStartFlashPick = findViewById(R.id.btnStartFlashPick);
        btnBack = findViewById(R.id.btnBack);

        btnStartFlashPick.setOnClickListener(v ->
                startActivity(new Intent(FlashPickIntroActivity.this, FlashPickGameActivity.class)));
                
        btnBack.setOnClickListener(v -> finish());
    }
}