package com.sanchez.fmf.event;

import com.sanchez.fmf.model.MarketDetailModel;
import com.sanchez.fmf.model.MarketListItemModel;

import java.util.HashMap;

/**
 * Created by dakota on 9/16/15.
 */
public class MarketsDetailsRetrievedEvent {
    private HashMap<MarketListItemModel, MarketDetailModel> marketDetailModels;

    public MarketsDetailsRetrievedEvent(HashMap<MarketListItemModel, MarketDetailModel> marketDetailModels) {
        this.marketDetailModels = marketDetailModels;
    }

    public HashMap<MarketListItemModel, MarketDetailModel> getMarketDetailModels() {
        return marketDetailModels;
    }
}
