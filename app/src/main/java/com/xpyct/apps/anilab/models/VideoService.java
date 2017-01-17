package com.xpyct.apps.anilab.models;

/**
 * anilab-android
 * Created by XpycT on 22.07.2015.
 */
public class VideoService {

    private String key;
    private String value;
    private String original_url;
    private String download_url;

    public VideoService(String key, String value, String original_url, String download_url) {
        this.key = key;
        this.value = value;
        this.original_url = original_url;
        this.download_url = download_url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOriginal_url() {
        return original_url;
    }

    public void setOriginal_url(String original_url) {
        this.original_url = original_url;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }
}
