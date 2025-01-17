package com.zeal.studentguide.models;

public class VirtualTourLocation {
    private String id;
    private String title;
    private String thumbnailUrl;
    private String iframeUrl;
    private long timestamp;

    public VirtualTourLocation() {} // Required for Firebase

    public VirtualTourLocation(String title, String thumbnailUrl, String iframeUrl) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.iframeUrl = iframeUrl;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public String getIframeUrl() { return iframeUrl; }
    public void setIframeUrl(String iframeUrl) { this.iframeUrl = iframeUrl; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}