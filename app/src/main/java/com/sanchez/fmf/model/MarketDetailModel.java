package com.sanchez.fmf.model;

/**
 * Created by dakota on 9/2/15.
 */
public class MarketDetailModel {

    private String Address;
    private String GoogleLink;
    private String Products;
    private String Schedule;

    public MarketDetailModel() {
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getMapLink() {
        return GoogleLink;
    }

    public void setMapLink(String mapLink) {
        GoogleLink = mapLink;
    }

    public String getProducts() {
        return Products;
    }

    public void setProducts(String products) {
        Products = products;
    }

    public String getSchedule() {
        return Schedule;
    }

    public void setSchedule(String schedule) {
        Schedule = schedule;
    }
}
