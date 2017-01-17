package com.xpyct.apps.anilab.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * anilab-android
 * Created by XpycT on 09.07.2015.
 */
public class Comments implements Serializable {
    @Expose
    private String status;
    @Expose
    private String count;
    @Expose
    private ArrayList<Comment> list = new ArrayList<>();

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The count
     */
    public String getCount() {
        return count;
    }

    /**
     * @param count The count
     */
    public void setCount(String count) {
        this.count = count;
    }

    /**
     * @return The list
     */
    public ArrayList<Comment> getList() {
        return list;
    }

    /**
     * @param list The list
     */
    public void setList(ArrayList<Comment> list) {
        this.list = list;
    }

}
