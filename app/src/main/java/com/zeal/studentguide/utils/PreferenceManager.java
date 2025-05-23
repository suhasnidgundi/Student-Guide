package com.zeal.studentguide.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.zeal.studentguide.models.UserRole;

public class PreferenceManager {
    private static final String PREF_NAME = "StudentGuidePrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_EMAIL = "userEmail";

    private static final String KEY_USER_DEPARTMENT = "userDepartment";
    private static final String KEY_USER_ACTIVE = "userActive";
    private static final String KEY_USER_PROFILE_COMPLETE = "userProfileComplete";

    private SharedPreferences preferences;

    public PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLoggedIn(boolean loggedIn) {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setUserRole(UserRole role) {
        preferences.edit().putString(KEY_USER_ROLE, String.valueOf(role)).apply();
    }

    public String getUserRole() {
        return preferences.getString(KEY_USER_ROLE, "");
    }

    public void setUserId(String userId) {
        preferences.edit().putString(KEY_USER_ID, userId).apply();
    }

    public String getUserId() {
        return preferences.getString(KEY_USER_ID, "");
    }

    public void setUsername(String username) {
        preferences.edit().putString(KEY_USERNAME, username).apply();
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    public void setUserEmail(String email) {
        preferences.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, "");
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

    public void setUserDepartment(String department) {
        preferences.edit().putString(KEY_USER_DEPARTMENT, department).apply();
    }

    public String getUserDepartment() {
        return preferences.getString(KEY_USER_DEPARTMENT, "");
    }

    public void setUserActive(boolean isActive) {
        preferences.edit().putBoolean(KEY_USER_ACTIVE, isActive).apply();
    }

    public boolean isUserActive() {
        return preferences.getBoolean(KEY_USER_ACTIVE, false);
    }

    public void setUserProfileComplete(boolean isProfileComplete) {
        preferences.edit().putBoolean(KEY_USER_PROFILE_COMPLETE, isProfileComplete).apply();
    }

    public boolean isUserProfileComplete() {
        return preferences.getBoolean(KEY_USER_PROFILE_COMPLETE, false);
    }


}