package com.example.husaybaybay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.husaybaybay.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GameResultActivity extends AppCompatActivity {

    private TextView tvScoreValue;
    private Button btnProceedHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

        tvScoreValue = findViewById(R.id.tvScoreValue);
        btnProceedHome = findViewById(R.id.btnProceedHome);
        Button btnRestart = findViewById(R.id.btnRestart);
        
        android.widget.LinearLayout llScoreBox = findViewById(R.id.llScoreBox);
        android.widget.ImageView ivGameIcon = findViewById(R.id.ivGameIcon);
        android.widget.TextView tvCongrats = findViewById(R.id.tvCongrats);

        String gameType = getIntent().getStringExtra("gameType");
        int wordsCompleted = getIntent().getIntExtra("wordsCompleted", 0);

        if ("game2".equals(gameType) || "game3".equals(gameType)) {
            llScoreBox.setVisibility(android.view.View.GONE);
            ivGameIcon.setVisibility(android.view.View.GONE);
            tvCongrats.setVisibility(android.view.View.VISIBLE);

            // Save words completed to Firestore
            if ("game2".equals(gameType)) {
                saveWordsCompletedToFirestore("game2WordsCompleted", wordsCompleted);
            } else {
                saveWordsCompletedToFirestore("game3WordsCompleted", wordsCompleted);
            }

            btnRestart.setOnClickListener(v -> {
                Intent intent;
                if ("game2".equals(gameType)) {
                    intent = new Intent(GameResultActivity.this, AudioGameActivity.class);
                } else {
                    intent = new Intent(GameResultActivity.this, DefinitionGameActivity.class);
                }
                startActivity(intent);
                finish();
            });
        } else {
            int score = getIntent().getIntExtra("score", 0);
            tvScoreValue.setText(String.valueOf(score));

            // Save FlashPick score to Firestore
            saveGame1ScoreToFirestore(score);

            btnRestart.setOnClickListener(v -> {
                Intent intent = new Intent(GameResultActivity.this, FlashPickGameActivity.class);
                startActivity(intent);
                finish();
            });
        }

        btnProceedHome.setOnClickListener(v -> {
            Intent intent = new Intent(GameResultActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Save FlashPick (Game 1) score to Firestore.
     * Updates lastScore always and highScore only if new score is higher.
     */
    private void saveGame1ScoreToFirestore(int score) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = user.getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("game1LastScore", score);

                    int currentHigh = 0;
                    if (doc.exists()) {
                        Long high = doc.getLong("game1HighScore");
                        if (high != null) currentHigh = high.intValue();
                    }

                    if (score > currentHigh) {
                        updates.put("game1HighScore", score);
                    }

                    db.collection("users").document(uid).update(updates);
                });
    }

    /**
     * Save words completed for Game 2 (Audio) or Game 3 (Definition) to Firestore.
     * Only updates if the new count is higher than the stored count.
     */
    private void saveWordsCompletedToFirestore(String fieldName, int wordsCompleted) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = user.getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    int currentCount = 0;
                    if (doc.exists()) {
                        Long stored = doc.getLong(fieldName);
                        if (stored != null) currentCount = stored.intValue();
                    }

                    if (wordsCompleted > currentCount) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put(fieldName, wordsCompleted);
                        db.collection("users").document(uid).update(updates);
                    }
                });
    }
}