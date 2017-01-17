package com.xpyct.apps.anilab.models.myvi;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * anilab-android
 * Created by XpycT on 16.08.2015.
 */
public class Playlist {
    @Expose
    private Integer duration;
    @Expose
    private String embedHtml;
    @Expose
    private String link;
    @Expose
    private String posterUrl;
    @Expose
    private String title;
    @Expose
    private List<Video> video = new ArrayList<>();
    @Expose
    private String videoId;

    /**
     * @return The duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * @param duration The duration
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * @return The embedHtml
     */
    public String getEmbedHtml() {
        return embedHtml;
    }

    /**
     * @param embedHtml The embedHtml
     */
    public void setEmbedHtml(String embedHtml) {
        this.embedHtml = embedHtml;
    }

    /**
     * @return The link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return The posterUrl
     */
    public String getPosterUrl() {
        return posterUrl;
    }

    /**
     * @param posterUrl The posterUrl
     */
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The video
     */
    public List<Video> getVideo() {
        return video;
    }

    /**
     * @param video The video
     */
    public void setVideo(List<Video> video) {
        this.video = video;
    }

    /**
     * @return The videoId
     */
    public String getVideoId() {
        return videoId;
    }

    /**
     * @param videoId The videoId
     */
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

}
