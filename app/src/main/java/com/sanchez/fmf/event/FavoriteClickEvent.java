package com.sanchez.fmf.event;

/**
 * Created by dakota on 9/14/15.
 */
public class FavoriteClickEvent {
    public final String id;
    public final String name;

    public FavoriteClickEvent(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
