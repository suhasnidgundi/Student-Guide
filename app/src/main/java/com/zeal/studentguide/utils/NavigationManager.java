package com.zeal.studentguide.utils;

import androidx.fragment.app.Fragment;

import com.zeal.studentguide.R;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import java.util.LinkedHashMap;
import java.util.Map;

public class NavigationManager {
    private static final Map<String, Map<String, Class<? extends Fragment>>> ROLE_NAVIGATION = new LinkedHashMap<>();

    static {
        // Student Navigation
        Map<String, Class<? extends Fragment>> studentNav = new LinkedHashMap<>();
//        studentNav.put("Home", StudentHomeFragment.class);
//        studentNav.put("Courses", StudentCoursesFragment.class);
//        studentNav.put("Assignments", StudentAssignmentsFragment.class);
//        studentNav.put("Schedule", StudentScheduleFragment.class);
//        studentNav.put("Profile", StudentProfileFragment.class);
//        studentNav.put("Settings", SettingsFragment.class);
        ROLE_NAVIGATION.put("student", studentNav);

        // Faculty Navigation
        Map<String, Class<? extends Fragment>> facultyNav = new LinkedHashMap<>();
//        facultyNav.put("Dashboard", FacultyDashboardFragment.class);
//        facultyNav.put("Students", FacultyStudentsFragment.class);
//        facultyNav.put("Courses", FacultyCoursesFragment.class);
//        facultyNav.put("Attendance", FacultyAttendanceFragment.class);
//        facultyNav.put("Profile", FacultyProfileFragment.class);
//        facultyNav.put("Settings", SettingsFragment.class);
        ROLE_NAVIGATION.put("faculty", facultyNav);

        // Admin Navigation
        Map<String, Class<? extends Fragment>> adminNav = new LinkedHashMap<>();
//        adminNav.put("Dashboard", AdminDashboardFragment.class);
//        adminNav.put("Users", AdminUsersFragment.class);
//        adminNav.put("Departments", AdminDepartmentsFragment.class);
//        adminNav.put("Reports", AdminReportsFragment.class);
//        adminNav.put("Profile", AdminProfileFragment.class);
//        adminNav.put("Settings", SettingsFragment.class);
        ROLE_NAVIGATION.put("admin", adminNav);
    }

    public static Map<String, Class<? extends Fragment>> getNavigationForRole(String role) {
        return ROLE_NAVIGATION.getOrDefault(role.toLowerCase(), new LinkedHashMap<>());
    }

    public static Fragment getFragmentForNavItem(String role, String navItem) {
        try {
            Map<String, Class<? extends Fragment>> navMap = ROLE_NAVIGATION.get(role.toLowerCase());
            if (navMap != null && navMap.containsKey(navItem)) {
                return navMap.get(navItem).newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}