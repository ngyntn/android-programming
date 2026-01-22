package com.example.aiwallpaperdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // --- CẤU HÌNH ---
    // Điền Access Key của Unsplash vào đây
    // Cấu trúc chuỗi header: "Client-ID <MÃ_KEY_CỦA_BẠN>"
    private static final String CLIENT_ID = "Client-ID 2-aHLzUiiJlxnckV_gXheSIjM9h1mAX4IUQLduwr8Pw";

    private ImageView ivWallpaper;
    private TextView tvAuthor;
    private Button btnNext;
    private ProgressBar progressBar;
    private UnsplashService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ View
        ivWallpaper = findViewById(R.id.ivWallpaper);
        tvAuthor = findViewById(R.id.tvAuthor);
        btnNext = findViewById(R.id.btnNext);
        progressBar = findViewById(R.id.progressBar);

        // 2. Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.unsplash.com/")
                .addConverterFactory(GsonConverterFactory.create()) // Dùng Gson parse JSON
                .build();

        apiService = retrofit.create(UnsplashService.class);

        // 3. Load ảnh lần đầu khi mở app
        fetchRandomPhoto();

        // 4. Sự kiện bấm nút
        btnNext.setOnClickListener(v -> fetchRandomPhoto());
    }

    private void fetchRandomPhoto() {
        progressBar.setVisibility(View.VISIBLE);
        btnNext.setEnabled(false); // Khóa nút để tránh spam request

        // Gọi API: Lấy ảnh dọc (portrait) cho đẹp điện thoại
        apiService.getRandomPhoto(CLIENT_ID, "portrait").enqueue(new Callback<UnsplashPhoto>() {
            @Override
            public void onResponse(Call<UnsplashPhoto> call, Response<UnsplashPhoto> response) {
                progressBar.setVisibility(View.GONE);
                btnNext.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    UnsplashPhoto photo = response.body();
                    displayPhoto(photo);
                } else {
                    // Lỗi thường gặp: 403 (Hết lượt request), 401 (Sai Key)
                    Toast.makeText(MainActivity.this, "Lỗi API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UnsplashPhoto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnNext.setEnabled(true);
                Toast.makeText(MainActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPhoto(UnsplashPhoto photo) {
        // 1. Hiển thị thông tin tác giả (Bắt buộc theo luật Unsplash)
        String credit = "Photo by " + photo.user.name + " on Unsplash";
        tvAuthor.setText(credit);

        // 2. Dùng Glide load ảnh từ URL
        // Chúng ta dùng urls.regular cho nhẹ, urls.full rất nặng
        Glide.with(this)
                .load(photo.urls.regular)
                .transition(DrawableTransitionOptions.withCrossFade()) // Hiệu ứng mờ dần
                .into(ivWallpaper);
    }
}