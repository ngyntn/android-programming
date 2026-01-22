package com.example.wallpaperclient;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    // Views
    private RecyclerView rcvPopular, rcvSearched, rcvTags;
    private TextView tvSearchedTitle;
    private EditText edtSearch;

    // Adapters
    private WallpaperAdapter popularAdapter;
    private WallpaperAdapter searchedAdapter;
    private TagAdapter tagAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ View
        rcvPopular = view.findViewById(R.id.rcvWallpapers);
        rcvSearched = view.findViewById(R.id.rcvSearched);
        rcvTags = view.findViewById(R.id.rcvTags);
        tvSearchedTitle = view.findViewById(R.id.tvSearchedTitle);
        edtSearch = view.findViewById(R.id.edtSearch);

        // 2. Setup Popular RecyclerView (Grid 3 cột)
        setupPopularList();

        // 3. Setup Tags RecyclerView (Horizontal)
        setupTagList();

        // 4. Setup Search Action
        setupSearchAction();
    }

    private void setupPopularList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        rcvPopular.setLayoutManager(gridLayoutManager);

        // Truyền thêm listener vào Adapter để xử lý click mở Dialog
        popularAdapter = new WallpaperAdapter(getContext(), getDummyData(15), resourceId -> {
            showDetailDialog(resourceId);
        });
        rcvPopular.setAdapter(popularAdapter);
    }

    private void setupTagList() {
        // Danh sách tag mẫu
        List<String> tags = Arrays.asList("Nature", "Abstract", "Cars", "Space", "Minimal");

        tagAdapter = new TagAdapter(getContext(), tags, tagName -> {
            // Logic khi click vào tag: Chỉ lọc Popular list
            Toast.makeText(getContext(), "Filtering Popular by: " + tagName, Toast.LENGTH_SHORT).show();
            // TODO: Sau này gọi API filter ở đây
        });

        rcvTags.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvTags.setAdapter(tagAdapter);
    }

    private void setupSearchAction() {
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(edtSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) return;

        // Ẩn bàn phím
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

        // Hiển thị vùng Search
        tvSearchedTitle.setVisibility(View.VISIBLE);
        rcvSearched.setVisibility(View.VISIBLE);

        // Setup Searched List
        GridLayoutManager searchLayoutManager = new GridLayoutManager(getContext(), 3);
        rcvSearched.setLayoutManager(searchLayoutManager);

        // Giả lập kết quả tìm kiếm (Dùng list ảnh khác hoặc giống cũng được)
        searchedAdapter = new WallpaperAdapter(getContext(), getDummyData(6), resourceId -> {
            showDetailDialog(resourceId);
        });
        rcvSearched.setAdapter(searchedAdapter);
    }

    private void showDetailDialog(int resourceId) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wallpaper_detail);

        // Cấu hình Window để Dialog full màn hình ngang, nền trong suốt
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Quan trọng: Set width là MATCH_PARENT để layout XML tự xử lý padding
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ImageView imgDetail = dialog.findViewById(R.id.imgDetail);
        imgDetail.setImageResource(resourceId);

        // Xử lý sự kiện click nút trong dialog (Optional)
        // dialog.findViewById(R.id.btnSave).setOnClickListener(...);

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