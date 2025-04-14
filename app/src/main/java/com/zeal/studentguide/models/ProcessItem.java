package com.zeal.studentguide.models;

import java.util.ArrayList;

public class ProcessItem {
    private String title;
    private String description;
    private ArrayList<String[]> steps;

    public ProcessItem(String title, String description, ArrayList<String[]> steps) {
        this.title = title;
        this.description = description;
        this.steps = steps;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String[]> getSteps() {
        return steps;
    }
}