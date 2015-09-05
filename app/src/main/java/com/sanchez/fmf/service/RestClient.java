package com.sanchez.fmf.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by dakota on 9/2/15.
 */
public class RestClient {

    private static final String BASE_URL = "http://search.ams.usda.gov/farmersmarkets";
    private MarketService marketService;

    public RestClient() {

//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(MarketListModel.class, new MarketListModelDeserializer())
//                .create();
        Gson gson = new GsonBuilder()
                .create();

        RestAdapter retrofit = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        marketService = retrofit.create(MarketService.class);
    }

    public MarketService getMarketService() {
        return marketService;
    }
}
