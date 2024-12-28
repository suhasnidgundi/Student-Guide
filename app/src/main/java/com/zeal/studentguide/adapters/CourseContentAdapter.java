package com.zeal.studentguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeal.studentguide.R;
import com.zeal.studentguide.models.CourseContent;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CourseContentAdapter extends RecyclerView.Adapter<CourseContentAdapter.ContentViewHolder> {
    private List<CourseContent> courseContents;
    private OnContentClickListener listener;

    public CourseContentAdapter(List<CourseContent> courseContents) {
        this.courseContents = courseContents;
    }

    public void setOnContentClickListener(OnContentClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_content, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        CourseContent content = courseContents.get(position);
        holder.bind(content);
    }

    @Override
    public int getItemCount() {
        return courseContents.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle;
        private TextView textType;
        private TextView textDate;
        private TextView textInfo;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textType = itemView.findViewById(R.id.textType);
            textDate = itemView.findViewById(R.id.textDate);
            textInfo = itemView.findViewById(R.id.textInfo);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onContentClick(courseContents.get(position));
                }
            });
        }

        public void bind(CourseContent content) {
            textTitle.setText(content.getTitle());
            textType.setText(formatContentType(content.getType()));

            // Format and set the upload date
            if (content.getUploadDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                textDate.setText(content.getUploadDate());
            } else {
                textDate.setVisibility(View.GONE);
            }

            // Combine file size and download count into info text
            String info = String.format("%s • %d downloads",
                    formatFileSize(content.getFileSize()),
                    content.getDownloadCount());
            textInfo.setText(info);
        }

        private String formatContentType(String type) {
            if (type == null) return "";
            // Convert snake_case to Title Case
            String[] words = type.split("_");
            StringBuilder formatted = new StringBuilder();
            for (String word : words) {
                if (word.length() > 0) {
                    formatted.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1).toLowerCase())
                            .append(" ");
                }
            }
            return formatted.toString().trim();
        }

        private String formatFileSize(long size) {
            if (size <= 0) return "0 B";
            final String[] units = new String[]{"B", "KB", "MB", "GB"};
            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
            return String.format("%.1f %s",
                    size / Math.pow(1024, digitGroups),
                    units[digitGroups]);
        }
    }

    public interface OnContentClickListener {
        void onContentClick(CourseContent content);
    }
}