package com.example.husaybaybay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.husaybaybay.R;
import com.example.husaybaybay.util.AnimationHelper;

public class GamesMenuActivity extends AppCompatActivity {

    private Button btnFlashPick, btnAudioGame, btnDefinitionGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_menu);

        btnFlashPick = findViewById(R.id.btnFlashPick);
        btnAudioGame = findViewById(R.id.btnAudioGame);
        btnDefinitionGame = findViewById(R.id.btnDefinitionGame);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Apply scale animations to buttons
        AnimationHelper.applyScaleAnimation(btnFlashPick);
        AnimationHelper.applyScaleAnimation(btnAudioGame);
        AnimationHelper.applyScaleAnimation(btnDefinitionGame);
        AnimationHelper.applyScaleAnimation(btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnFlashPick.setOnClickListener(v ->
                startActivity(new Intent(GamesMenuActivity.this, FlashPickIntroActivity.class)));

        btnAudioGame.setOnClickListener(v ->
                startActivity(new Intent(GamesMenuActivity.this, AudioIntroActivity.class)));

        btnDefinitionGame.setOnClickListener(v ->
                startActivity(new Intent(GamesMenuActivity.this, DefinitionIntroActivity.class)));
    }
}