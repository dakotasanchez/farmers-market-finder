package com.sanchez.fmf.preference;

import java.util.LinkedHashMap;

/**
 * Created by dakota on 9/10/15.
 */
public interface GlobalPreferences {
    
    void setFavoriteMarkets(LinkedHashMap<String, String> favoriteMarkets);

    LinkedHashMap<String, String> getFavoriteMarkets();
}
