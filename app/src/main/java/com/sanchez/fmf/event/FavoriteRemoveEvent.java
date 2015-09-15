package com.sanchez.fmf.event;

/**
 * Created by dakota on 9/14/15.
 */
public class FavoriteRemoveEvent {
    public final String id;

    public FavoriteRemoveEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
