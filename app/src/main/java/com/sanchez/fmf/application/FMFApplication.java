package com.sanchez.fmf.application;

import android.app.Application;

import com.sanchez.fmf.preference.GlobalPreferencesImpl;

/**
 * Created by dakota on 9/10/15.
 */
public class FMFApplication extends Application {

    private static GlobalPreferencesImpl mGlobalPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mGlobalPreferences = new GlobalPreferencesImpl(this);
    }

    public static GlobalPreferencesImpl getGlobalPreferences() {
        return mGlobalPreferences;
    }
}