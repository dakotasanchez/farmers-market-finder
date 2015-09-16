package com.sanchez.fmf.event;

import com.sanchez.fmf.model.MarketListModel;

/**
 * Created by dakota on 9/15/15.
 */
public class GetMarketListSuccessEvent {
    private final MarketListModel marketList;

    public GetMarketListSuccessEvent(MarketListModel marketList) {
        this.marketList = marketList;
    }

    public MarketListModel getMarketList() {
        return marketList;
    }
}
