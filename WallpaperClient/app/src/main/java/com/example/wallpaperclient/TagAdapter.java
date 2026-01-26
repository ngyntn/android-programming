package com.example.wallpaperclient;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private Context context;
    private List<String> tags;
    private int selectedPosition = -1; // -1 indicates no selection
    private OnTagClickListener listener;

    public interface OnTagClickListener {
        void onTagClick(String tag);
    }

    public TagAdapter(Context context, List<String> tags, OnTagClickListener listener) {
        this.context = context;
        this.tags = tags;
        this.listener = listener;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        String tagName = tags.get(position);
        holder.tvTag.setText(tagName);

        // Highlight selected tag state
        if (selectedPosition == position) {
            holder.tvTag.setBackgroundResource(R.drawable.bg_tag_selected);
            holder.tvTag.setTextColor(Color.BLACK);
        } else {
            holder.tvTag.setBackgroundResource(R.drawable.bg_tag_default);
            holder.tvTag.setTextColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged(); // Refresh UI to reflect selection change
            listener.onTagClick(tagName);
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag;
        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tvTag);
        }
    }
}