package com.xpyct.apps.anilab.models.myvi;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * anilab-android
 * Created by XpycT on 16.08.2015.
 */
public class SprutoData {
    private List<Playlist> playlist = new ArrayList<Playlist>();
    @Expose
    private String playlistUrl;

    /**
     * @return The playlist
     */
    public List<Playlist> getPlaylist() {
        return playlist;
    }

    /**
     * @param playlist The playlist
     */
    public void setPlaylist(List<Playlist> playlist) {
        this.playlist = playlist;
    }

    /**
     * @return The playlistUrl
     */
    public String getPlaylistUrl() {
        return playlistUrl;
    }

    /**
     * @param playlistUrl The playlistUrl
     */
    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }
}
