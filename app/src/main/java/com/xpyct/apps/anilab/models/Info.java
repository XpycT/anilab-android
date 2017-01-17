package com.xpyct.apps.anilab.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * anilab-android
 * Created by XpycT on 09.07.2015.
 */
public class Info implements Serializable {
    @SerializedName("published_at")
    @Expose
    private String publishedAt;
    @Expose
    private Images images;
    @Expose
    private String year;
    @Expose
    private String production;
    @Expose
    private List<String> genres = new ArrayList<String>();
    @Expose
    private String type;
    @Expose
    private String series;
    @Expose
    private String aired;
    @Expose
    private List<String> producers = new ArrayList<String>();
    @Expose
    private List<String> authors = new ArrayList<String>();
    @Expose
    private List<String> scenarist = new ArrayList<String>();
    @Expose
    private List<String> postscoring = new ArrayList<String>();
    @Expose
    private String studio;
    @Expose
    private Boolean online;
    @Expose
    private Boolean torrent;
    @Expose
    private Comments comments;
    @Expose
    private List<Images> screenshots;

    /**
     * @return The publishedAt
     */
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     * @param publishedAt The published_at
     */
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * @return The images
     */
    public Images getImages() {
        return images;
    }

    /**
     * @param images The images
     */
    public void setImages(Images images) {
        this.images = images;
    }

    /**
     * @return The year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year The year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return The production
     */
    public String getProduction() {
        return production;
    }

    /**
     * @param production The production
     */
    public void setProduction(String production) {
        this.production = production;
    }

    /**
     * @return The genres
     */
    public List<String> getGenres() {
        return genres;
    }

    /**
     * @param genres The genres
     */
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The series
     */
    public String getSeries() {
        return series;
    }

    /**
     * @param series The series
     */
    public void setSeries(String series) {
        this.series = series;
    }

    /**
     * @return The aired
     */
    public String getAired() {
        return aired;
    }

    /**
     * @param aired The aired
     */
    public void setAired(String aired) {
        this.aired = aired;
    }

    /**
     * @return The producers
     */
    public List<String> getProducers() {
        return producers;
    }

    /**
     * @param producers The producers
     */
    public void setProducers(List<String> producers) {
        this.producers = producers;
    }

    /**
     * @return The authors
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * @param authors The authors
     */
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    /**
     * @return The scenarist
     */
    public List<String> getScenarist() {
        return scenarist;
    }

    /**
     * @param scenarist The scenarist
     */
    public void setScenarist(List<String> scenarist) {
        this.scenarist = scenarist;
    }

    /**
     * @return The postscoring
     */
    public List<String> getPostscoring() {
        return postscoring;
    }

    /**
     * @param postscoring The postscoring
     */
    public void setPostscoring(List<String> postscoring) {
        this.postscoring = postscoring;
    }

    /**
     * @return The studio
     */
    public String getStudio() {
        return studio;
    }

    /**
     * @param studio The studio
     */
    public void setStudio(String studio) {
        this.studio = studio;
    }

    /**
     * @return The online
     */
    public Boolean getOnline() {
        return online;
    }

    /**
     * @param online The online
     */
    public void setOnline(Boolean online) {
        this.online = online;
    }

    /**
     * @return The torrent
     */
    public Boolean getTorrent() {
        return torrent;
    }

    /**
     * @param torrent The torrent
     */
    public void setTorrent(Boolean torrent) {
        this.torrent = torrent;
    }

    /**
     * @return The comments
     */
    public Comments getComments() {
        return comments;
    }

    /**
     * @param comments The comments
     */
    public void setComments(Comments comments) {
        this.comments = comments;
    }

    /**
     * @return
     */
    public List<Images> getScreenshots() {
        return screenshots;
    }

    /**
     * @param screenshots
     */
    public void setScreenshots(List<Images> screenshots) {
        this.screenshots = screenshots;
    }
}
