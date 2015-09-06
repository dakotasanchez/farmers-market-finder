package com.sanchez.fmf.util;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by dakota on 9/6/15.
 */
public class RippleForegroundListener implements View.OnTouchListener {
    private int rippleViewId = -1;
    private Context context;

    /**
     * @param context to open the keyboard
     * @param rippleViewId the id of the view which has the ripple effect
     */
    public RippleForegroundListener(Context context, int rippleViewId) {
        this.rippleViewId = rippleViewId;
        this.context = context;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Convert to view coordinates. Assumes the host view is
        // a direct child and the view is not scrollable.
        float x = event.getX() + v.getLeft();
        float y = event.getY() + v.getTop();

        final View rippleView = findRippleView(v);
        //if we were not able to find the view to display the ripple on, continue.
        if (rippleView == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Simulate motion on the view.
            rippleView.drawableHotspotChanged(x, y);
        }

        // Simulate pressed state on the view.
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                rippleView.setPressed(true);
                break;
            case MotionEvent.ACTION_UP:
                rippleView.setPressed(false);
                ViewUtils.showKeyboard(context, v);
                break;
            case MotionEvent.ACTION_CANCEL:
                rippleView.setPressed(false);
                break;
        }

        // Pass all events through to the host view.
        return false;
    }

    public View findRippleView(View view) {
        if (view.getId() == rippleViewId) {
            return view;
        } else {
            if (view.getParent().getParent() instanceof View) {
                return findRippleView((View) view.getParent().getParent());
            } else {
                return null;
            }
        }
    }
}