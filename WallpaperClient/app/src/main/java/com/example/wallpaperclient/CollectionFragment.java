package com.example.wallpaperclient;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionFragment extends Fragment {

    // Views Tab
    private RecyclerView rcvCollectionTabs;

    // Layouts con
    private NestedScrollView layoutGallery;
    private LinearLayout layoutUpload;
    private ConstraintLayout layoutTrash;

    // Views trong Gallery
    private RecyclerView rcvLastWeek, rcvOlder;

    // Views trong Upload
    private AppCompatButton btnDoUpload;

    // Views trong Trash
    private RecyclerView rcvTrash;
    private TextView btnCleanTrash, tvEmptyTrash;

    // Data Adapters
    private TagAdapter tabAdapter;
    private WallpaperAdapter trashAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ Views
        initViews(view);

        // 2. Setup Tabs (Gallery, Upload, Trash)
        setupTabs();

        // 3. Setup Nội dung từng màn hình
        setupGalleryView();
        setupUploadView();
        setupTrashView();
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

    private void setupTabs() {
        List<String> tabs = Arrays.asList("Gallery", "Upload", "Trash");

        tabAdapter = new TagAdapter(getContext(), tabs, tagName -> {
            // Logic chuyển Tab
            switch (tagName) {
                case "Gallery":
                    layoutGallery.setVisibility(View.VISIBLE);
                    layoutUpload.setVisibility(View.GONE);
                    layoutTrash.setVisibility(View.GONE);
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
                    break;
            }
        });

        tabAdapter.setSelectedPosition(0);

        rcvCollectionTabs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvCollectionTabs.setAdapter(tabAdapter);
    }

    private void setupGalleryView() {
        // Setup Grid cho Last Week
        rcvLastWeek.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rcvLastWeek.setAdapter(new WallpaperAdapter(getContext(), getDummyData(6), this::showUnsaveDialog));

        // Setup Grid cho Older
        rcvOlder.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rcvOlder.setAdapter(new WallpaperAdapter(getContext(), getDummyData(9), this::showUnsaveDialog));
    }

    private void setupUploadView() {
        btnDoUpload.setOnClickListener(v -> {
            // Giả lập upload
            Toast.makeText(getContext(), "Uploading...", Toast.LENGTH_SHORT).show();
            v.postDelayed(() -> {
                Toast.makeText(getContext(), "Upload Successful! Added to Gallery.", Toast.LENGTH_LONG).show();
            }, 1500);
        });
    }

    private void setupTrashView() {
        rcvTrash.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Giả lập dữ liệu trong thùng rác (3 ảnh)
        List<Integer> trashData = getDummyData(3);
        trashAdapter = new WallpaperAdapter(getContext(), trashData, resourceId -> {
            Toast.makeText(getContext(), "Item is in Trash", Toast.LENGTH_SHORT).show();
        });
        rcvTrash.setAdapter(trashAdapter);

        // Sự kiện làm sạch thùng rác
        btnCleanTrash.setOnClickListener(v -> {
            trashData.clear();
            trashAdapter.notifyDataSetChanged();

            // Ẩn nút clean, hiện thông báo trống
            btnCleanTrash.setVisibility(View.GONE);
            tvEmptyTrash.setVisibility(View.VISIBLE);

            Toast.makeText(getContext(), "Trash Cleaned", Toast.LENGTH_SHORT).show();
        });
    }

    // Logic Dialog Unsave
    private void showUnsaveDialog(int resourceId) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wallpaper_detail);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ImageView imgDetail = dialog.findViewById(R.id.imgDetail);
        imgDetail.setImageResource(resourceId);

        AppCompatButton btnAction = dialog.findViewById(R.id.btnSave);
        btnAction.setText("Unsave"); // Đổi thành nút xóa

        btnAction.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Moved to Trash", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        AppCompatButton btnSetWallpaper = dialog.findViewById(R.id.btnSetWallpaper);
        btnSetWallpaper.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Wallpaper Set!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private List<Integer> getDummyData(int count) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(R.drawable.img_demo);
        }
        return list;
    }
}