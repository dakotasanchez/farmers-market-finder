package com.sanchez.fmf.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by dakota on 8/31/15.
 */
public class ViewUtils {

    public static void crossfadeTwoViews(final View inView, final View outView, int duration) {
        inView.setAlpha(0f);
        inView.setVisibility(View.VISIBLE);

        inView.animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null);

        outView.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        outView.setVisibility(View.GONE);
                    }
                });
    }

    public static void hideKeyboard(Activity activity) {
        View v = activity.getCurrentFocus();
        if(v != null) {
            InputMethodManager iMM = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            iMM.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
