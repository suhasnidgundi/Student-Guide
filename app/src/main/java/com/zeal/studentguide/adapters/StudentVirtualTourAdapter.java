package com.zeal.studentguide.adapters;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.zeal.studentguide.R;
import com.zeal.studentguide.databinding.ItemVirtualTourLocationBinding;
import com.zeal.studentguide.models.VirtualTourLocation;
import java.util.ArrayList;
import java.util.List;

public class StudentVirtualTourAdapter extends RecyclerView.Adapter<StudentVirtualTourAdapter.LocationViewHolder> {
    private List<VirtualTourLocation> locations;

    public StudentVirtualTourAdapter(List<VirtualTourLocation> locations) {
        this.locations = new ArrayList<>(locations);
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

            // Hide delete button in student view
            binding.btnDelete.setVisibility(View.GONE);

            binding.btnViewTour.setOnClickListener(v -> showMapDialog(location));
        }

        private void showMapDialog(VirtualTourLocation location) {
            Dialog dialog = new Dialog(binding.getRoot().getContext(), android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
            dialog.setContentView(R.layout.dialog_virtual_tour_map);

            WebView webView = dialog.findViewById(R.id.webViewMap);
            webView.setWebViewClient(new WebViewClient());

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);

            // Load the HTML with iframe
            String iframeHtml = "<html><body style='margin:0;padding:0;'>" +
                    "<iframe src='" + location.getIframeUrl() + "' " +
                    "width='100%' height='100%' frameborder='0' " +
                    "style='border:0;' allowfullscreen='' loading='lazy'>" +
                    "</iframe></body></html>";

            webView.loadDataWithBaseURL(null, iframeHtml, "text/html", "UTF-8", null);

            // Setup close button
            dialog.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        }
    }
}