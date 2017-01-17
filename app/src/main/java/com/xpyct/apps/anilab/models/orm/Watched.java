package com.xpyct.apps.anilab.models.orm;

import com.orm.SugarRecord;

public class Watched extends SugarRecord {

    private String movieId;
    private String service;
    private String part;

    public Watched() {
    }

    /**
     * default constructor
     *
     * @param movieId String movie id
     * @param service String service name
     * @param part    String part name
     */
    public Watched(String movieId, String service, String part) {
        this.movieId = movieId;
        this.service = service;
        this.part = part;
    }
}
