package com.example.husaybaybay.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.husaybaybay.R;
import com.example.husaybaybay.data.model.DictionaryRepository;
import com.example.husaybaybay.data.model.DictionaryRepository.DictionaryWord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryListActivity extends AppCompatActivity {

    private RecyclerView rvDictionary;
    private LinearLayout llAlphabetSidebar;
    private DictionaryAdapter adapter;
    private List<Object> listItems;
    private Map<String, Integer> letterPositions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary_list);

        rvDictionary = findViewById(R.id.rvDictionary);
        llAlphabetSidebar = findViewById(R.id.llAlphabetSidebar);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        List<DictionaryWord> words = DictionaryRepository.getDictionaryWords();
        Collections.sort(words, (w1, w2) -> w1.word.compareToIgnoreCase(w2.word));

        listItems = new ArrayList<>();
        letterPositions = new HashMap<>();

        String currentLetter = "";
        for (DictionaryWord dw : words) {
            String initial = dw.word.substring(0, 1).toUpperCase();
            if (!initial.equals(currentLetter)) {
                currentLetter = initial;
                listItems.add(currentLetter);
                letterPositions.put(currentLetter, listItems.size() - 1);
            }
            listItems.add(dw);
        }

        adapter = new DictionaryAdapter(this, listItems);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (listItems.get(position) instanceof String) {
                    return 2;
                }
                return 1;
            }
        });
        
        rvDictionary.setLayoutManager(layoutManager);
        rvDictionary.setAdapter(adapter);

        setupAlphabetSidebar();
    }

    private void setupAlphabetSidebar() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < alphabet.length(); i++) {
            String letter = String.valueOf(alphabet.charAt(i));
            TextView tvLetter = new TextView(this);
            tvLetter.setText(letter);
            tvLetter.setTextColor(0xFFFFFFFF);
            tvLetter.setShadowLayer(3f, 0f, 2f, 0xAA000000);
            tvLetter.setTextSize(12f);
            tvLetter.setTypeface(null, Typeface.BOLD);
            tvLetter.setGravity(Gravity.CENTER);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
            tvLetter.setLayoutParams(params);
            
            tvLetter.setOnClickListener(v -> {
                if (letterPositions.containsKey(letter)) {
                    int pos = letterPositions.get(letter);
                    ((GridLayoutManager) rvDictionary.getLayoutManager()).scrollToPositionWithOffset(pos, 0);
                }
            });
            
            llAlphabetSidebar.addView(tvLetter);
        }
    }
}