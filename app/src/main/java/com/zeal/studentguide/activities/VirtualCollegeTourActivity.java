package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.zeal.studentguide.adapters.StudentVirtualTourAdapter;
import com.zeal.studentguide.databinding.ActivityCollegeVirtualTourBinding;
import com.zeal.studentguide.models.VirtualTourLocation;

import java.util.ArrayList;
import java.util.List;

public class VirtualCollegeTourActivity extends AppCompatActivity {
    private ActivityCollegeVirtualTourBinding binding;
    private StudentVirtualTourAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollegeVirtualTourBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        loadVirtualTourLocations();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarVirtualTour);
        binding.toolbarVirtualTour.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new StudentVirtualTourAdapter(new ArrayList<>());
        // Changed to LinearLayoutManager for vertical scrolling list
        binding.recyclerViewLocations.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewLocations.setAdapter(adapter);
    }

    private void loadVirtualTourLocations() {
        binding.progressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();

        db.collection("virtual_tour_locations")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<VirtualTourLocation> locations = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        VirtualTourLocation location = document.toObject(VirtualTourLocation.class);
                        location.setId(document.getId());
                        locations.add(location);
                    }
                    adapter.setLocations(locations);
                    binding.progressBar.setVisibility(View.GONE);

                    // Show empty state if no locations are available
                    if (locations.isEmpty()) {
                        binding.layoutEmpty.setVisibility(View.VISIBLE);
                        binding.recyclerViewLocations.setVisibility(View.GONE);
                    } else {
                        binding.layoutEmpty.setVisibility(View.GONE);
                        binding.recyclerViewLocations.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading virtual tour locations: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}