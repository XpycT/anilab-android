package com.xpyct.apps.anilab.models.myvi;

import com.google.gson.annotations.Expose;

/**
 * anilab-android
 * Created by XpycT on 16.08.2015.
 */
public class MyviFile {
    @Expose
    private SprutoData sprutoData;

    /**
     *
     * @return
     * The sprutoData
     */
    public SprutoData getSprutoData() {
        return sprutoData;
    }

    /**
     *
     * @param sprutoData
     * The sprutoData
     */
    public void setSprutoData(SprutoData sprutoData) {
        this.sprutoData = sprutoData;
    }
}
