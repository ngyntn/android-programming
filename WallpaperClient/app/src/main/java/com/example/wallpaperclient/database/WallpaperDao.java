package com.example.wallpaperclient.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WallpaperDao {

    // 1. Thêm ảnh mới (Upload hoặc Save)
    @Insert
    void insertWallpaper(WallpaperEntity wallpaper);

    // 2. Lấy danh sách ảnh đang hiển thị (Active), mới nhất lên đầu
    @Query("SELECT * FROM wallpapers WHERE status = 'ACTIVE' ORDER BY created_at DESC")
    List<WallpaperEntity> getAllActiveWallpapers();

    // 3. Lấy danh sách thùng rác (Trash)
    @Query("SELECT * FROM wallpapers WHERE status = 'TRASH' ORDER BY created_at DESC")
    List<WallpaperEntity> getTrashWallpapers();

    // 4. Cập nhật trạng thái (Dùng cho Unsave -> Trash hoặc Restore -> Active)
    @Query("UPDATE wallpapers SET status = :newStatus WHERE id = :id")
    void updateStatus(int id, String newStatus);

    // 5. Dọn sạch thùng rác (Xóa vĩnh viễn)
    @Query("DELETE FROM wallpapers WHERE status = 'TRASH'")
    void clearTrash();

    // 6. Kiểm tra xem ảnh Unsplash này đã lưu chưa (để tránh lưu trùng)
    @Query("SELECT * FROM wallpapers WHERE remote_id = :remoteId LIMIT 1")
    WallpaperEntity checkDuplicate(String remoteId);


    // 7. Xóa 1 ảnh cụ thể
    @Delete
    void deleteWallpaper(WallpaperEntity wallpaper);
}