package com.sanchez.fmf.preference;

import java.util.HashMap;

/**
 * Created by dakota on 9/10/15.
 */
public interface GlobalPreferences {
    
    void setFavoriteMarkets(HashMap<String, String> favoriteMarkets);

    HashMap<String, String> getFavoriteMarkets();
}
