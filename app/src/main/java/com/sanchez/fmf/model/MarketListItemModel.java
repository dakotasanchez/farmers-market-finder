package com.sanchez.fmf.model;

/**
 * Created by dakota on 8/31/15.
 */
public class MarketListItemModel {

    private String mId;
    private String mName;

    public MarketListItemModel(String id, String name) {
        mId = id;
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }
}
