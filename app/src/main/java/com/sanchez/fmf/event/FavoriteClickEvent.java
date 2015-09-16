package com.sanchez.fmf.event;

/**
 * Created by dakota on 9/14/15.
 */
public class FavoriteClickEvent {
    private final String id;
    private final String name;

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
