package com.example.wallpaperclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder> {

    private Context mContext;
    private List<Integer> mListPhoto;
    private OnItemClickListener mListener; // 1. Khai báo listener

    // 2. Tạo interface
    public interface OnItemClickListener {
        void onItemClick(int resourceId);
    }

    // 3. Cập nhật Constructor
    public WallpaperAdapter(Context context, List<Integer> listPhoto, OnItemClickListener listener) {
        this.mContext = context;
        this.mListPhoto = listPhoto;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int resourceId = mListPhoto.get(position);
        holder.imgWallpaper.setImageResource(resourceId);

        // 4. Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(resourceId);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListPhoto != null) return mListPhoto.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgWallpaper;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgWallpaper = itemView.findViewById(R.id.imgWallpaper);
        }
    }
}