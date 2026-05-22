package com.example.husaybaybay.data.model;

public class Game2Question {
    private int id;
    private int questionNumber;
    private String correctAnswer;
    private String audioFileName;

    public Game2Question(int id, int questionNumber, String correctAnswer, String audioFileName) {
        this.id = id;
        this.questionNumber = questionNumber;
        this.correctAnswer = correctAnswer;
        this.audioFileName = audioFileName;
    }

    public int getId() {
        return id;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getAudioFileName() {
        return audioFileName;
    }
}