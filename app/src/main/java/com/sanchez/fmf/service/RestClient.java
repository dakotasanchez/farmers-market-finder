package com.sanchez.fmf.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by dakota on 9/2/15.
 */
public class RestClient {

    private static final String BASE_URL = "http://10.0.2.2:5000";
    private MarketService marketService;

    public RestClient() {

        Gson gson = new Gson();

        RestAdapter retrofit = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setEndpoint(BASE_URL)
                .setClient(new OkClient(new OkHttpClient()))
                .setConverter(new GsonConverter(gson))
                .build();

        marketService = retrofit.create(MarketService.class);
    }

    public MarketService getMarketService() {
        return marketService;
    }
}
