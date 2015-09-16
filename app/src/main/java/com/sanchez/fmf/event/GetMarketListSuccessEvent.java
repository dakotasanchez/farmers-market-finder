package com.sanchez.fmf.event;

import com.sanchez.fmf.model.MarketListResponseModel;

/**
 * Created by dakota on 9/15/15.
 */
public class GetMarketListSuccessEvent {
    private final MarketListResponseModel marketList;

    public GetMarketListSuccessEvent(MarketListResponseModel marketList) {
        this.marketList = marketList;
    }

    public MarketListResponseModel getMarketList() {
        return marketList;
    }
}
