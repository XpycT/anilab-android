package com.xpyct.apps.anilab.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Comment implements Serializable {
    @SerializedName("comment_id")
    @Expose
    private String commentId;
    @Expose
    private String date;
    @Expose
    private String author;
    @Expose
    private String body;
    @Expose
    private String avatar;

    /**
     * @return The commentId
     */
    public String getCommentId() {
        return commentId;
    }

    /**
     * @param commentId The comment_id
     */
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    /**
     * @return The date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return The body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body The body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return The avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * @param avatar The avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
