package com.sanchez.fmf.service;

import com.sanchez.fmf.model.MarketDetailModel;
import com.sanchez.fmf.model.MarketListItemModel;
import com.sanchez.fmf.model.MarketListModel;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by dakota on 9/2/15.
 */
public interface MarketService {

    @GET("/v1/data.svc/locSearch")
    void getMarkets(@Query("lat") double lat, @Query("lng") double lng, Callback<MarketListModel> cb);

    @GET("/v1/data.svc/zipSearch")
    void getMarkets(@Query("zip") int zip, Callback<MarketListItemModel[]> cb);

    @GET("/v1/data.svc/mktDetail")
    void getMarket(@Query("id") int id, Callback<MarketDetailModel> cb);
}
