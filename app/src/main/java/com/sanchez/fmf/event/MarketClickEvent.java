package com.sanchez.fmf.event;

import android.view.View;

/**
 * Created by dakota on 9/12/15.
 */
public class MarketClickEvent {
    public final View market;

    public MarketClickEvent(View market) {
        this.market = market;
    }

    public View getMarket() {
        return market;
    }
}
