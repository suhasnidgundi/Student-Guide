package com.zeal.studentguide.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.PropertyName;

@Entity(tableName = "course_contents",
        foreignKeys = @ForeignKey(
                entity = Course.class,
                parentColumns = "courseId",
                childColumns = "courseId",
                onDelete = ForeignKey.CASCADE
        ))
public class CourseContent {
    @PrimaryKey
    @NonNull
    private String contentId;

    private String courseId;
    private String title;
    private String type; // syllabus, notes, question_paper, project_report
    private String fileUrl;
    private String uploadDate;
    private long fileSize;

    @PropertyName("download_count")
    private int downloadCount;

    public CourseContent() {
        // Required empty constructor for Firestore
    }

    public CourseContent(@NonNull String contentId, String courseId, String title, String type) {
        this.contentId = contentId;
        this.courseId = courseId;
        this.title = title;
        this.type = type;
    }

    @NonNull
    public String getContentId() { return contentId; }
    public void setContentId(@NonNull String contentId) { this.contentId = contentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getUploadDate() { return uploadDate; }
    public void setUploadDate(String uploadDate) { this.uploadDate = uploadDate; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    @PropertyName("download_count")
    public int getDownloadCount() { return downloadCount; }

    @PropertyName("download_count")
    public void setDownloadCount(int downloadCount) { this.downloadCount = downloadCount; }
}