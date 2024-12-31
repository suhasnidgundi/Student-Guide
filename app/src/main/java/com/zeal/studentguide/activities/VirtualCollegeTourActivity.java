package com.zeal.studentguide.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zeal.studentguide.adapters.VirtualTourAdapter;
import com.zeal.studentguide.databinding.ActivityCollegeVirtualTourBinding;

import java.util.ArrayList;
import java.util.List;

public class VirtualCollegeTourActivity extends AppCompatActivity {
    private ActivityCollegeVirtualTourBinding binding;
    private VirtualTourAdapter adapter;
    private FirebaseStorage storage;
    private boolean isShowingVideos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollegeVirtualTourBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarVirtualTour.setNavigationOnClickListener(v -> onBackPressed());
        binding.btnImages.setChecked(true);  // Set Images button checked by default
        setupRecyclerView();
        setupToggleButtons();

        storage = FirebaseStorage.getInstance();
        isShowingVideos = false;  // Default to images
        loadMedia();  // Will load images by default
    }

    private void setupRecyclerView() {
        adapter = new VirtualTourAdapter(new ArrayList<>(), false, this::handleMediaClick);
        binding.mediaRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.mediaRecyclerView.setAdapter(adapter);
    }

    private void setupToggleButtons() {
        binding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                isShowingVideos = checkedId == binding.btnVideos.getId();
                loadMedia();
            }
        });
    }

    private void loadMedia() {
        String folderPath = "virtual_tour/" + (isShowingVideos ? "videos" : "images");
        StorageReference folderRef = storage.getReference().child(folderPath);

        folderRef.listAll()
                .addOnSuccessListener(listResult -> {
                    List<String> urls = new ArrayList<>();
                    int totalItems = listResult.getItems().size();
                    if (totalItems == 0) {
                        adapter.updateMedia(urls, isShowingVideos);
                        return;
                    }

                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            urls.add(uri.toString());
                            if (urls.size() == totalItems) {
                                adapter.updateMedia(urls, isShowingVideos);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> showToast("Error loading media: " + e.getMessage()));
    }

    private void handleMediaClick(String url, boolean isVideo) {
        if (isVideo) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "video/*");
            startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "image/*");
            startActivity(intent);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}