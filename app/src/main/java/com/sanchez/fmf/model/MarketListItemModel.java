package com.sanchez.fmf.model;

/**
 * Created by dakota on 8/31/15.
 */
public class MarketListItemModel {

    private String id;
    private String marketname;

    public MarketListItemModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return marketname;
    }

    public void setName(String name) {
        this.marketname = name;
    }
}
