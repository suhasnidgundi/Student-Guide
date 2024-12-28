// CourseAdapter.java
package com.zeal.studentguide.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.zeal.studentguide.databinding.ItemCourseBinding;
import com.zeal.studentguide.models.Course;

public class CourseAdapter extends ListAdapter<Course, CourseAdapter.CourseViewHolder> {
    private final OnCourseClickListener editListener;
    private final OnCourseClickListener deleteListener;

    public CourseAdapter(OnCourseClickListener editListener, OnCourseClickListener deleteListener) {
        super(new CourseDiffCallback());
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCourseBinding binding = ItemCourseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private final ItemCourseBinding binding;

        CourseViewHolder(ItemCourseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Course course) {
            binding.textCourseName.setText(course.getCourseName());
            binding.textCourseCode.setText(course.getCourseCode());
            binding.textCredits.setText(String.valueOf(course.getCredits()) + " Credits");
            binding.textSemester.setText(course.getSemester());

            binding.buttonEdit.setOnClickListener(v -> editListener.onCourseClick(course));
            binding.buttonDelete.setOnClickListener(v -> deleteListener.onCourseClick(course));
        }
    }

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    private static class CourseDiffCallback extends DiffUtil.ItemCallback<Course> {
        @Override
        public boolean areItemsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getCourseId().equals(newItem.getCourseId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getCourseName().equals(newItem.getCourseName()) &&
                    oldItem.getCourseCode().equals(newItem.getCourseCode()) &&
                    oldItem.getCredits() == newItem.getCredits() &&
                    oldItem.getSemester().equals(newItem.getSemester());
        }
    }
}