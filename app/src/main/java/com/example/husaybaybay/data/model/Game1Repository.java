package com.example.husaybaybay.data.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Game1Repository {

    public static List<Game1Question> loadQuestions(Context context) {
        List<Game1Question> questionList = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open("game1_questions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                int id = obj.getInt("id");
                int questionNumber = obj.getInt("questionNumber");
                String correctAnswer = obj.getString("correctAnswer");

                JSONArray choicesArray = obj.getJSONArray("choices");
                List<String> choices = new ArrayList<>();

                for (int j = 0; j < choicesArray.length(); j++) {
                    choices.add(choicesArray.getString(j));
                }

                Game1Question question = new Game1Question(
                        id,
                        questionNumber,
                        choices,
                        correctAnswer
                );

                questionList.add(question);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return questionList;
    }
}