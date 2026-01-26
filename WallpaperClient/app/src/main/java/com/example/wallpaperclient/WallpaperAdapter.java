package com.example.wallpaperclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.wallpaperclient.database.WallpaperEntity;
import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder> {

    private Context mContext;
    private List<WallpaperEntity> mListPhoto;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(WallpaperEntity wallpaper);
    }

    public WallpaperAdapter(Context context, List<WallpaperEntity> listPhoto, OnItemClickListener listener) {
        this.mContext = context;
        this.mListPhoto = listPhoto;
        this.mListener = listener;
    }

    public void setData(List<WallpaperEntity> list) {
        this.mListPhoto = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WallpaperEntity wallpaper = mListPhoto.get(position);

        try {
            // Heuristic: Check if localPath is a resource ID (demo data) or a valid URI/URL
            int resId = Integer.parseInt(wallpaper.localPath);
            holder.imgWallpaper.setImageResource(resId);
        } catch (NumberFormatException e) {
            // Path is a String URI (File path or HTTP URL)
            Glide.with(mContext).load(wallpaper.localPath).into(holder.imgWallpaper);
        }

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(wallpaper);
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