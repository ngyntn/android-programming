package com.example.wallpaperclient.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "wallpapers")
public class WallpaperEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "local_path")
    public String localPath; // Đường dẫn file ảnh trên máy

    @ColumnInfo(name = "remote_id")
    public String remoteId; // ID từ Unsplash

    @ColumnInfo(name = "source_type")
    public String sourceType; // "UNSPLASH", "UPLOAD", "AI"

    @ColumnInfo(name = "status")
    public String status; // "ACTIVE", "TRASH"

    @ColumnInfo(name = "created_at")
    public long createdAt; // Thời gian tạo (long)

    @ColumnInfo(name = "ai_prompt")
    public String aiPrompt;

    // Constructor mặc định
    public WallpaperEntity(String localPath, String sourceType, long createdAt) {
        this.localPath = localPath;
        this.sourceType = sourceType;
        this.createdAt = createdAt;
        this.status = "ACTIVE"; // Mặc định là Active
    }
}
