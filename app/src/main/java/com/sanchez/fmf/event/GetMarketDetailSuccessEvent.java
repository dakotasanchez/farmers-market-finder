package com.sanchez.fmf.event;

import com.sanchez.fmf.model.MarketDetailResponseModel;

/**
 * Created by dsanchez on 3/9/17.
 */

public class GetMarketDetailSuccessEvent {
    private final MarketDetailResponseModel marketDetail;

    public GetMarketDetailSuccessEvent(MarketDetailResponseModel marketDetail) {
        this.marketDetail = marketDetail;
    }

    public MarketDetailResponseModel getMarketDetail() {
        return marketDetail;
    }
}
