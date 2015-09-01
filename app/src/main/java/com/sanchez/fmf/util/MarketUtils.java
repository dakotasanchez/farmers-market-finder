package com.sanchez.fmf.util;

import com.sanchez.fmf.model.MarketListItemModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dakota on 8/31/15.
 */
public class MarketUtils {

    public static ArrayList<MarketListItemModel> getExampleMarkets() {
        ArrayList<MarketListItemModel> mModels = new ArrayList<>();
        Random r = new Random();
        DecimalFormat df = new DecimalFormat("#.0");
        for (int i = 0; i < 50; i++) {
            double dist = r.nextDouble() * 50;
            mModels.add(new MarketListItemModel(Integer.toString(i), "Definitely a Market\n" + i,
                    Double.parseDouble(df.format(dist))));
        }
        return mModels;
    }
}
