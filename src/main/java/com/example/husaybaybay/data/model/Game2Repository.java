package com.example.husaybaybay.data.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Game2Repository {

    public static List<Game2Question> loadQuestions(Context context) {
        List<Game2Question> questionList = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open("game2_questions.json");
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
                String audioFileName = obj.getString("audioFileName");

                Game2Question question = new Game2Question(
                        id,
                        questionNumber,
                        correctAnswer,
                        audioFileName
                );

                questionList.add(question);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return questionList;
    }
}