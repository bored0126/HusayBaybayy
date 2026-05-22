package com.example.husaybaybay.data.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WordRepository {

    public static List<WordItem> loadWords(Context context) {
        List<WordItem> wordList = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open("words.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                int id = obj.getInt("id");
                String correctWord = obj.getString("correctWord");
                String meaning = obj.getString("meaning");
                String definitionClue = obj.getString("definitionClue");
                String audioFileName = obj.getString("audioFileName");

                JSONArray choicesArray = obj.getJSONArray("choices");
                List<String> choices = new ArrayList<>();
                for (int j = 0; j < choicesArray.length(); j++) {
                    choices.add(choicesArray.getString(j));
                }

                WordItem wordItem = new WordItem(
                        id,
                        correctWord,
                        meaning,
                        definitionClue,
                        choices,
                        audioFileName
                );

                wordList.add(wordItem);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return wordList;
    }
}