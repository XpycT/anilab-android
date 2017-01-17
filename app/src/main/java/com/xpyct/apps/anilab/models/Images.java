package com.xpyct.apps.anilab.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * anilab-android
 * Created by XpycT on 09.07.2015.
 */
public class Images implements Serializable {
    @Expose
    private String thumbnail;
    @Expose
    private String original;

    /**
     * @return The thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail The thumbnail
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * @return The original
     */
    public String getOriginal() {
        return original;
    }

    /**
     * @param original The original
     */
    public void setOriginal(String original) {
        this.original = original;
    }
}
