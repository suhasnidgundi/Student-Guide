package com.zeal.studentguide.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeal.studentguide.R;
import com.zeal.studentguide.models.Announcement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {

    private List<Announcement> announcements;
    private Context context;
    private OnAnnouncementActionListener listener;

    public interface OnAnnouncementActionListener {
        void onDeleteAnnouncement(Announcement announcement, int position);
    }

    public AnnouncementAdapter(Context context, OnAnnouncementActionListener listener) {
        this.context = context;
        this.announcements = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);

        holder.textAnnouncementType.setText(announcement.getType());
        holder.textDepartment.setText(announcement.getDepartment());
        holder.textAnnouncementMessage.setText(announcement.getMessage());

        // Format timestamp
        if (announcement.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());
            Date date = announcement.getTimestamp().toDate();
            holder.textTimestamp.setText(sdf.format(date));
        } else {
            holder.textTimestamp.setText("Just now");
        }

        // Set delete button click listener
        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteAnnouncement(announcement, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
        notifyDataSetChanged();
    }

    public void addAnnouncement(Announcement announcement) {
        this.announcements.add(0, announcement);
        notifyItemInserted(0);
    }

    public void removeAnnouncement(int position) {
        if (position >= 0 && position < announcements.size()) {
            announcements.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView textAnnouncementType, textDepartment, textAnnouncementMessage, textTimestamp;
        ImageView buttonDelete;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            textAnnouncementType = itemView.findViewById(R.id.textAnnouncementType);
            textDepartment = itemView.findViewById(R.id.textDepartment);
            textAnnouncementMessage = itemView.findViewById(R.id.textAnnouncementMessage);
            textTimestamp = itemView.findViewById(R.id.textTimestamp);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}