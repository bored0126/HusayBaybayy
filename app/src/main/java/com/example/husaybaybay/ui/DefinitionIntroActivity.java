package com.example.husaybaybay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.husaybaybay.R;

public class DefinitionIntroActivity extends AppCompatActivity {

    private Button btnStartDefinition;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition_intro);

        btnStartDefinition = findViewById(R.id.btnStartDefinition);
        btnBack = findViewById(R.id.btnBack);

        btnStartDefinition.setOnClickListener(v ->
                startActivity(new Intent(DefinitionIntroActivity.this, DefinitionGameActivity.class)));
                
        btnBack.setOnClickListener(v -> finish());
    }
}