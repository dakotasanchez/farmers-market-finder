package com.sanchez.fmf.model;

import java.util.List;

/**
 * Created by dakota on 9/4/15.
 */
public class MarketListModel {

    private List<MarketListItemModel> results;

    public MarketListModel() {}

    public List<MarketListItemModel> getMarkets() {
        return results;
    }

    public void setMarkets(List<MarketListItemModel> markets) {
        results = markets;
    }
}
