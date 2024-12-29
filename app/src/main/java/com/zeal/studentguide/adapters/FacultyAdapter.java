package com.zeal.studentguide.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.zeal.studentguide.databinding.ItemFacultyBinding;
import com.zeal.studentguide.models.FacultyWithUser;
import java.util.List;

public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder> {
    private List<FacultyWithUser> faculties;
    private final OnFacultyClickListener listener;

    public interface OnFacultyClickListener {
        void onFacultyClick(FacultyWithUser faculty);
    }

    public FacultyAdapter(List<FacultyWithUser> faculties, OnFacultyClickListener listener) {
        this.faculties = faculties;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFacultyBinding binding = ItemFacultyBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FacultyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyViewHolder holder, int position) {
        holder.bind(faculties.get(position));
    }

    @Override
    public int getItemCount() {
        return faculties.size();
    }

    public void updateFaculties(List<FacultyWithUser> newFaculties) {
        this.faculties = newFaculties;
        notifyDataSetChanged();
    }

    class FacultyViewHolder extends RecyclerView.ViewHolder {
        private final ItemFacultyBinding binding;

        FacultyViewHolder(ItemFacultyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FacultyWithUser facultyWithUser) {
            binding.textName.setText(facultyWithUser.getName());
            binding.textDesignation.setText(facultyWithUser.getDesignation());
            binding.textDepartment.setText(facultyWithUser.getDepartment());
            binding.getRoot().setOnClickListener(v -> listener.onFacultyClick(facultyWithUser));
        }
    }
}