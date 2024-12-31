package com.zeal.studentguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zeal.studentguide.R;
import com.zeal.studentguide.databinding.ItemMediaBinding;

import java.util.List;

public class VirtualTourAdapter extends RecyclerView.Adapter<VirtualTourAdapter.MediaViewHolder> {
    private List<String> mediaUrls;
    private boolean isVideo;
    private OnItemClickListener listener;

    public VirtualTourAdapter(List<String> mediaUrls, boolean isVideo, OnItemClickListener listener) {
        this.mediaUrls = mediaUrls;
        this.isVideo = isVideo;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMediaBinding binding = ItemMediaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MediaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        String url = mediaUrls.get(position);
        if (isVideo) {
            holder.binding.playIcon.setVisibility(View.VISIBLE);
            // Load video thumbnail
            Glide.with(holder.itemView.getContext())
                    .asBitmap()
                    .load(url)
                    .into(holder.binding.mediaImage);
        } else {
            holder.binding.playIcon.setVisibility(View.GONE);
            // Load image
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .into(holder.binding.mediaImage);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(url, isVideo));
    }

    @Override
    public int getItemCount() {
        return mediaUrls.size();
    }

    public void updateMedia(List<String> newMediaUrls, boolean isVideo) {
        this.mediaUrls = newMediaUrls;
        this.isVideo = isVideo;
        notifyDataSetChanged();
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder {
        ItemMediaBinding binding;

        MediaViewHolder(ItemMediaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String url, boolean isVideo);
    }
}