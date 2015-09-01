package com.sanchez.fmf.model;

/**
 * Created by dakota on 8/31/15.
 */
public class MarketListItemModel {

    private String mId;
    private String mName;
    private double mDistanceFromQuery;

    public MarketListItemModel(String id, String name, double distanceFromQuery) {
        mId = id;
        mName = name;
        mDistanceFromQuery = distanceFromQuery;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public double getDistanceFromQuery() {
        return mDistanceFromQuery;
    }
}
