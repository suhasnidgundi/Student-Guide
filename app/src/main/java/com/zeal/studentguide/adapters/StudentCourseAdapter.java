package com.zeal.studentguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeal.studentguide.R;
import com.zeal.studentguide.models.Course;

import java.util.List;

public class StudentCourseAdapter extends RecyclerView.Adapter<StudentCourseAdapter.CourseViewHolder> {
    private List<Course> courses;
    private OnCourseClickListener listener;

    public StudentCourseAdapter(List<Course> courses, OnCourseClickListener listener) {
        this.courses = courses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        holder.bind(courses.get(position));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private TextView textCourseName;
        private TextView textCourseCode;
        private TextView textCredits;

        CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourseName = itemView.findViewById(R.id.textCourseName);
            textCourseCode = itemView.findViewById(R.id.textCourseCode);
            textCredits = itemView.findViewById(R.id.textCredits);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCourseClick(courses.get(position));
                }
            });
        }

        void bind(Course course) {
            textCourseName.setText(course.getCourseName());
            textCourseCode.setText(course.getCourseCode());
            textCredits.setText(String.format("%d Credits", course.getCredits()));
        }
    }

    interface OnCourseClickListener {
        void onCourseClick(Course course);
    }
}