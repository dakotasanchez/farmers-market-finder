package com.sanchez.fmf.service;

import com.sanchez.fmf.model.MarketDetailResponseModel;
import com.sanchez.fmf.model.MarketListResponseModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by dakota on 9/2/15.
 */
public interface MarketService {

    @GET("/api/v1/markets")
    void getMarkets(@Query("x") double lng, @Query("y") double lat, @Query("dist") double distance, Callback<MarketListResponseModel> cb);

    @GET("/api/v1/market")
    void getMarket(@Query("id") String id, Callback<MarketDetailResponseModel> cb);
}
