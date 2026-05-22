package com.example.husaybaybay.data.model;

import java.util.List;

public class Game1Question {
    private int id;
    private int questionNumber;
    private List<String> choices;
    private String correctAnswer;

    public Game1Question(int id, int questionNumber, List<String> choices, String correctAnswer) {
        this.id = id;
        this.questionNumber = questionNumber;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public int getId() {
        return id;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public List<String> getChoices() {
        return choices;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}