package com.example.husaybaybay.data.model;

/**
 * Data model representing a player's scores across all three games.
 * Used in the Admin Dashboard to display player progress.
 */
public class PlayerScore {

    private String uid;
    private String name;
    private String email;

    // Game 1 (FlashPick) - score out of 30 questions
    private int game1LastScore;
    private int game1HighScore;

    // Game 2 (DinigBaybay / Audio) - words completed out of total dictionary words
    private int game2WordsCompleted;

    // Game 3 (IsipBaybay / Definition) - words completed out of total dictionary words
    private int game3WordsCompleted;

    private String section;

    public PlayerScore() {
        // Required empty constructor for Firestore
    }

    public PlayerScore(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.game1LastScore = 0;
        this.game1HighScore = 0;
        this.game2WordsCompleted = 0;
        this.game3WordsCompleted = 0;
    }

    // Getters and setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getGame1LastScore() { return game1LastScore; }
    public void setGame1LastScore(int game1LastScore) { this.game1LastScore = game1LastScore; }

    public int getGame1HighScore() { return game1HighScore; }
    public void setGame1HighScore(int game1HighScore) { this.game1HighScore = game1HighScore; }

    public int getGame2WordsCompleted() { return game2WordsCompleted; }
    public void setGame2WordsCompleted(int game2WordsCompleted) { this.game2WordsCompleted = game2WordsCompleted; }

    public int getGame3WordsCompleted() { return game3WordsCompleted; }
    public void setGame3WordsCompleted(int game3WordsCompleted) { this.game3WordsCompleted = game3WordsCompleted; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
}
