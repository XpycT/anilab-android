package com.xpyct.apps.anilab.models;

import com.google.gson.annotations.Expose;

public class MovieFull {
    @Expose
    private String status;
    @Expose
    private Movie movie = new Movie();

    /**
     * Get status
     *
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set status
     *
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get movie
     *
     * @return The movie
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * Set movie
     *
     * @param movie The movie
     */
    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
