package com.sanchez.fmf.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

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
}
