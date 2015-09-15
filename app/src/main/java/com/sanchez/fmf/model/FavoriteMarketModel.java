package com.sanchez.fmf.model;

/**
 * Created by dakota on 9/14/15.
 */
public class FavoriteMarketModel {

    private String id;
    private String name;

    public FavoriteMarketModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
