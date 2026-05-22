package com.example.husaybaybay.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import com.example.husaybaybay.R;
import com.example.husaybaybay.data.model.Game2Question;
import com.example.husaybaybay.data.model.Game2Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioGameActivity extends AppCompatActivity {

    private TextView tvAudioCounter;
    private LinearLayout btnPlayAudio;
    private EditText etAnswer;
    private ImageButton btnPause;
    private ImageButton btnHint;
    private LinearLayout llHintClueContainer;
    private TextView tvHintSyllables;
    private TextView tvHintDefinition;

    private List<Game2Question> questionList;
    private int currentIndex = 0;
    private boolean isTransitioning = false;
    private boolean isPaused = false;
    private boolean hintUsed = false;
    private int audioPlayCount = 0;

    private MediaPlayer mediaPlayer;

    private static final String PREFS_NAME = "HusayBaybayPrefs";
    private static final String KEY_GAME2_INDEX = "game2_current_index";
    private static final String KEY_GAME2_ORDER = "game2_question_order";
    private static final String KEY_GAME2_ANSWER = "game2_current_answer";
    private static final String KEY_VOICE_VOLUME = "voice_volume";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_game);

        tvAudioCounter = findViewById(R.id.tvAudioCounter);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);
        etAnswer = findViewById(R.id.etAnswer);
        btnPause = findViewById(R.id.btnPause);
        btnHint = findViewById(R.id.btnHint);
        llHintClueContainer = findViewById(R.id.llHintClueContainer);
        tvHintSyllables = findViewById(R.id.tvHintSyllables);
        tvHintDefinition = findViewById(R.id.tvHintDefinition);

        questionList = Game2Repository.loadQuestions(this);

        loadSavedProgressOrShuffle();

        if (questionList.size() > 30) {
            questionList = questionList.subList(0, 30);
        }

        showQuestion();

        btnPause.setOnClickListener(v -> showPauseDialog());
        btnHint.setOnClickListener(v -> useHint());

        btnPlayAudio.setOnClickListener(v -> playCurrentAudio(true));

        etAnswer.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                    (event != null
                            && event.getAction() == android.view.KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) {
                checkAnswer();
                return true;
            }
            return false;
        });

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });
    }

    private void loadSavedProgressOrShuffle() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedIndex = prefs.getInt(KEY_GAME2_INDEX, -1);
        String savedOrder = prefs.getString(KEY_GAME2_ORDER, null);

        if (savedIndex != -1 && savedOrder != null && !savedOrder.isEmpty()) {
            List<Game2Question> reorderedList = rebuildQuestionOrder(savedOrder);
            if (!reorderedList.isEmpty()) {
                questionList = reorderedList;
                currentIndex = savedIndex;
            } else {
                Collections.shuffle(questionList);
            }
        } else {
            Collections.shuffle(questionList);
        }
    }

    private List<Game2Question> rebuildQuestionOrder(String savedOrder) {
        List<Game2Question> reordered = new ArrayList<>();
        String[] audioNames = savedOrder.split(",");

        for (String audioName : audioNames) {
            for (Game2Question question : Game2Repository.loadQuestions(this)) {
                if (question.getAudioFileName().equals(audioName)) {
                    reordered.add(question);
                    break;
                }
            }
        }

        return reordered;
    }

    private void saveProgress() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(KEY_GAME2_INDEX, currentIndex);
        editor.putString(KEY_GAME2_ORDER, buildQuestionOrderString());
        editor.putString(KEY_GAME2_ANSWER, etAnswer.getText().toString().trim());

        editor.apply();
    }

    private void clearProgress() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_GAME2_INDEX)
                .remove(KEY_GAME2_ORDER)
                .remove(KEY_GAME2_ANSWER)
                .apply();
    }

    private String buildQuestionOrderString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < questionList.size(); i++) {
            builder.append(questionList.get(i).getAudioFileName());
            if (i < questionList.size() - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    private void showPauseDialog() {
        if (isTransitioning || isPaused) return;
        isPaused = true;
        
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_pause);
        dialog.setCancelable(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        Button btnDialogQuit = dialog.findViewById(R.id.btnDialogQuit);
        Button btnDialogResume = dialog.findViewById(R.id.btnDialogResume);

        btnDialogQuit.setOnClickListener(v -> {
            dialog.dismiss();
            showExitDialog();
        });

        btnDialogResume.setOnClickListener(v -> {
            isPaused = false;
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
            dialog.dismiss();
        });

        dialog.show();
    }
    
    private void useHint() {
        if (hintUsed || isTransitioning || isPaused) return;
        hintUsed = true;
        
        btnHint.animate().cancel();
        btnHint.setScaleX(1.0f);
        btnHint.setScaleY(1.0f);
        btnHint.setVisibility(android.view.View.GONE);
        
        String correctAnswer = questionList.get(currentIndex).getCorrectAnswer().trim().toUpperCase();
        if (!correctAnswer.isEmpty()) {
            // Option A (Part 1): Fetch dictionary definition
            String meaning = "";
            for (com.example.husaybaybay.data.model.DictionaryRepository.DictionaryWord d : com.example.husaybaybay.data.model.DictionaryRepository.getDictionaryWords()) {
                if (d.word.equalsIgnoreCase(correctAnswer)) {
                    meaning = d.meaning;
                    break;
                }
            }
            if (meaning.isEmpty()) {
                meaning = "Kahulugan ng salita.";
            }
            tvHintDefinition.setText("Kahulugan: " + meaning);

            // Option C: Build syllable blanks with first letters
            String[] syllables = getSyllables(correctAnswer);
            StringBuilder syllableClue = new StringBuilder();
            for (int i = 0; i < syllables.length; i++) {
                String syl = syllables[i];
                if (syl.length() > 0) {
                    syllableClue.append("[ ").append(syl.charAt(0));
                    for (int j = 1; j < syl.length(); j++) {
                        syllableClue.append(" _");
                    }
                    syllableClue.append(" ]");
                    if (i < syllables.length - 1) {
                        syllableClue.append("  ");
                    }
                }
            }
            tvHintSyllables.setText(syllableClue.toString());

            // Slide and Fade In Animation for the card
            llHintClueContainer.setVisibility(android.view.View.VISIBLE);
            llHintClueContainer.setAlpha(0f);
            llHintClueContainer.setTranslationY(20f);
            llHintClueContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
        }
    }

    private void showExitDialog() {
        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_exit_game);
        dialog.setCancelable(true);

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvDialogMessage = dialog.findViewById(R.id.tvDialogMessage);
        Button btnDialogNo = dialog.findViewById(R.id.btnDialogNo);
        Button btnDialogYes = dialog.findViewById(R.id.btnDialogYes);

        Runnable resumeAction = () -> {
            isPaused = false;
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        };

        btnDialogNo.setOnClickListener(v -> {
            dialog.dismiss();
            resumeAction.run();
        });

        dialog.setOnCancelListener(d -> {
            resumeAction.run();
        });

        btnDialogYes.setOnClickListener(v -> {
            saveProgress();
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }

    private void showQuestion() {
        if (currentIndex >= questionList.size()) {
            clearProgress();
            goToResultScreen();
            return;
        }

        isTransitioning = false;
        hintUsed = false;
        audioPlayCount = 0;
        
        btnHint.animate().cancel();
        btnHint.setScaleX(1.0f);
        btnHint.setScaleY(1.0f);
        btnHint.setVisibility(android.view.View.GONE);
        
        if (llHintClueContainer != null) {
            llHintClueContainer.setVisibility(android.view.View.GONE);
            llHintClueContainer.setAlpha(0f);
            tvHintSyllables.setText("");
            tvHintDefinition.setText("");
        }
        
        etAnswer.setText(getSavedAnswerIfCurrent());
        etAnswer.setBackgroundResource(R.drawable.bg_choice_blue);
        etAnswer.setEnabled(true);
        etAnswer.requestFocus();

        tvAudioCounter.setText(String.valueOf(currentIndex + 1));

        playCurrentAudio(false);
    }

    private String getSavedAnswerIfCurrent() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_GAME2_ANSWER, "");
    }

    private void playCurrentAudio(boolean isManual) {
        if (isTransitioning || isPaused)
            return;

        if (isManual) {
            audioPlayCount++;
            
            // Recommendation C: Play-Counter Badge / Tracker
            if (!hintUsed) {
                if (audioPlayCount == 1) {
                    Toast.makeText(this, "Pakinggan pa nang 2 beses para sa pahiwatig!", Toast.LENGTH_SHORT).show();
                } else if (audioPlayCount == 2) {
                    Toast.makeText(this, "Pakinggan pa nang 1 beses para sa pahiwatig!", Toast.LENGTH_SHORT).show();
                } else if (audioPlayCount == 3) {
                    Toast.makeText(this, "Naka-unlock na ang pahiwatig!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (audioPlayCount == 0) {
                audioPlayCount = 1;
            }
        }

        if (audioPlayCount >= 3 && !hintUsed) {
            if (btnHint.getVisibility() != android.view.View.VISIBLE) {
                btnHint.setVisibility(android.view.View.VISIBLE);
                btnHint.setColorFilter(Color.parseColor("#FFCA28"), android.graphics.PorterDuff.Mode.SRC_IN);
                startHintPulseAnimation();
            }
        }

        releaseMediaPlayer();

        Game2Question currentQuestion = questionList.get(currentIndex);

        int audioResId = getResources().getIdentifier(
                currentQuestion.getAudioFileName(),
                "raw",
                getPackageName());

        if (audioResId == 0) {
            Toast.makeText(this, "Audio file not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        mediaPlayer = MediaPlayer.create(this, audioResId);

        if (mediaPlayer != null) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            float voiceVolume = prefs.getInt(KEY_VOICE_VOLUME, 100) / 100f;
            mediaPlayer.setVolume(voiceVolume, voiceVolume);

            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer());
        }
    }

    private void startHintPulseAnimation() {
        if (btnHint == null || hintUsed) return;
        btnHint.setScaleX(1.0f);
        btnHint.setScaleY(1.0f);
        
        btnHint.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(600)
            .withEndAction(() -> {
                btnHint.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(600)
                    .withEndAction(this::startHintPulseAnimation)
                    .start();
            })
            .start();
    }

    private String[] getSyllables(String word) {
        String upper = word.toUpperCase().trim();
        switch (upper) {
            case "ALAALA": return new String[]{"A", "LA", "A", "LA"};
            case "AWTORISADO": return new String[]{"AW", "TO", "RI", "SA", "DO"};
            case "BARANGAY": return new String[]{"BA", "RAN", "GAY"};
            case "BUWENAS": return new String[]{"BU", "WE", "NAS"};
            case "DYIPNI": return new String[]{"DYIP", "NI"};
            case "ELEKSIYON": return new String[]{"E", "LEK", "SI", "YON"};
            case "ESTRUKTURA": return new String[]{"ES", "TRUK", "TU", "RA"};
            case "ESTUDYANTE": return new String[]{"ES", "TUD", "YAN", "TE"};
            case "FEMINISMO": return new String[]{"FE", "MI", "NIS", "MO"};
            case "GIRIAN": return new String[]{"GI", "RI", "AN"};
            case "GOBYERNO": return new String[]{"GO", "BYER", "NO"};
            case "ISNATSER": return new String[]{"IS", "NAT", "SER"};
            case "ISPELING": return new String[]{"IS", "PE", "LING"};
            case "KABESERA": return new String[]{"KA", "BE", "SE", "RA"};
            case "KAILAN": return new String[]{"KA", "I", "LAN"};
            case "KORTINA": return new String[]{"KOR", "TI", "NA"};
            case "KORYENTE": return new String[]{"KO", "RYEN", "TE"};
            case "KUMPIYANSA": return new String[]{"KUM", "PI", "YAN", "SA"};
            case "LATHALAIN": return new String[]{"LA", "THA", "LA", "IN"};
            case "LISENSIYA": return new String[]{"LI", "SEN", "SI", "YA"};
            case "MAYROON": return new String[]{"MAY", "RO", "ON"};
            case "NAKATUTULONG": return new String[]{"NA", "KA", "TU", "TU", "LONG"};
            case "OPERASYON": return new String[]{"O", "PE", "RAS", "YON"};
            case "ORASYON": return new String[]{"O", "RAS", "YON"};
            case "ORYENTASYON": return new String[]{"OR", "YEN", "TAS", "YON"};
            case "REKOMENDASYON": return new String[]{"RE", "KO", "MEN", "DAS", "YON"};
            case "RELIHIYON": return new String[]{"RE", "LI", "HI", "YON"};
            case "SIYENSIYA": return new String[]{"SI", "YEN", "SI", "YA"};
            case "TALUDTOD": return new String[]{"TA", "LUD", "TOD"};
            case "UGNAYAN": return new String[]{"UG", "NA", "YAN"};
            default:
                return new String[]{upper};
        }
    }

    private void checkAnswer() {
        if (isTransitioning || isPaused)
            return;

        String userAnswer = etAnswer.getText().toString().trim().toUpperCase();
        String correctAnswer = questionList.get(currentIndex).getCorrectAnswer().trim().toUpperCase();

        if (userAnswer.isEmpty()) {
            Toast.makeText(this, "Maglagay ng sagot.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userAnswer.equals(correctAnswer)) {
            isTransitioning = true;
            etAnswer.setBackgroundResource(R.drawable.bg_choice_correct);
            etAnswer.setEnabled(false);
            etAnswer.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.pulse));

            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().remove(KEY_GAME2_ANSWER).apply();

            new android.os.Handler().postDelayed(() -> {
                etAnswer.setText("");
                currentIndex++;
                saveProgress();
                showQuestion();
            }, 1000);
        } else {
            etAnswer.setBackgroundResource(R.drawable.bg_choice_incorrect);
            etAnswer.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.shake));
            Toast.makeText(this, "Mali! Subukan muli.", Toast.LENGTH_SHORT).show();
            new android.os.Handler().postDelayed(() -> {
                etAnswer.setBackgroundResource(R.drawable.bg_choice_blue);
            }, 800);
        }
    }

    private void goToResultScreen() {
        Intent intent = new Intent(AudioGameActivity.this, GameResultActivity.class);
        intent.putExtra("gameType", "game2");
        // currentIndex = number of words successfully answered
        intent.putExtra("wordsCompleted", currentIndex);
        startActivity(intent);
        finish();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
}