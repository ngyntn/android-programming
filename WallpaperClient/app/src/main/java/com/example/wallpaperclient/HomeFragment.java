package com.example.wallpaperclient;

import android.app.Dialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wallpaperclient.api.ApiClient;
import com.example.wallpaperclient.api.ApiService;
import com.example.wallpaperclient.api.WallpaperResponse;
import com.example.wallpaperclient.database.WallpaperEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rcvPopular, rcvSearched, rcvTags;
    private TextView tvSearchedTitle, tvPopularTitle;
    private EditText edtSearch;

    private WallpaperAdapter popularAdapter;
    private WallpaperAdapter searchedAdapter;
    private TagAdapter tagAdapter;

    private List<WallpaperEntity> popularList = new ArrayList<>();
    private List<WallpaperEntity> searchedList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvPopular = view.findViewById(R.id.rcvWallpapers);
        rcvSearched = view.findViewById(R.id.rcvSearched);
        rcvTags = view.findViewById(R.id.rcvTags);
        tvSearchedTitle = view.findViewById(R.id.tvSearchedTitle);
        // Map layout ID to class field
        tvPopularTitle = view.findViewById(R.id.tvTitle);
        edtSearch = view.findViewById(R.id.edtSearch);

        setupPopularList();
        setupSearchedList();
        setupTagList();
        setupSearchAction();

        // Load default content
        fetchPopularWallpapers("Popular");
    }

    private void setupPopularList() {
        rcvPopular.setLayoutManager(new GridLayoutManager(getContext(), 3));
        popularAdapter = new WallpaperAdapter(getContext(), popularList, this::showDetailDialog);
        rcvPopular.setAdapter(popularAdapter);
    }

    private void setupSearchedList() {
        rcvSearched.setLayoutManager(new GridLayoutManager(getContext(), 3));
        searchedAdapter = new WallpaperAdapter(getContext(), searchedList, this::showDetailDialog);
        rcvSearched.setAdapter(searchedAdapter);
    }

    private void setupTagList() {
        List<String> tags = Arrays.asList("Popular", "Nature", "Abstract", "Cars", "Space", "Minimal", "Cyberpunk", "Neon", "Anime");

        tagAdapter = new TagAdapter(getContext(), tags, tagName -> {
            if (tagName.equals("Popular")) {
                tvPopularTitle.setText("Popular");
                fetchPopularWallpapers("Popular");
            } else {
                tvPopularTitle.setText(tagName + " Wallpapers");
                fetchPopularWallpapers(tagName);
            }
        });

        // Highlight the first tag by default
        tagAdapter.setSelectedPosition(0);

        rcvTags.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvTags.setAdapter(tagAdapter);
    }

    private void setupSearchAction() {
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(edtSearch.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void fetchPopularWallpapers(String tag) {
        ApiService apiService = ApiClient.getApiService();
        Call<List<WallpaperResponse>> call = apiService.getRandomWallpapers(tag);

        call.enqueue(new Callback<List<WallpaperResponse>>() {
            @Override
            public void onResponse(Call<List<WallpaperResponse>> call, Response<List<WallpaperResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WallpaperResponse> apiData = response.body();

                    popularList.clear();

                    // Map API DTOs to local Entity model
                    for (WallpaperResponse item : apiData) {
                        WallpaperEntity entity = new WallpaperEntity(
                                item.getFullUrl(),
                                "UNSPLASH",
                                System.currentTimeMillis()
                        );
                        entity.remoteId = item.getId();
                        popularList.add(entity);
                    }

                    popularAdapter.setData(popularList);
                }
            }

            @Override
            public void onFailure(Call<List<WallpaperResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) return;

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

        tvSearchedTitle.setVisibility(View.VISIBLE);
        rcvSearched.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getApiService();
        Call<List<WallpaperResponse>> call = apiService.searchWallpapers(query, 1);

        call.enqueue(new Callback<List<WallpaperResponse>>() {
            @Override
            public void onResponse(Call<List<WallpaperResponse>> call, Response<List<WallpaperResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WallpaperResponse> apiData = response.body();

                    searchedList.clear();
                    for (WallpaperResponse item : apiData) {
                        WallpaperEntity entity = new WallpaperEntity(
                                item.getFullUrl(),
                                "UNSPLASH",
                                System.currentTimeMillis()
                        );
                        entity.remoteId = item.getId();
                        searchedList.add(entity);
                    }
                    searchedAdapter.setData(searchedList);

                    if (searchedList.isEmpty()) {
                        Toast.makeText(getContext(), "No results found for '" + query + "'", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<WallpaperResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Search failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDetailDialog(WallpaperEntity wallpaper) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wallpaper_detail);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ImageView imgDetail = dialog.findViewById(R.id.imgDetail);
        AppCompatButton btnSave = dialog.findViewById(R.id.btnSave);
        AppCompatButton btnSetWallpaper = dialog.findViewById(R.id.btnSetWallpaper);

        // Heuristic: Check if localPath is a resource ID (demo) or a URL
        try {
            int resId = Integer.parseInt(wallpaper.localPath);
            imgDetail.setImageResource(resId);
        } catch (NumberFormatException e) {
            Glide.with(this).load(wallpaper.localPath).into(imgDetail);
        }

        btnSave.setOnClickListener(v -> {
            WallpaperUtils.saveWallpaper(getContext(), wallpaper);
            dialog.dismiss();
        });

        btnSetWallpaper.setOnClickListener(v -> {
            WallpaperUtils.setWallpaper(getContext(), wallpaper);
        });

        dialog.show();
    }
}