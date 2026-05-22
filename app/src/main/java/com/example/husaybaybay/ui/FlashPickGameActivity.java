package com.example.husaybaybay.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;

import com.example.husaybaybay.R;
import com.example.husaybaybay.data.model.Game1Question;
import com.example.husaybaybay.data.model.Game1Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlashPickGameActivity extends AppCompatActivity {

    private TextView tvQuestionCounter, tvTimer;
    private android.widget.Button btnChoice1, btnChoice2, btnChoice3;
    private ImageButton btnNext;
    private ImageButton btnPause;
    private ImageButton btnHint;

    private List<Game1Question> questionList;
    private int currentIndex = 0;
    private int score = 0;
    private boolean answered = false;

    private CountDownTimer countDownTimer;
    private final long TIME_PER_QUESTION = 30000; // 30 seconds
    private long timeLeftInMillis = TIME_PER_QUESTION;
    private boolean isPaused = false;
    private boolean hintUsed = false;
    private int hintEliminatedChoiceIndex = -1; // -1 if not used

    private static final String PREFS_NAME = "HusayBaybayPrefs";
    private static final String KEY_GAME1_INDEX = "game1_current_index";
    private static final String KEY_GAME1_SCORE = "game1_score";
    private static final String KEY_GAME1_ORDER = "game1_question_order";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_pick_game);

        tvQuestionCounter = findViewById(R.id.tvQuestionCounter);
        tvTimer = findViewById(R.id.tvTimer);

        btnChoice1 = findViewById(R.id.btnChoice1);
        btnChoice2 = findViewById(R.id.btnChoice2);
        btnChoice3 = findViewById(R.id.btnChoice3);
        btnNext = findViewById(R.id.btnNext);
        btnPause = findViewById(R.id.btnPause);
        btnHint = findViewById(R.id.btnHint);

        questionList = Game1Repository.loadQuestions(this);
        loadSavedProgressOrShuffle();

        if (questionList.size() > 30) {
            questionList = questionList.subList(0, 30);
        }

        showQuestion();

        btnPause.setOnClickListener(v -> showPauseDialog());
        btnHint.setOnClickListener(v -> useHint());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });

        btnChoice1.setOnClickListener(v -> checkAnswer(btnChoice1));
        btnChoice2.setOnClickListener(v -> checkAnswer(btnChoice2));
        btnChoice3.setOnClickListener(v -> checkAnswer(btnChoice3));

        btnNext.setOnClickListener(v -> {
            currentIndex++;
            hintUsed = false;
            hintEliminatedChoiceIndex = -1;
            saveProgress();

            if (currentIndex < questionList.size()) {
                showQuestion();
            } else {
                clearProgress();
                goToResultScreen();
            }
        });
    }

    private void showQuestion() {
        if (currentIndex >= questionList.size()) {
            clearProgress();
            goToResultScreen();
            return;
        }

        answered = false;
        isPaused = false;
        btnNext.setVisibility(android.view.View.GONE);
        btnHint.setVisibility(android.view.View.GONE);
        btnHint.animate().cancel();
        btnHint.setScaleX(1.0f);
        btnHint.setScaleY(1.0f);

        resetButtonColors();
        enableChoices(true);

        Game1Question currentQuestion = questionList.get(currentIndex);

        tvQuestionCounter.setText(String.valueOf(currentIndex + 1));

        btnChoice1.setText(currentQuestion.getChoices().get(0));
        btnChoice2.setText(currentQuestion.getChoices().get(1));
        btnChoice3.setText(currentQuestion.getChoices().get(2));

        // Restore hint if loaded from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedIndex = prefs.getInt(KEY_GAME1_INDEX, -1);
        if (savedIndex == currentIndex) {
            hintUsed = prefs.getBoolean("game1_hint_used", false);
            hintEliminatedChoiceIndex = prefs.getInt("game1_hint_eliminated_choice_index", -1);
        } else {
            hintUsed = false;
            hintEliminatedChoiceIndex = -1;
        }

        if (hintUsed && hintEliminatedChoiceIndex != -1) {
            Button[] choices = {btnChoice1, btnChoice2, btnChoice3};
            Button toHide = choices[hintEliminatedChoiceIndex];
            toHide.setEnabled(false);
            toHide.setVisibility(android.view.View.INVISIBLE);
        }

        timeLeftInMillis = TIME_PER_QUESTION;
        startTimer();
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                tvTimer.setText(String.valueOf(millisUntilFinished / 1000));
                
                if (millisUntilFinished <= 15000 && !hintUsed && !answered) {
                    if (btnHint.getVisibility() != android.view.View.VISIBLE) {
                        btnHint.setVisibility(android.view.View.VISIBLE);
                        btnHint.setColorFilter(android.graphics.Color.parseColor("#FFCA28"), android.graphics.PorterDuff.Mode.SRC_IN);
                        startHintPulseAnimation();
                    }
                }
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                tvTimer.setText("0");
                if (!answered) {
                    revealCorrectAnswer();
                    answered = true;
                    enableChoices(false);
                    btnHint.animate().cancel();
                    btnHint.setScaleX(1.0f);
                    btnHint.setScaleY(1.0f);
                    btnHint.setVisibility(android.view.View.GONE);
                    btnNext.setVisibility(android.view.View.VISIBLE);
                }
            }
        }.start();
    }
    
    private void showPauseDialog() {
        if (answered || isPaused) return;
        isPaused = true;
        if (countDownTimer != null) countDownTimer.cancel();

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
            startTimer();
            dialog.dismiss();
        });

        dialog.show();
    }
    
    private void useHint() {
        if (hintUsed || answered || isPaused) return;
        hintUsed = true;
        
        btnHint.animate().cancel();
        btnHint.setScaleX(1.0f);
        btnHint.setScaleY(1.0f);
        btnHint.setVisibility(android.view.View.GONE);
        
        String correctAnswer = questionList.get(currentIndex).getCorrectAnswer();
        Button[] choices = {btnChoice1, btnChoice2, btnChoice3};
        
        java.util.List<Integer> wrongIndices = new java.util.ArrayList<>();
        for (int i = 0; i < choices.length; i++) {
            if (!choices[i].getText().toString().equals(correctAnswer)) {
                wrongIndices.add(i);
            }
        }
        
        if (!wrongIndices.isEmpty()) {
            java.util.Collections.shuffle(wrongIndices);
            hintEliminatedChoiceIndex = wrongIndices.get(0);
            Button toHide = choices[hintEliminatedChoiceIndex];
            
            animateWrongChoiceElimination(toHide);
            saveProgress();
        }
    }

    private void startHintPulseAnimation() {
        if (btnHint == null || hintUsed || answered) return;
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

    private void animateWrongChoiceElimination(View button) {
        button.setEnabled(false);
        button.animate()
            .rotation(-15f)
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setDuration(500)
            .withEndAction(() -> {
                button.setVisibility(android.view.View.INVISIBLE);
            })
            .start();
    }

    private void checkAnswer(Button selectedButton) {
        if (answered)
            return;

        answered = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        String selectedAnswer = selectedButton.getText().toString();
        String correctAnswer = questionList.get(currentIndex).getCorrectAnswer();

        if (selectedAnswer.equals(correctAnswer)) {
            score++;
            selectedButton.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.pulse));
        } else {
            selectedButton.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.shake));
        }

        saveProgress();

        revealCorrectAnswer();
        enableChoices(false);
        btnHint.animate().cancel();
        btnHint.setScaleX(1.0f);
        btnHint.setScaleY(1.0f);
        btnHint.setVisibility(android.view.View.GONE);
        btnNext.setVisibility(android.view.View.VISIBLE);
    }

    private void revealCorrectAnswer() {
        String correctAnswer = questionList.get(currentIndex).getCorrectAnswer();

        colorButton(btnChoice1, btnChoice1.getText().toString().equals(correctAnswer));
        colorButton(btnChoice2, btnChoice2.getText().toString().equals(correctAnswer));
        colorButton(btnChoice3, btnChoice3.getText().toString().equals(correctAnswer));
    }

    private void colorButton(Button button, boolean isCorrect) {
        if (isCorrect) {
            button.setBackgroundResource(R.drawable.bg_choice_correct);
            button.setTextColor(0xFFFFFFFF);
        } else {
            button.setBackgroundResource(R.drawable.bg_choice_incorrect);
            button.setTextColor(0xFFFFFFFF);
        }
    }

    private void resetButtonColors() {
        for (Button btn : new Button[]{btnChoice1, btnChoice2, btnChoice3}) {
            btn.setVisibility(android.view.View.VISIBLE);
            btn.setEnabled(true);
            btn.setRotation(0f);
            btn.setScaleX(1.0f);
            btn.setScaleY(1.0f);
            btn.setAlpha(1.0f);
        }
        btnChoice1.setBackgroundResource(R.drawable.bg_choice_blue);
        btnChoice2.setBackgroundResource(R.drawable.bg_choice_blue);
        btnChoice3.setBackgroundResource(R.drawable.bg_choice_blue);
        btnChoice1.setTextColor(android.graphics.Color.BLACK);
        btnChoice2.setTextColor(android.graphics.Color.BLACK);
        btnChoice3.setTextColor(android.graphics.Color.BLACK);
    }

    private void enableChoices(boolean enable) {
        btnChoice1.setEnabled(enable);
        btnChoice2.setEnabled(enable);
        btnChoice3.setEnabled(enable);
    }

    private void goToResultScreen() {
        Intent intent = new Intent(FlashPickGameActivity.this, GameResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("totalItems", questionList.size());
        startActivity(intent);
        finish();
    }

    private void showExitDialog() {
        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_exit_game);
        dialog.setCancelable(true);
        
        boolean wasPaused = isPaused;
        if (!wasPaused && !answered) {
            isPaused = true;
            if (countDownTimer != null) countDownTimer.cancel();
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
            if (!answered) {
                isPaused = false;
                startTimer();
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

    private void saveProgress() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(KEY_GAME1_INDEX, currentIndex);
        editor.putInt(KEY_GAME1_SCORE, score);
        editor.putString(KEY_GAME1_ORDER, buildQuestionOrderString());
        editor.putBoolean("game1_hint_used", hintUsed);
        editor.putInt("game1_hint_eliminated_choice_index", hintEliminatedChoiceIndex);

        editor.apply();
    }

    private void clearProgress() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_GAME1_INDEX)
                .remove(KEY_GAME1_SCORE)
                .remove(KEY_GAME1_ORDER)
                .remove("game1_hint_used")
                .remove("game1_hint_eliminated_choice_index")
                .apply();
    }

    private String buildQuestionOrderString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < questionList.size(); i++) {
            builder.append(questionList.get(i).getId());
            if (i < questionList.size() - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    private void loadSavedProgressOrShuffle() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedIndex = prefs.getInt(KEY_GAME1_INDEX, -1);
        int savedScore = prefs.getInt(KEY_GAME1_SCORE, 0);
        String savedOrder = prefs.getString(KEY_GAME1_ORDER, null);

        if (savedIndex != -1 && savedOrder != null && !savedOrder.isEmpty()) {
            List<Game1Question> reorderedList = rebuildQuestionOrder(savedOrder);
            if (!reorderedList.isEmpty()) {
                questionList = reorderedList;
                currentIndex = savedIndex;
                score = savedScore;
                hintUsed = prefs.getBoolean("game1_hint_used", false);
                hintEliminatedChoiceIndex = prefs.getInt("game1_hint_eliminated_choice_index", -1);
            } else {
                Collections.shuffle(questionList);
            }
        } else {
            Collections.shuffle(questionList);
        }
    }

    private List<Game1Question> rebuildQuestionOrder(String savedOrder) {
        List<Game1Question> reordered = new ArrayList<>();
        String[] ids = savedOrder.split(",");

        List<Game1Question> originalList = Game1Repository.loadQuestions(this);

        for (String idStr : ids) {
            int id = Integer.parseInt(idStr);
            for (Game1Question question : originalList) {
                if (question.getId() == id) {
                    reordered.add(question);
                    break;
                }
            }
        }

        return reordered;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}