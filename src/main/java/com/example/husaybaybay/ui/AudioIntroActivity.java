package com.example.husaybaybay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.husaybaybay.R;

public class AudioIntroActivity extends AppCompatActivity {

    private Button btnStartAudio;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_intro);

        btnStartAudio = findViewById(R.id.btnStartAudio);
        btnBack = findViewById(R.id.btnBack);

        btnStartAudio.setOnClickListener(v ->
                startActivity(new Intent(AudioIntroActivity.this, AudioGameActivity.class)));
                
        btnBack.setOnClickListener(v -> finish());
    }
}