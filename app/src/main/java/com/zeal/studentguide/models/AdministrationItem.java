package com.zeal.studentguide.models;

public class AdministrationItem {
    private String title;
    private String description;
    private int iconResourceId;

    public AdministrationItem(String title, String description, int iconResourceId) {
        this.title = title;
        this.description = description;
        this.iconResourceId = iconResourceId;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getIconResourceId() { return iconResourceId; }
}