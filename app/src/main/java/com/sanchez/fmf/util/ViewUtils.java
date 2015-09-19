package com.sanchez.fmf.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.sanchez.fmf.R;

import java.util.Random;

/**
 * Created by dakota on 8/31/15.
 */
public class ViewUtils {

    // Non-instantiatable class
    private ViewUtils() {
    }

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

    public static void showKeyboard(Context context, View v) {
        InputMethodManager iMM = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        iMM.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }

    public static float dpToPx(Context c, float dp) {
        Resources r = c.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static float pxToDp(Context c, float px) {
        Resources r = c.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, r.getDisplayMetrics());
    }
}
