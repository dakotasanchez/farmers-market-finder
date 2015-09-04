package com.sanchez.fmf.service.json;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.sanchez.fmf.model.MarketListModel;

import java.lang.reflect.Type;

/**
 * Created by dakota on 9/4/15.
 */
public class MarketListModelDeserializer implements JsonDeserializer<MarketListModel> {

    @Override
    public MarketListModel deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException
    {
        return new Gson().fromJson(je.toString(), MarketListModel.class);

    }
}
