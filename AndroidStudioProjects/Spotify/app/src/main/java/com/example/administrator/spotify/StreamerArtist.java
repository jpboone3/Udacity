package com.example.administrator.spotify;

import android.graphics.Bitmap;

/**
 * Object to hold album/track data for the ListViews
 */
public class StreamerArtist implements Comparable<StreamerArtist> {

    private String name;                  //artist or track name
    private String preview_url = null;    // preview song track
    private String song_image_url = null; // large image of song

    private Bitmap thumbnail = null;      // thumbnail of album/song image
    private int popularity = -1;          // popularity of the song (based on 0 - 100)
    private boolean selected = false;     // item selected

    @Override
    public int compareTo(StreamerArtist compareStreamerArtist) {
        // we want descending order

        if (compareStreamerArtist.popularity < popularity)
            return -1;
        if (compareStreamerArtist.popularity > popularity)
            return 1;
        return 0;

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPopularity() {
        return this.popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getPreviewUrl() {
        return this.preview_url;
    }

    public void setPreviewUrl(String preview_url) {
        this.preview_url = preview_url;
    }

    public String getSongImageUtl() {
        return this.song_image_url;
    }

    public void setSongImageUtl(String song_image_url) {
        this.song_image_url = song_image_url;
    }

    public Bitmap getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean state) {
        this.selected = state;
        return;
    }
}
