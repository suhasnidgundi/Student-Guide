package com.zeal.studentguide.models;

public enum UserRole {
    ADMIN,
    FACULTY,
    STUDENT,
    USER;

    public boolean canManipulate(UserRole otherRole) {
        return this.ordinal() < otherRole.ordinal();
    }
}