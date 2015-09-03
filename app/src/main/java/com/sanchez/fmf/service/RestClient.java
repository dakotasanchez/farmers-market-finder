package com.sanchez.fmf.service;

import com.squareup.moshi.Moshi;

import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;

/**
 * Created by dakota on 9/2/15.
 */
public class RestClient {

    private static final String BASE_URL = "http://search.ams.usda.gov/farmersmarkets";
    private MarketService marketService;

    public RestClient() {
        Moshi moshi = new Moshi.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();

        marketService = retrofit.create(MarketService.class);
    }

    public MarketService getMarketService() {
        return marketService;
    }
}
