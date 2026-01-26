package com.example.wallpaperclient;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wallpaperclient.database.AppDatabase;
import com.example.wallpaperclient.database.WallpaperEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionFragment extends Fragment {

    // UI Components
    private RecyclerView rcvCollectionTabs;
    private NestedScrollView layoutGallery;
    private LinearLayout layoutUpload;
    private ConstraintLayout layoutTrash;
    private RecyclerView rcvLastWeek, rcvOlder, rcvTrash;
    private AppCompatButton btnDoUpload;
    private TextView btnCleanTrash, tvEmptyTrash;

    // Adapters
    private TagAdapter tabAdapter;
    private WallpaperAdapter lastWeekAdapter, olderAdapter, trashAdapter;

    private AppDatabase db;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(getContext());

        initViews(view);
        setupTabs();
        setupAdapters();
        setupImagePicker();

        loadGalleryData();
        loadTrashData();
    }

    private void initViews(View view) {
        rcvCollectionTabs = view.findViewById(R.id.rcvCollectionTabs);
        layoutGallery = view.findViewById(R.id.layoutGallery);
        layoutUpload = view.findViewById(R.id.layoutUpload);
        layoutTrash = view.findViewById(R.id.layoutTrash);
        rcvLastWeek = view.findViewById(R.id.rcvLastWeek);
        rcvOlder = view.findViewById(R.id.rcvOlder);
        btnDoUpload = view.findViewById(R.id.btnDoUpload);
        rcvTrash = view.findViewById(R.id.rcvTrash);
        btnCleanTrash = view.findViewById(R.id.btnCleanTrash);
        tvEmptyTrash = view.findViewById(R.id.tvEmptyTrash);
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        handleImageSelected(uri);
                    }
                }
        );
    }

    private void setupAdapters() {
        // Gallery Adapters
        lastWeekAdapter = new WallpaperAdapter(getContext(), new ArrayList<>(), this::showGalleryDetailDialog);
        rcvLastWeek.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rcvLastWeek.setAdapter(lastWeekAdapter);

        olderAdapter = new WallpaperAdapter(getContext(), new ArrayList<>(), this::showGalleryDetailDialog);
        rcvOlder.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rcvOlder.setAdapter(olderAdapter);

        // Trash Adapter
        trashAdapter = new WallpaperAdapter(getContext(), new ArrayList<>(), this::showTrashDetailDialog);
        rcvTrash.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rcvTrash.setAdapter(trashAdapter);

        btnDoUpload.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnCleanTrash.setOnClickListener(v -> handleCleanTrash());
    }

    private void handleImageSelected(Uri sourceUri) {
        Toast.makeText(getContext(), "Processing image...", Toast.LENGTH_SHORT).show();

        // Copy file to internal storage to maintain access rights across app restarts
        File savedFile = copyUriToInternalStorage(sourceUri);

        if (savedFile != null) {
            WallpaperEntity newImg = new WallpaperEntity(
                    savedFile.getAbsolutePath(),
                    "UPLOAD",
                    System.currentTimeMillis()
            );
            db.wallpaperDao().insertWallpaper(newImg);

            Toast.makeText(getContext(), "Upload Successful!", Toast.LENGTH_SHORT).show();

            loadGalleryData();
            // Switch view context to Gallery
            tabAdapter.setSelectedPosition(0);
            layoutGallery.setVisibility(View.VISIBLE);
            layoutUpload.setVisibility(View.GONE);
            layoutTrash.setVisibility(View.GONE);
        } else {
            Toast.makeText(getContext(), "Failed to import image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Copies content from a content URI to the app's private files directory.
     */
    private File copyUriToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            File dir = new File(getContext().getFilesDir(), "uploads");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "upload_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleCleanTrash() {
        // Purge physical files before clearing DB records
        List<WallpaperEntity> trashItems = db.wallpaperDao().getTrashWallpapers();
        for (WallpaperEntity item : trashItems) {
            deletePhysicalFile(item.localPath);
        }

        db.wallpaperDao().clearTrash();
        loadTrashData();
        Toast.makeText(getContext(), "Trash Cleaned", Toast.LENGTH_SHORT).show();
    }

    private void loadGalleryData() {
        List<WallpaperEntity> allActive = db.wallpaperDao().getAllActiveWallpapers();
        List<WallpaperEntity> lastWeekList = new ArrayList<>();
        List<WallpaperEntity> olderList = new ArrayList<>();

        long oneWeekInMillis = 7 * 24 * 60 * 60 * 1000L;
        long now = System.currentTimeMillis();

        for (WallpaperEntity item : allActive) {
            if (now - item.createdAt < oneWeekInMillis) {
                lastWeekList.add(item);
            } else {
                olderList.add(item);
            }
        }
        lastWeekAdapter.setData(lastWeekList);
        olderAdapter.setData(olderList);
    }

    private void loadTrashData() {
        List<WallpaperEntity> trashList = db.wallpaperDao().getTrashWallpapers();
        trashAdapter.setData(trashList);

        if (trashList.isEmpty()) {
            btnCleanTrash.setVisibility(View.GONE);
            tvEmptyTrash.setVisibility(View.VISIBLE);
        } else {
            btnCleanTrash.setVisibility(View.VISIBLE);
            tvEmptyTrash.setVisibility(View.GONE);
        }
    }

    private void showGalleryDetailDialog(WallpaperEntity wallpaper) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wallpaper_detail);
        setupDialogWindow(dialog);

        ImageView imgDetail = dialog.findViewById(R.id.imgDetail);
        AppCompatButton btnLeft = dialog.findViewById(R.id.btnSave);
        AppCompatButton btnRight = dialog.findViewById(R.id.btnSetWallpaper);

        Glide.with(this).load(wallpaper.localPath).into(imgDetail);

        // Action: Move to Trash
        btnLeft.setText("Unsave");
        btnLeft.setOnClickListener(v -> {
            db.wallpaperDao().updateStatus(wallpaper.id, "TRASH");
            Toast.makeText(getContext(), "Moved to Trash", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadGalleryData();
            loadTrashData();
        });

        // Action: Set Wallpaper
        btnRight.setText("Set Wallpaper");
        btnRight.setOnClickListener(v -> {
            WallpaperUtils.setWallpaper(getContext(), wallpaper);
        });

        dialog.show();
    }

    private void showTrashDetailDialog(WallpaperEntity wallpaper) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wallpaper_detail);
        setupDialogWindow(dialog);

        ImageView imgDetail = dialog.findViewById(R.id.imgDetail);
        AppCompatButton btnLeft = dialog.findViewById(R.id.btnSave);
        AppCompatButton btnRight = dialog.findViewById(R.id.btnSetWallpaper);

        Glide.with(this).load(wallpaper.localPath).into(imgDetail);

        // Action: Restore
        btnLeft.setText("Restore");
        btnLeft.setOnClickListener(v -> {
            db.wallpaperDao().updateStatus(wallpaper.id, "ACTIVE");
            Toast.makeText(getContext(), "Restored!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadGalleryData();
            loadTrashData();
        });

        // Action: Permanent Delete
        btnRight.setText("Delete Forever");
        btnRight.setOnClickListener(v -> {
            deletePhysicalFile(wallpaper.localPath);
            db.wallpaperDao().deleteWallpaper(wallpaper);

            Toast.makeText(getContext(), "Deleted Permanently", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadTrashData();
        });

        dialog.show();
    }

    private void setupDialogWindow(Dialog dialog) {
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void deletePhysicalFile(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTabs() {
        List<String> tabs = Arrays.asList("Gallery", "Upload", "Trash");
        tabAdapter = new TagAdapter(getContext(), tabs, tagName -> {
            switch (tagName) {
                case "Gallery":
                    layoutGallery.setVisibility(View.VISIBLE);
                    layoutUpload.setVisibility(View.GONE);
                    layoutTrash.setVisibility(View.GONE);
                    loadGalleryData();
                    break;
                case "Upload":
                    layoutGallery.setVisibility(View.GONE);
                    layoutUpload.setVisibility(View.VISIBLE);
                    layoutTrash.setVisibility(View.GONE);
                    break;
                case "Trash":
                    layoutGallery.setVisibility(View.GONE);
                    layoutUpload.setVisibility(View.GONE);
                    layoutTrash.setVisibility(View.VISIBLE);
                    loadTrashData();
                    break;
            }
        });
        tabAdapter.setSelectedPosition(0);
        rcvCollectionTabs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvCollectionTabs.setAdapter(tabAdapter);
    }
}