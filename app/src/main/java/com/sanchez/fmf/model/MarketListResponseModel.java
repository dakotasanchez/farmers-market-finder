package com.sanchez.fmf.model;

import java.util.List;

/**
 * Created by dakota on 9/4/15.
 */
public class MarketListResponseModel {

    private List<MarketListItemModel> markets;

    public MarketListResponseModel() {}

    public List<MarketListItemModel> getMarkets() {
        return this.markets;
    }

    public void setMarkets(List<MarketListItemModel> markets) {
        this.markets = markets;
    }
}
