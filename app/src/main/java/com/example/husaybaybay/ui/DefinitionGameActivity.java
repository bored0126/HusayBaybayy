package com.example.husaybaybay.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.husaybaybay.R;
import com.example.husaybaybay.data.model.DictionaryRepository;
import com.example.husaybaybay.data.model.DictionaryRepository.DictionaryWord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DefinitionGameActivity extends AppCompatActivity {

    private TextView tvCounter, tvDefinitionClue;
    private LinearLayout llAnswerSlotsTop, llAnswerSlotsBottom, llJumbledTop, llJumbledBottom;
    private ImageButton btnPause;
    private ImageButton btnHint;
    private ImageButton btnShuffle;

    // Hints Recommendations variables
    private View flHintContainer;
    private TextView tvHintBadge;
    private final Handler idleHandler = new Handler();
    private final Runnable idleRunnable = new Runnable() {
        @Override
        public void run() {
            triggerHintShiver();
            idleHandler.postDelayed(this, 12000); // Shiver every 12 seconds
        }
    };

    private List<DictionaryWord> questionList;
    private int currentIndex = 0;
    private String currentAnswer;

    private TextView[] answerSlotViews;
    private final Map<Integer, View> slottedButtonsMap = new HashMap<>();
    private boolean isTransitioning = false;
    private boolean isPaused = false;
    private int hintsRemainingForCurrentNumber = 0;

    private static final String PREFS = "HusayBaybayPrefs";
    private static final String KEY_INDEX = "game3_index";
    private static final String KEY_ORDER = "game3_order";
    private static final String KEY_INPUT = "game3_input";
    private static final String KEY_HINTS_REMAINING = "game3_hints_remaining";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition_game);

        btnPause = findViewById(R.id.btnPause);
        btnHint = findViewById(R.id.btnHint);
        btnShuffle = findViewById(R.id.btnShuffle);
        tvCounter = findViewById(R.id.tvCounter);
        tvDefinitionClue = findViewById(R.id.tvDefinitionClue);
        llAnswerSlotsTop = findViewById(R.id.llAnswerSlotsTop);
        llAnswerSlotsBottom = findViewById(R.id.llAnswerSlotsBottom);
        llJumbledTop = findViewById(R.id.llJumbledTop);
        llJumbledBottom = findViewById(R.id.llJumbledBottom);

        flHintContainer = findViewById(R.id.flHintContainer);
        tvHintBadge = findViewById(R.id.tvHintBadge);

        List<DictionaryWord> allWords = new ArrayList<>(DictionaryRepository.getDictionaryWords());
        loadSavedProgressOrShuffle(allWords);

        if (questionList.size() > 30) {
            questionList = questionList.subList(0, 30);
        }

        btnPause.setOnClickListener(v -> showPauseDialog());
        btnHint.setOnClickListener(v -> useHint());
        btnShuffle.setOnClickListener(v -> shuffleJumbledButtons());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });

        showQuestion();
    }

    private void loadSavedProgressOrShuffle(List<DictionaryWord> allWords) {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        int savedIndex = prefs.getInt(KEY_INDEX, -1);
        String savedOrder = prefs.getString(KEY_ORDER, null);

        if (savedIndex != -1 && savedOrder != null && !savedOrder.isEmpty()) {
            List<DictionaryWord> restored = rebuildOrder(savedOrder, allWords);
            if (!restored.isEmpty()) {
                questionList = restored;
                currentIndex = savedIndex;
                String currentWord = questionList.get(currentIndex).word.toUpperCase();
                int maxHints = Math.max(1, currentWord.length() / 2);
                hintsRemainingForCurrentNumber = prefs.getInt(KEY_HINTS_REMAINING, maxHints);
                updateHintButtonState();
                return;
            }
        }

        Collections.shuffle(allWords);
        questionList = new ArrayList<>(allWords.subList(0, Math.min(30, allWords.size())));
        currentIndex = 0;
        // hintsRemainingForCurrentNumber will be calculated in showQuestion()
    }
    
    private void updateHintButtonState() {
        if (hintsRemainingForCurrentNumber <= 0) {
            if (flHintContainer != null) {
                flHintContainer.setVisibility(View.GONE);
            }
        } else {
            if (flHintContainer != null) {
                flHintContainer.setVisibility(View.VISIBLE);
            }
            btnHint.setEnabled(true);
            btnHint.setAlpha(1.0f);
            btnHint.setColorFilter(Color.parseColor("#FFCA28"), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (tvHintBadge != null) {
            tvHintBadge.setText(String.valueOf(hintsRemainingForCurrentNumber));
        }
    }

    private List<DictionaryWord> rebuildOrder(String savedOrder, List<DictionaryWord> allWords) {
        List<DictionaryWord> result = new ArrayList<>();
        String[] words = savedOrder.split(",");

        for (String w : words) {
            for (DictionaryWord d : allWords) {
                if (d.word.equalsIgnoreCase(w)) {
                    result.add(d);
                    break;
                }
            }
        }
        return result;
    }

    private String buildOrderString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < questionList.size(); i++) {
            sb.append(questionList.get(i).word);
            if (i < questionList.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    private void saveProgress() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit()
                .putInt(KEY_INDEX, currentIndex)
                .putString(KEY_ORDER, buildOrderString())
                .putString(KEY_INPUT, getCurrentInput())
                .putInt(KEY_HINTS_REMAINING, hintsRemainingForCurrentNumber)
                .apply();
    }

    private void saveProgressForNextQuestion() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit()
                .putInt(KEY_INDEX, currentIndex)
                .putString(KEY_ORDER, buildOrderString())
                .remove(KEY_INPUT)
                .remove(KEY_HINTS_REMAINING)
                .apply();
    }

    private void clearProgress() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_INDEX)
                .remove(KEY_ORDER)
                .remove(KEY_INPUT)
                .remove(KEY_HINTS_REMAINING)
                .apply();
    }

    private void showPauseDialog() {
        if (isTransitioning || isPaused) return;
        isPaused = true;

        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_pause);
        dialog.setCancelable(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
            );
        }

        Button btnDialogQuit = dialog.findViewById(R.id.btnDialogQuit);
        Button btnDialogResume = dialog.findViewById(R.id.btnDialogResume);

        btnDialogQuit.setOnClickListener(v -> {
            dialog.dismiss();
            showExitDialog();
        });

        btnDialogResume.setOnClickListener(v -> {
            isPaused = false;
            dialog.dismiss();
        });

        dialog.show();
    }
    
    private void useHint() {
        if (isTransitioning || isPaused || hintsRemainingForCurrentNumber <= 0) return;

        resetIdleTimer();

        int targetIndex = -1;
        // Find the first empty slot from the left
        for (int i = 0; i < answerSlotViews.length; i++) {
            if (answerSlotViews[i].getText().toString().isEmpty() && answerSlotViews[i].isEnabled()) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex != -1) {
            String correctLetter = String.valueOf(currentAnswer.charAt(targetIndex));
            View matchedButton = findMatchingVisibleJumbledButton(correctLetter, 0);

            if (matchedButton != null) {
                hintsRemainingForCurrentNumber--;
                updateHintButtonState();
                android.widget.Toast.makeText(this, "Mga pahiwatig na natitira: " + hintsRemainingForCurrentNumber, android.widget.Toast.LENGTH_SHORT).show();

                TextView targetSlot = answerSlotViews[targetIndex];
                targetSlot.setText(correctLetter);
                targetSlot.setBackgroundResource(R.drawable.bg_isip_box_correct);
                targetSlot.setEnabled(false); // Locked by hint

                slottedButtonsMap.put(targetIndex, matchedButton);
                matchedButton.setVisibility(View.INVISIBLE);

                // Spring Pop Animation
                animateHintReveal(targetSlot);

                saveProgress();
                checkAnswerCompletion();
            }
        }
    }
    
    private void shuffleJumbledButtons() {
        if (isTransitioning || isPaused) return;
        
        resetIdleTimer();
        List<View> visibleButtons = new ArrayList<>();
        
        for (int i = 0; i < llJumbledTop.getChildCount(); i++) {
            View child = llJumbledTop.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) visibleButtons.add(child);
        }
        for (int i = 0; i < llJumbledBottom.getChildCount(); i++) {
            View child = llJumbledBottom.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) visibleButtons.add(child);
        }
        
        if (visibleButtons.isEmpty()) return;
        
        List<String> visibleLetters = new ArrayList<>();
        for (View v : visibleButtons) {
            visibleLetters.add(((TextView)v).getText().toString());
        }
        
        Collections.shuffle(visibleLetters);
        
        for (int i = 0; i < visibleButtons.size(); i++) {
            ((TextView)visibleButtons.get(i)).setText(visibleLetters.get(i));
        }
    }

    private void showExitDialog() {
        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_exit_game);
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
            );
        }

        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvDialogMessage = dialog.findViewById(R.id.tvDialogMessage);
        Button btnDialogNo = dialog.findViewById(R.id.btnDialogNo);
        Button btnDialogYes = dialog.findViewById(R.id.btnDialogYes);

        Runnable resumeAction = () -> {
            isPaused = false;
            resetIdleTimer();
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
        slottedButtonsMap.clear();

        llAnswerSlotsTop.removeAllViews();
        llAnswerSlotsBottom.removeAllViews();
        llJumbledTop.removeAllViews();
        llJumbledBottom.removeAllViews();

        DictionaryWord item = questionList.get(currentIndex);
        currentAnswer = item.word.toUpperCase();

        tvCounter.setText(String.valueOf(currentIndex + 1));
        tvDefinitionClue.setText(item.meaning);

        // Dynamically calculate hints count based on word length (Option A: half the word length, minimum of 1)
        // If there's saved progress for this current number, restore it, otherwise initialize a fresh pool.
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        int savedIndex = prefs.getInt(KEY_INDEX, -1);
        if (savedIndex == currentIndex && prefs.contains(KEY_HINTS_REMAINING)) {
            hintsRemainingForCurrentNumber = prefs.getInt(KEY_HINTS_REMAINING, Math.max(1, currentAnswer.length() / 2));
        } else {
            hintsRemainingForCurrentNumber = Math.max(1, currentAnswer.length() / 2);
        }
        updateHintButtonState();

        createAnswerSlots();
        createJumbledButtons();
        restoreSavedInput();
        resetIdleTimer();
    }

    private void createAnswerSlots() {
        int length = currentAnswer.length();
        answerSlotViews = new TextView[length];

        int marginPx = dpToPx(3);
        int slotSize = dpToPx(36);

        boolean useTwoRows = length > 8;
        int splitIndex = useTwoRows ? (int) Math.ceil(length / 2.0) : length;

        for (int i = 0; i < length; i++) {
            TextView tv = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(slotSize, slotSize);
            params.setMargins(marginPx, marginPx, marginPx, marginPx);
            tv.setLayoutParams(params);
            tv.setBackgroundResource(R.drawable.bg_isip_box_blue);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(16f);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            tv.setText("");

            final int index = i;
            tv.setOnClickListener(v -> onAnswerSlotClicked(index));

            answerSlotViews[i] = tv;

            if (i < splitIndex) {
                llAnswerSlotsTop.addView(tv);
            } else {
                llAnswerSlotsBottom.addView(tv);
            }
        }

        if (!useTwoRows) {
            llAnswerSlotsBottom.setVisibility(View.GONE);
        } else {
            llAnswerSlotsBottom.setVisibility(View.VISIBLE);
        }
    }

    private void createJumbledButtons() {
        int targetButtons = Math.max(12, currentAnswer.length() + (currentAnswer.length() % 2 == 0 ? 4 : 5));
        if (targetButtons % 2 != 0) targetButtons++;

        List<Character> jumbledChars = new ArrayList<>();
        for (char c : currentAnswer.toCharArray()) {
            jumbledChars.add(c);
        }

        Random random = new Random();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        while (jumbledChars.size() < targetButtons) {
            jumbledChars.add(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        Collections.shuffle(jumbledChars);

        int half = targetButtons / 2;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int marginPx = dpToPx(4);
        int availableWidth = screenWidth - dpToPx(32);
        int btnSize = Math.min(dpToPx(44), (availableWidth / Math.max(1, half)) - (marginPx * 2));
        if (btnSize < dpToPx(30)) btnSize = dpToPx(30);

        for (int i = 0; i < targetButtons; i++) {
            TextView btn = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(btnSize, btnSize);
            params.setMargins(marginPx, marginPx, marginPx, marginPx);
            btn.setLayoutParams(params);
            btn.setBackgroundResource(R.drawable.bg_isip_box_green);
            btn.setGravity(Gravity.CENTER);
            btn.setTextColor(Color.BLACK);
            btn.setTextSize(18f);
            btn.setTypeface(null, android.graphics.Typeface.BOLD);
            btn.setText(String.valueOf(jumbledChars.get(i)));

            final String letter = String.valueOf(jumbledChars.get(i));
            btn.setOnClickListener(v -> onJumbledButtonClicked(v, letter));

            if (i < half) {
                llJumbledTop.addView(btn);
            } else {
                llJumbledBottom.addView(btn);
            }
        }
    }

    private void onJumbledButtonClicked(View btn, String letter) {
        if (isTransitioning || isPaused) return;

        resetIdleTimer();
        int targetIndex = -1;
        // Find the first empty slot
        for (int i = 0; i < answerSlotViews.length; i++) {
            if (answerSlotViews[i].getText().toString().isEmpty() && answerSlotViews[i].isEnabled()) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex != -1) {
            TextView targetSlot = answerSlotViews[targetIndex];
            targetSlot.setText(((TextView)btn).getText().toString());
            
            // Pop Animation
            animateHintReveal(targetSlot);

            slottedButtonsMap.put(targetIndex, btn);
            btn.setVisibility(View.INVISIBLE);
            saveProgress();
            checkAnswerCompletion();
        }
    }

    private void onAnswerSlotClicked(int index) {
        if (isTransitioning || isPaused) return;

        resetIdleTimer();
        TextView slot = answerSlotViews[index];
        if (!slot.isEnabled()) return; // Lock if hinted

        if (!slot.getText().toString().isEmpty()) {
            slot.setText("");

            View original = slottedButtonsMap.get(index);
            if (original != null) {
                original.setVisibility(View.VISIBLE);
            }

            slottedButtonsMap.remove(index);
            saveProgress();
        }
    }

    private void animateHintReveal(View view) {
        view.setScaleX(0.7f);
        view.setScaleY(0.7f);
        view.animate()
            .scaleX(1.15f)
            .scaleY(1.15f)
            .setDuration(150)
            .withEndAction(() -> {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start();
            })
            .start();
    }

    private void resetIdleTimer() {
        idleHandler.removeCallbacks(idleRunnable);
        idleHandler.postDelayed(idleRunnable, 12000);
    }

    private void triggerHintShiver() {
        if (flHintContainer != null && flHintContainer.getVisibility() == View.VISIBLE) {
            flHintContainer.animate()
                .translationXBy(8f)
                .setDuration(50)
                .withEndAction(() -> {
                    flHintContainer.animate()
                        .translationXBy(-16f)
                        .setDuration(50)
                        .withEndAction(() -> {
                            flHintContainer.animate()
                                    .translationXBy(16f)
                                    .setDuration(50)
                                    .withEndAction(() -> {
                                        flHintContainer.animate()
                                                .translationXBy(-8f)
                                                .setDuration(50)
                                                .start();
                                    })
                                    .start();
                        })
                        .start();
                })
                .start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetIdleTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        idleHandler.removeCallbacks(idleRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        idleHandler.removeCallbacks(idleRunnable);
    }

    private void checkAnswerCompletion() {
        StringBuilder sb = new StringBuilder();
        for (TextView tv : answerSlotViews) {
            if (tv.getText().toString().isEmpty()) return;
            sb.append(tv.getText().toString());
        }

        if (sb.toString().equals(currentAnswer)) {
            isTransitioning = true;

            for (TextView tv : answerSlotViews) {
                tv.setBackgroundResource(R.drawable.bg_isip_box_correct);
                tv.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.pulse));
            }

            new Handler().postDelayed(() -> {
                currentIndex++;
                saveProgressForNextQuestion();
                showQuestion();
            }, 1000);
        } else {
            // Shake the slots that are filled but incorrect (only those that are not hint-locked)
            for (TextView tv : answerSlotViews) {
                if (tv.isEnabled()) {
                    tv.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.shake));
                }
            }
        }
    }

    private String getCurrentInput() {
        StringBuilder sb = new StringBuilder();
        for (TextView tv : answerSlotViews) {
            sb.append(tv.getText().toString()).append("|");
        }
        return sb.toString();
    }

    private void restoreSavedInput() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String saved = prefs.getString(KEY_INPUT, "");
        if (saved.isEmpty()) return;

        String[] letters = saved.split("\\|");
        Map<String, Integer> usedCount = new LinkedHashMap<>();

        for (int i = 0; i < letters.length && i < answerSlotViews.length; i++) {
            if (!letters[i].isEmpty()) {
                answerSlotViews[i].setText(letters[i]);

                int count = usedCount.containsKey(letters[i]) ? usedCount.get(letters[i]) : 0;
                View matchedButton = findMatchingVisibleJumbledButton(letters[i], count);
                if (matchedButton != null) {
                    matchedButton.setVisibility(View.INVISIBLE);
                    slottedButtonsMap.put(i, matchedButton);
                }
                usedCount.put(letters[i], count + 1);
            }
        }
    }

    private View findMatchingVisibleJumbledButton(String letter, int occurrenceIndex) {
        int count = 0;

        for (int i = 0; i < llJumbledTop.getChildCount(); i++) {
            View child = llJumbledTop.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView) child;
                if (tv.getVisibility() == View.VISIBLE && tv.getText().toString().equals(letter)) {
                    if (count == occurrenceIndex) return tv;
                    count++;
                }
            }
        }

        for (int i = 0; i < llJumbledBottom.getChildCount(); i++) {
            View child = llJumbledBottom.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView) child;
                if (tv.getVisibility() == View.VISIBLE && tv.getText().toString().equals(letter)) {
                    if (count == occurrenceIndex) return tv;
                    count++;
                }
            }
        }

        return null;
    }

    private void goToResultScreen() {
        Intent intent = new Intent(this, GameResultActivity.class);
        intent.putExtra("gameType", "game3");
        // currentIndex = number of words successfully answered
        intent.putExtra("wordsCompleted", currentIndex);
        startActivity(intent);
        finish();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}