package com.sanchez.fmf.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.sanchez.fmf.R;

import java.util.Random;

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

    public static void showKeyboard(Context context, View v) {
        InputMethodManager iMM = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        iMM.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }

    public static int colorGenerator(Activity activity) {
        Random r = new Random();
        Resources res = activity.getResources();

        int randInt = r.nextInt(5);

        switch (randInt) {
            case 0:
                return res.getColor(R.color.market_blue);
            case 1:
                return res.getColor(R.color.market_red);
            case 2:
                return res.getColor(R.color.market_green);
            case 3:
                return res.getColor(R.color.market_orange);
            case 4:
                return res.getColor(R.color.market_pink);
            default:
                return res.getColor(R.color.market_red);
        }
    }
}
