package com.zeal.studentguide.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "StudentGuidePrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_USER_ID = "userId";

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

    public void setUserRole(String role) {
        preferences.edit().putString(KEY_USER_ROLE, role).apply();
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

    public void clearPreferences() {
        preferences.edit().clear().apply();
    }
}