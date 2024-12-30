package com.zeal.studentguide.models;
public enum Departments {

    CSE("Computer Science and Engineering"),
    ECE("Electronics and Communication Engineering"),
    ME("Mechanical Engineering"),
    CE("Civil Engineering"),
    EE("Electrical Engineering"),
    CHE("Chemical Engineering"),
    MME("Metallurgical and Materials Engineering"),
    PE("Production Engineering"),
    MATH("Mathematics"),
    PHY("Physics"),
    CHEM("Chemistry"),
    HSS("Humanities and Social Sciences");

    private final String departmentName;

    Departments(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentName() {
        return departmentName;
    }
    
}