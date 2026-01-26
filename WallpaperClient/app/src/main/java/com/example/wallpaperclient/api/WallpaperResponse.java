package com.example.wallpaperclient.api;

import com.google.gson.annotations.SerializedName;

public class WallpaperResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("thumb_url")
    private String thumbUrl;

    @SerializedName("full_url")
    private String fullUrl;

    public String getId() {
        return id;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getFullUrl() {
        return fullUrl;
    }
}