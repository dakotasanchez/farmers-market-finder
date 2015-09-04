package com.sanchez.fmf.model;

/**
 * Created by dakota on 9/2/15.
 */
public class MarketDetailModel {

    private String mAddress;
    private String mMapLink;
    private String mProducts;
    private String mSchedule;

    public MarketDetailModel() {
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getMapLink() {
        return mMapLink;
    }

    public void setMapLink(String mapLink) {
        mMapLink = mapLink;
    }

    public String getProducts() {
        return mProducts;
    }

    public void setProducts(String products) {
        mProducts = products;
    }

    public String getSchedule() {
        return mSchedule;
    }

    public void setSchedule(String schedule) {
        mSchedule = schedule;
    }
}
