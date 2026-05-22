package com.example.husaybaybay.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.husaybaybay.HusayBaybayApplication;
import com.example.husaybaybay.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private SeekBar seekVoiceVolume, seekMusicVolume;
    private Switch switchTapSound;
    private Button btnResetProgress;

    public static final String PREFS_NAME = "HusayBaybayPrefs";
    public static final String KEY_VOICE_VOLUME = "voice_volume";
    public static final String KEY_MUSIC_VOLUME = "music_volume";
    public static final String KEY_TAP_SOUND = "tap_sound";

    public static final String KEY_GAME1_INDEX = "game1_current_index";
    public static final String KEY_GAME1_SCORE = "game1_score";
    public static final String KEY_GAME1_ORDER = "game1_question_order";

    public static final String KEY_GAME2_INDEX = "game2_current_index";
    public static final String KEY_GAME2_ORDER = "game2_question_order";
    public static final String KEY_GAME2_ANSWER = "game2_current_answer";

    public static final String KEY_GAME3_INDEX = "game3_index";
    public static final String KEY_GAME3_ORDER = "game3_order";
    public static final String KEY_GAME3_INPUT = "game3_input";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnBack = findViewById(R.id.btnBack);
        seekVoiceVolume = findViewById(R.id.seekVoiceVolume);
        seekMusicVolume = findViewById(R.id.seekMusicVolume);
        switchTapSound = findViewById(R.id.switchTapSound);
        btnResetProgress = findViewById(R.id.btnResetProgress);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        int savedVoiceVolume = prefs.getInt(KEY_VOICE_VOLUME, 100);
        int savedMusicVolume = prefs.getInt(KEY_MUSIC_VOLUME, 30);
        boolean savedTapSound = prefs.getBoolean(KEY_TAP_SOUND, true);

        seekVoiceVolume.setProgress(savedVoiceVolume);
        seekMusicVolume.setProgress(savedMusicVolume);
        switchTapSound.setChecked(savedTapSound);

        btnBack.setOnClickListener(v -> finish());

        seekVoiceVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt(KEY_VOICE_VOLUME, progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekMusicVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt(KEY_MUSIC_VOLUME, progress).apply();

                if (getApplication() instanceof HusayBaybayApplication) {
                    HusayBaybayApplication app = (HusayBaybayApplication) getApplication();
                    app.updateMusicVolume(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        switchTapSound.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_TAP_SOUND, isChecked).apply()
        );

        btnResetProgress.setOnClickListener(v -> showResetDialog());
    }

    private void showResetDialog() {
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

        android.widget.TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        android.widget.TextView tvDialogMessage = dialog.findViewById(R.id.tvDialogMessage);
        android.widget.Button btnDialogNo = dialog.findViewById(R.id.btnDialogNo);
        android.widget.Button btnDialogYes = dialog.findViewById(R.id.btnDialogYes);

        tvDialogTitle.setText("Ire-reset mo ba?");
        tvDialogMessage.setText("Mabubura ang saved progress ng laro, pero hindi maaapektuhan ang volume settings mo.");

        btnDialogNo.setText("Hindi");
        btnDialogYes.setText("Oo");

        btnDialogNo.setOnClickListener(v -> dialog.dismiss());

        btnDialogYes.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit()
                    .remove(KEY_GAME1_INDEX)
                    .remove(KEY_GAME1_SCORE)
                    .remove(KEY_GAME1_ORDER)
                    .remove(KEY_GAME2_INDEX)
                    .remove(KEY_GAME2_ORDER)
                    .remove(KEY_GAME2_ANSWER)
                    .remove(KEY_GAME3_INDEX)
                    .remove(KEY_GAME3_ORDER)
                    .remove(KEY_GAME3_INPUT)
                    .apply();

            Toast.makeText(SettingsActivity.this, "Na-reset na ang progress.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
}