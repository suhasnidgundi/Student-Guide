package com.zeal.studentguide.models;

public class CourseMaterial {
    private String id;
    private String name;
    private String type;
    private String url;
    private String section;

    public CourseMaterial(String id, String name, String type, String url, String section) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.section = section;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getUrl() { return url; }
    public String getSection() { return section; }
}