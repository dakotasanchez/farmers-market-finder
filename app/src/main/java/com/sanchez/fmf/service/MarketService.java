package com.sanchez.fmf.service;

import com.sanchez.fmf.model.MarketDetailModel;
import com.sanchez.fmf.model.MarketListItemModel;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by dakota on 9/2/15.
 */
public interface MarketService {

    @GET("/v1/data.svc/locSearch?lat={lat}&lng={lng}")
    void getMarkets(@Query("lat") double lat, @Query("lng") double lng, Callback<List<MarketListItemModel>> cb);

    @GET("/v1/data.svc/zipSearch?zip={zip}")
    void getMarkets(@Query("zip") int zip, Callback<List<MarketListItemModel>> cb);

    @GET("/v1/data.svc/mktDetail?id={id}")
    void getMarket(@Query("id") int id, Callback<MarketDetailModel> cb);
}
