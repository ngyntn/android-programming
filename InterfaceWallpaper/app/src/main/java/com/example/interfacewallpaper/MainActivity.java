package com.example.interfacewallpaper;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rvWallpapers = findViewById(R.id.rvWallpapers);

        // Setup Grid 3 cột giống ảnh
        rvWallpapers.setLayoutManager(new GridLayoutManager(this, 3));

        // Tạo dữ liệu giả (Link ảnh bóng rổ)
        List<String> imageUrls = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            // Link ảnh mẫu ngẫu nhiên từ Unsplash
            imageUrls.add("https://images.unsplash.com/photo-1546519638-68e109498ffc?w=500&auto=format&fit=crop&q=60");
        }

        // Gán Adapter
        WallpaperAdapter adapter = new WallpaperAdapter(imageUrls);
        rvWallpapers.setAdapter(adapter);
    }

    // Class Adapter viết gộp vào đây cho gọn (thường thì nên tách file)
    private class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder> {
        private List<String> urls;

        public WallpaperAdapter(List<String> urls) {
            this.urls = urls;
        }

        @NonNull
        @Override
        public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper, parent, false);
            return new WallpaperViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {
            // Dùng Glide load ảnh từ URL vào ImageView
            Glide.with(holder.itemView.getContext())
                    .load(urls.get(position))
                    .transform(new CenterCrop(), new RoundedCorners(30)) // Bo góc 30
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return urls.size();
        }

        class WallpaperViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            public WallpaperViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imgWallpaper);
            }
        }
    }
}