package com.sanchez.fmf.util;

import com.sanchez.fmf.R;

import java.util.Random;

/**
 * Created by dakota on 8/31/15.
 */
public class MarketUtils {

    private static final Random RANDOM = new Random();

    // Non-instantiatable class
    private MarketUtils() {
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double radius = 3959; // miles
        return radius * c;
    }

    public static int getRandomMarketColor() {

        switch (RANDOM.nextInt(5)) {
            default:
            case 0:
                return R.color.market_blue;
            case 1:
                return R.color.market_red;
            case 2:
                return R.color.market_green;
            case 3:
                return R.color.market_orange;
            case 4:
                return R.color.market_pink;
        }
    }

    public static int getRandomMarketDrawable() {
        switch (RANDOM.nextInt(4)) {
            default:
            case 0:
                return R.drawable.market_1;
            case 1:
                return R.drawable.market_2;
            case 2:
                return R.drawable.market_3;
            case 3:
                return R.drawable.market_4;
        }
    }
}
