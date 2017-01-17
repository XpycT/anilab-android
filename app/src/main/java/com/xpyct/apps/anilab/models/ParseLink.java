package com.xpyct.apps.anilab.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * anilab-android
 * Created by XpycT on 09.07.2015.
 */
public class ParseLink implements Serializable {
    @SerializedName("original_link")
    @Expose
    private String originalLink;
    @SerializedName("download_link")
    @Expose
    private String downloadLink;

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
