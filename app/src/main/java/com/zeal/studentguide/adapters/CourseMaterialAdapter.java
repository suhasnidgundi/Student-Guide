package com.zeal.studentguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeal.studentguide.R;
import com.zeal.studentguide.models.CourseMaterial;

import java.util.List;

public class CourseMaterialAdapter extends RecyclerView.Adapter<CourseMaterialAdapter.MaterialViewHolder> {
    private List<CourseMaterial> materials;
    private OnMaterialClickListener listener;

    public CourseMaterialAdapter(List<CourseMaterial> materials, OnMaterialClickListener listener) {
        this.materials = materials;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_material, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        holder.bind(materials.get(position));
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    class MaterialViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageDocumentType;
        private TextView textDocumentName;

        MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            imageDocumentType = itemView.findViewById(R.id.imageDocumentType);
            textDocumentName = itemView.findViewById(R.id.textDocumentName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMaterialClick(materials.get(position));
                }
            });
        }

        void bind(CourseMaterial material) {
            textDocumentName.setText(material.getName());

            // Set icon based on document type
            int iconResId;
            switch (material.getType().toLowerCase()) {
                case "pdf":
                    iconResId = R.drawable.ic_pdf;
                    break;
                case "doc":
                case "docx":
                    iconResId = R.drawable.ic_word;
                    break;
                default:
                    iconResId = R.drawable.ic_document;
            }
            imageDocumentType.setImageResource(iconResId);
        }
    }

    public interface OnMaterialClickListener {
        void onMaterialClick(CourseMaterial material);
    }
}