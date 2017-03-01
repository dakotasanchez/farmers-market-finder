package com.sanchez.fmf.model;

/**
 * Created by dakota on 9/16/15.
 */
public class MarketDetailResponseModel {

    private MarketDetailModel market_details;

    public MarketDetailResponseModel() {}

    public MarketDetailModel getMarketdetails() {
        return this.market_details;
    }

    public void setMarketdetails(MarketDetailModel market_details) {
        this.market_details = market_details;
    }
}
