package com.sanchez.fmf.model;

/**
 * Created by dakota on 9/2/15.
 */
public class MarketDetailModel {

    private String mAddress;
    private String mMapLink;
    private String mProducts;
    private String mSchedule;

    public MarketDetailModel(String address, String mapLink, String products, String schedule) {
        mAddress = address;
        mMapLink = mapLink;
        mProducts = products;
        mSchedule = schedule;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getMapLink() {
        return mMapLink;
    }

    public String getProducts() {
        return mProducts;
    }

    public String getSchedule() {
        return mSchedule;
    }
}
