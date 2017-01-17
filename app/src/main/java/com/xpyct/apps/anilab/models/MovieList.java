package com.xpyct.apps.anilab.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class MovieList {
    @Expose
    private String status;
    @Expose
    private Integer page;
    @Expose
    private ArrayList<Movie> movies = new ArrayList<>();

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
     * Get page number
     *
     * @return The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * Set page number
     *
     * @param page The page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * Get movies list
     *
     * @return The movies
     */
    public ArrayList<Movie> getMovies() {
        return movies;
    }

    /**
     * Set movies list
     *
     * @param movies The movies
     */
    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }
}
