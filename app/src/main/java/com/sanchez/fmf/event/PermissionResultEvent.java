package com.sanchez.fmf.event;

/**
 * Created by dakota on 10/5/15.
 */
public class PermissionResultEvent {
    private boolean mGranted;
    public PermissionResultEvent(boolean granted) {
        mGranted = granted;
    }

    public boolean getGranted() {
        return mGranted;
    }
}
