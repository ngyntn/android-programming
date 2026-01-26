package com.example.wallpaperclient;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wallpaperclient.api.ApiClient;
import com.example.wallpaperclient.api.ApiService;
import com.example.wallpaperclient.database.AppDatabase;
import com.example.wallpaperclient.database.WallpaperEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WallpaperUtils {

    /**
     * Saves the wallpaper to internal storage and registers it in the database.
     * Handles both remote URLs and local file paths.
     */
    public static void saveWallpaper(Context context, WallpaperEntity currentItem) {
        // Distinguish between remote URLs (download required) and local files
        if (currentItem.localPath.startsWith("http")) {
            downloadAndSave(context, currentItem);
        } else {
            saveLocalImageToGallery(context, currentItem);
        }
    }

    private static void downloadAndSave(Context context, WallpaperEntity item) {
        Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();

        ApiService apiService = ApiClient.getApiService();
        // Initiate download via proxy API
        apiService.downloadImage(item.localPath).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    File savedFile = saveFileToInternalStorage(context, response.body().byteStream(), "img_" + item.remoteId);

                    if (savedFile != null) {
                        saveToDatabase(context, savedFile.getAbsolutePath(), item.remoteId, "UNSPLASH");
                        Toast.makeText(context, "Saved to My Collection!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void saveLocalImageToGallery(Context context, WallpaperEntity item) {
        // Move AI-generated artifact from temporary cache to permanent internal storage
        try {
            File sourceFile = new File(item.localPath);
            InputStream in = new FileInputStream(sourceFile);
            File savedFile = saveFileToInternalStorage(context, in, "ai_" + System.currentTimeMillis());

            if (savedFile != null) {
                saveToDatabase(context, savedFile.getAbsolutePath(), null, "AI");
                Toast.makeText(context, "Saved to My Collection!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper: Streams input data to a local file in the app's internal storage.
     */
    private static File saveFileToInternalStorage(Context context, InputStream inputStream, String fileName) {
        try {
            File dir = new File(context.getFilesDir(), "saved_wallpapers");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, fileName + ".png");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inserts metadata into the local database, preventing duplicates for remote items.
     */
    private static void saveToDatabase(Context context, String path, String remoteId, String source) {
        AppDatabase db = AppDatabase.getInstance(context);

        // Prevent duplicate entries for the same remote ID
        if (remoteId != null && db.wallpaperDao().checkDuplicate(remoteId) != null) {
            return;
        }

        WallpaperEntity entity = new WallpaperEntity(path, source, System.currentTimeMillis());
        entity.remoteId = remoteId;
        db.wallpaperDao().insertWallpaper(entity);
    }

    /**
     * Applies the selected bitmap to both Home and Lock screens using WallpaperManager.
     */
    public static void setWallpaper(Context context, WallpaperEntity item) {
        Toast.makeText(context, "Setting wallpaper...", Toast.LENGTH_SHORT).show();

        Glide.with(context)
                .asBitmap()
                .load(item.localPath)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                            wallpaperManager.setBitmap(resource);
                            Toast.makeText(context, "Wallpaper applied successfully!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(context, "Failed to set wallpaper", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }
}