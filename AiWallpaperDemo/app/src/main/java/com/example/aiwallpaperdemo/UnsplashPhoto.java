package com.example.aiwallpaperdemo;

import com.google.gson.annotations.SerializedName;

public class UnsplashPhoto {

    // ID ảnh
    @SerializedName("id")
    public String id;

    // Mô tả ảnh (có thể null)
    @SerializedName("description")
    public String description;

    // Object chứa các link ảnh
    @SerializedName("urls")
    public Urls urls;

    // Object chứa thông tin tác giả
    @SerializedName("user")
    public User user;

    // Object chứa link trigger download (QUAN TRỌNG VỚI UNSPLASH)
    @SerializedName("links")
    public Links links;

    // --- Inner Classes (Class con để map object lồng nhau) ---

    public static class Urls {
        @SerializedName("regular")
        public String regular; // Ảnh kích thước vừa phải cho điện thoại

        @SerializedName("full")
        public String full;    // Ảnh gốc (nặng)
    }

    public static class User {
        @SerializedName("name")
        public String name;

        @SerializedName("username")
        public String username;
    }

    public static class Links {
        @SerializedName("download_location")
        public String downloadLocation; // Gọi cái này để tính lượt tải
    }
}
