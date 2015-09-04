package com.sanchez.fmf.util;

import com.sanchez.fmf.model.MarketListItemModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dakota on 8/31/15.
 */
public class MarketUtils {

//    public static ArrayList<MarketListItemModel> getExampleMarkets() {
//        ArrayList<MarketListItemModel> mModels = new ArrayList<>();
//        String alphabet = " 1234567890 abcdefghijklmnopqrstuvwxyz ";
//        Random r = new Random();
//        DecimalFormat df = new DecimalFormat("#.0");
//        for (int i = 0; i < 50; i++) {
//            double dist = r.nextDouble() * 50;
//            int marketLength = r.nextInt(50) + 1;
//            StringBuilder sb = new StringBuilder();
//            sb.append(Double.parseDouble(df.format(dist)) + " ");
//            for(int j = 0; j < marketLength; j++) {
//                sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
//            }
//            mModels.add(new MarketListItemModel(Integer.toString(i), sb.toString()));
//        }
//        return mModels;
//    }

    // get distance (in miles) from 'marketName' json key
    public static String getDistanceFromName(String marketName) {
        StringBuilder dist = new StringBuilder();
        int i = 0;
        while (marketName.charAt(i) != ' ') {
            dist.append(marketName.charAt(i));
            i++;
        }
        return dist.toString();
    }
}
