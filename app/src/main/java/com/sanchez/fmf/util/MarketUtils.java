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

    // get distance (in miles) from 'marketName' json key
    public static String getDistanceFromMarketString(String marketString) {
        StringBuilder dist = new StringBuilder();
        int i = 0;
        while (marketString.charAt(i) != ' ') {
            dist.append(marketString.charAt(i));
            i++;
        }
        return dist.toString();
    }

    // strip distance (in miles) from 'marketName' json key
    public static String getNameFromMarketString(String marketString) {
        return marketString.substring(marketString.indexOf(" ") + 1);
    }

    public static double[] getCoordinatesFromMapUrl(String mapUrl) {
        int queryStart = mapUrl.indexOf("=") + 1;
        int nameStart = mapUrl.indexOf("(");

        String[] coordinates = mapUrl.substring(queryStart, nameStart).split("%20");
        coordinates[0] = coordinates[0].replace("%2C", "");

        return new double[] {Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1])};
    }

    public static String getQueryFromMapUrl(String mapUrl) {
        int queryStart = mapUrl.indexOf("=") + 1;
        return mapUrl.substring(queryStart);
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
