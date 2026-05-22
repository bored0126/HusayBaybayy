package com.example.husaybaybay.data.model;

import java.util.List;

public class WordItem {
    private int id;
    private String correctWord;
    private String meaning;
    private String definitionClue;
    private List<String> choices;
    private String audioFileName;

    public WordItem(int id, String correctWord, String meaning, String definitionClue,
                    List<String> choices, String audioFileName) {
        this.id = id;
        this.correctWord = correctWord;
        this.meaning = meaning;
        this.definitionClue = definitionClue;
        this.choices = choices;
        this.audioFileName = audioFileName;
    }

    public int getId() {
        return id;
    }

    public String getCorrectWord() {
        return correctWord;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getDefinitionClue() {
        return definitionClue;
    }

    public List<String> getChoices() {
        return choices;
    }

    public String getAudioFileName() {
        return audioFileName;
    }
}