package com.example.husaybaybay.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.husaybaybay.R;

public class WordDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        TextView tvWord = findViewById(R.id.tvWord);
        TextView tvMeaning = findViewById(R.id.tvMeaning);
        ImageButton btnBack = findViewById(R.id.btnBack);
        AppCompatButton btnSource = findViewById(R.id.btnSource);

        Intent intent = getIntent();
        final String finalWord;
        if (intent != null) {
            String word = intent.getStringExtra("WORD_TEXT");
            String meaning = intent.getStringExtra("WORD_MEANING");

            if (word != null) {
                tvWord.setText(word);
                finalWord = word;
            } else {
                finalWord = "";
            }
            if (meaning != null) {
                tvMeaning.setText(meaning);
            }
        } else {
            finalWord = "";
        }

        btnBack.setOnClickListener(v -> finish());

        if (btnSource != null) {
            btnSource.setOnClickListener(v -> {
                if (!finalWord.isEmpty()) {
                    String query = finalWord.toLowerCase().trim();
                    String url = "https://kwfdiksiyonaryo.ph/?query=" + Uri.encode(query);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });
        }
    }
}