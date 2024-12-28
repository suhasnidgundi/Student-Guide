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

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseViewHolder> {
    private final List<Course> courses;
    private final CourseClickListener courseClickListener;

    public CourseListAdapter(List<Course> courses, CourseClickListener listener) {
        this.courses = courses;
        this.courseClickListener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
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

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView textCourseName;
        private final TextView textCourseCode;
        private final TextView textCredits;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourseName = itemView.findViewById(R.id.textCourseName);
            textCourseCode = itemView.findViewById(R.id.textCourseCode);
            textCredits = itemView.findViewById(R.id.textCredits);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && courseClickListener != null) {
                    courseClickListener.onCourseClicked(courses.get(position));
                }
            });
        }

        public void bind(Course course) {
            textCourseName.setText(course.getCourseName());
            textCourseCode.setText(course.getCourseCode());
            textCredits.setText(String.format("%d credits", course.getCredits()));
        }
    }

    public interface CourseClickListener {
        void onCourseClicked(Course course);
    }
}