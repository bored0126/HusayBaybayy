package com.example.husaybaybay.util;

import android.view.MotionEvent;
import android.view.View;

public class AnimationHelper {
    /**
     * Applies a scale-down animation on touch press and scales it back to normal on release.
     * Returns false in the OnTouchListener so that onClickListener can still be triggered.
     */
    public static void applyScaleAnimation(View view) {
        if (view == null) return;
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
                    break;
            }
            return false; // Propagate the touch event to click listeners
        });
    }
}
