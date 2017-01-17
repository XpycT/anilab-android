package com.xpyct.apps.anilab.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * anilab-android
 * Created by XpycT on 20.07.2015.
 */
public class File implements Serializable {
    @Expose
    private String service;
    @Expose
    private String part;
    @SerializedName("original_link")
    @Expose
    private String originalLink;
    @SerializedName("download_link")
    @Expose
    private String downloadLink;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getOriginalLink() {
        return originalLink;
    }

    public void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
