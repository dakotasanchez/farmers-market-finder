package com.sanchez.fmf.event;

import com.sanchez.fmf.model.MarketDetailModel;

import java.util.List;

/**
 * Created by dakota on 9/16/15.
 */
public class MarketsDetailsRetrievedEvent {
    private List<MarketDetailModel> marketDetailModels;

    public MarketsDetailsRetrievedEvent(List<MarketDetailModel> marketDetailModels) {
        this.marketDetailModels = marketDetailModels;
    }

    public List<MarketDetailModel> getMarketDetailModels() {
        return marketDetailModels;
    }
}
