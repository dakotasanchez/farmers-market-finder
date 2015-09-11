package com.sanchez.fmf.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by dakota on 9/10/15.
 */
public class GlobalPreferencesImpl implements GlobalPreferences {

    private static final String GLOBAL_PREF = "global_pref";
    private static final String FAVORITE_MARKETS = "favorite_markets";

    private final Gson mGson;
    private final SharedPreferences mPref;

    public GlobalPreferencesImpl(Context context) {
        mPref = context.getSharedPreferences(GLOBAL_PREF, Context.MODE_PRIVATE);
        mGson = new Gson();
    }

    @Override
    public void setFavoriteMarkets(HashMap<String, String> favoriteMarkets) {
        String favoriteMarketsJson = mGson.toJson(favoriteMarkets, HashMap.class);
        mPref.edit().putString(FAVORITE_MARKETS, favoriteMarketsJson).apply();
    }

    @Override
    public HashMap<String, String> getFavoriteMarkets() {
        String favoriteMarketsJson = mPref.getString(FAVORITE_MARKETS, null);
        if(null != favoriteMarketsJson) {
            return mGson.fromJson(favoriteMarketsJson, HashMap.class);
        }
        return null;
    }
}
