package com.sanchez.fmf.model;

/**
 * Created by dakota on 9/16/15.
 */
public class MarketDetailResponseModel {

    private MarketDetailModel market;

    public MarketDetailResponseModel() {}

    public MarketDetailModel getMarketdetails() {
        return this.market;
    }

    public void setMarketdetails(MarketDetailModel market) {
        this.market = market;
    }
}
