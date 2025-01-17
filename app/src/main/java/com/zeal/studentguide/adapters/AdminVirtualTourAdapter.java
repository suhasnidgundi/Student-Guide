package com.zeal.studentguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.zeal.studentguide.databinding.ItemVirtualTourLocationBinding;
import com.zeal.studentguide.models.VirtualTourLocation;
import java.util.ArrayList;
import java.util.List;

public class AdminVirtualTourAdapter extends RecyclerView.Adapter<AdminVirtualTourAdapter.LocationViewHolder> {
    private List<VirtualTourLocation> locations;
    private final OnLocationDeleteListener deleteListener;

    public interface OnLocationDeleteListener {
        void onDelete(VirtualTourLocation location);
    }

    public AdminVirtualTourAdapter(List<VirtualTourLocation> locations, OnLocationDeleteListener deleteListener) {
        this.locations = new ArrayList<>(locations);
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVirtualTourLocationBinding binding = ItemVirtualTourLocationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new LocationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        holder.bind(locations.get(position));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void setLocations(List<VirtualTourLocation> locations) {
        this.locations = new ArrayList<>(locations);
        notifyDataSetChanged();
    }

    public void addLocation(VirtualTourLocation location) {
        locations.add(location);
        notifyItemInserted(locations.size() - 1);
    }

    public void removeLocation(VirtualTourLocation location) {
        int position = locations.indexOf(location);
        if (position != -1) {
            locations.remove(position);
            notifyItemRemoved(position);
        }
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {
        private final ItemVirtualTourLocationBinding binding;

        LocationViewHolder(ItemVirtualTourLocationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(VirtualTourLocation location) {
            binding.textTitle.setText(location.getTitle());

            // Load thumbnail using Glide
            Glide.with(binding.imageThumbnail.getContext())
                    .load(location.getThumbnailUrl())
                    .centerCrop()
                    .into(binding.imageThumbnail);

            binding.btnDelete.setOnClickListener(v -> deleteListener.onDelete(location));

            // Hide the view tour button in admin view
            binding.btnViewTour.setVisibility(View.GONE);
        }
    }
}