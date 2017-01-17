package com.xpyct.apps.anilab.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Movie implements Serializable, Parcelable {

    @SerializedName("movie_id")
    @Expose
    private String movieId;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @Expose
    private Integer id;
    @Expose
    private String title;
    @Expose
    private Info info;
    @Expose
    private String description;

    private boolean commonFavorite;
    private long movieService;

    public long getMovieService() {
        return movieService;
    }

    public void setMovieService(long movieService) {
        this.movieService = movieService;
    }

    public boolean isCommonFavorite() {
        return commonFavorite;
    }

    public void setCommonFavorite(boolean commonFavorite) {
        this.commonFavorite = commonFavorite;
    }

    /**
     * @return The movieId
     */
    public String getMovieId() {
        return movieId;
    }

    /**
     * @param movieId The movie_id
     */
    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    /**
     * @return The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
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
     * @return The info
     */
    public Info getInfo() {
        return info;
    }

    /**
     * @param info The info
     */
    public void setInfo(Info info) {
        this.info = info;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public Movie() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.movieId);
        dest.writeString(this.updatedAt);
        dest.writeString(this.createdAt);
        dest.writeValue(this.id);
        dest.writeString(this.title);
        dest.writeSerializable(this.info);
        dest.writeString(this.description);
        dest.writeByte(commonFavorite ? (byte) 1 : (byte) 0);
    }

    protected Movie(Parcel in) {
        this.movieId = in.readString();
        this.updatedAt = in.readString();
        this.createdAt = in.readString();
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.title = in.readString();
        this.info = (Info) in.readSerializable();
        this.description = in.readString();
        this.commonFavorite = in.readByte() != 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
