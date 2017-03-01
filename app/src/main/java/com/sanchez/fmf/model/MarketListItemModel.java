package com.sanchez.fmf.model;

/**
 * Created by dakota on 8/31/15.
 */
public class MarketListItemModel {

    private String fmid;
    private String market_name;
    private String distance;
    private String uri;
    private double x;
    private double y;

    public MarketListItemModel() {
    }

    public String getId() {
        return fmid;
    }

    public void setId(String id) {
        this.fmid = fmid;
    }

    public String getName() {
        return market_name;
    }

    public void setName(String market_name) {
        this.market_name = market_name;
    }


    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
