package com.sanchez.fmf.event;

import com.sanchez.fmf.model.MarketDetailModel;

import java.util.List;

/**
 * Created by dakota on 9/16/15.
 */
public class MarketsDetailsRetrievedEvent {
    private List<MarketDetailModel> marketDetailModels;
    private double[] coordinates;

    public MarketsDetailsRetrievedEvent(List<MarketDetailModel> marketDetailModels, double[] coordinates) {
        this.marketDetailModels = marketDetailModels;
        this.coordinates = coordinates;
    }

    public List<MarketDetailModel> getMarketDetailModels() {
        return marketDetailModels;
    }

    public double[] getCoordinates() {
        return coordinates;
    }
}
