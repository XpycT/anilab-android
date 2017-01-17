package com.xpyct.apps.anilab.models;

/**
 * anilab-android
 * Created by XpycT on 16.07.2015.
 */
public class InfoItem {

    private String title;
    private String value;

    public InfoItem(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
