package com.sanchez.fmf.event;

/**
 * Created by dakota on 9/17/15.
 */
public class PlaceTitleResolvedEvent {
    private String placeTitle;

    public PlaceTitleResolvedEvent(String placeTitle) {
        this.placeTitle = placeTitle;
    }

    public String getPlaceTitle() {
        return placeTitle;
    }
}
