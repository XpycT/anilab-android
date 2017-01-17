package com.xpyct.apps.anilab.models.myvi;

import com.google.gson.annotations.Expose;

/**
 * anilab-android
 * Created by XpycT on 16.08.2015.
 */
public class Video {
    @Expose
    private String url;

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
