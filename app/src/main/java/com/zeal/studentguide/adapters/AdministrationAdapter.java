package com.zeal.studentguide.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.zeal.studentguide.R;
import com.zeal.studentguide.activities.AdmissionActivity;
import com.zeal.studentguide.activities.FinancialAidActivity;
import com.zeal.studentguide.activities.InternshipOpportunitiesActivity;
import com.zeal.studentguide.activities.ScholarshipApplicationActivity;
import com.zeal.studentguide.models.AdministrationItem;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdministrationAdapter extends RecyclerView.Adapter<AdministrationAdapter.ViewHolder> {
    private Context context;
    private List<AdministrationItem> items;

    public AdministrationAdapter(Context context, List<AdministrationItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_administration, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdministrationItem item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.icon.setImageResource(item.getIconResourceId());

        holder.cardView.setOnClickListener(v -> {
            Intent intent;
            switch (position) {
                case 0:
                    intent = new Intent(context, ScholarshipApplicationActivity.class);
                    break;
                case 1:
                    intent = new Intent(context, InternshipOpportunitiesActivity.class);
                    break;
                case 2:
                    intent = new Intent(context, AdmissionActivity.class);
                    break;
                case 3:
                    intent = new Intent(context, FinancialAidActivity.class);
                    break;
                default:
                    return;
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        MaterialCardView cardView;
        CircleImageView icon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtAdminTitle);
            description = itemView.findViewById(R.id.txtAdminDescription);
            cardView = itemView.findViewById(R.id.cardAdministration);
            icon = itemView.findViewById(R.id.imgAdminIcon);
        }
    }
}