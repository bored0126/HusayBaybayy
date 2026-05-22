package com.example.husaybaybay;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HusayBaybayApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private MediaPlayer mediaPlayer;
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    public static final String PREFS_NAME = "HusayBaybayPrefs";
    public static final String KEY_MUSIC_VOLUME = "music_volume";

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        activity.setVolumeControlStream(android.media.AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.bgm_harvest_moon);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                    applyMusicVolume();
                    mediaPlayer.start();
                }
            } else {
                applyMusicVolume();
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        applyMusicVolume();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    public void updateMusicVolume(int progress) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(KEY_MUSIC_VOLUME, progress).apply();

        if (mediaPlayer != null) {
            float musicVolume = progress / 100f;
            mediaPlayer.setVolume(musicVolume, musicVolume);
        }
    }

    private void applyMusicVolume() {
        if (mediaPlayer != null) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int savedVolume = prefs.getInt(KEY_MUSIC_VOLUME, 30);
            float musicVolume = savedVolume / 100f;
            mediaPlayer.setVolume(musicVolume, musicVolume);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}