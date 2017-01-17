package com.xpyct.apps.anilab.models.orm;

import com.orm.SugarRecord;

/**
 * anilab-android
 * Created by XpycT on 26.07.2015.
 */
public class Favorites extends SugarRecord {

    private String movieId;
    private long service;
    private String title;
    private String poster;

    public Favorites() {
    }

    /**
     * default constructor
     *
     * @param movieId String movie id
     * @param service String service name
     * @param title   String movie title
     * @param poster  movie image url
     */
    public Favorites(String movieId, long service, String title, String poster) {
        this.movieId = movieId;
        this.service = service;
        this.title = title;
        this.poster = poster;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public long getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
