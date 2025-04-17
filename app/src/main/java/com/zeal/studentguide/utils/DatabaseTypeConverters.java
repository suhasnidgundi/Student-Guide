package com.zeal.studentguide.utils;

import androidx.room.TypeConverter;

import com.zeal.studentguide.models.UserRole;
import com.zeal.studentguide.models.UserStatus;

import java.util.Date;

public class DatabaseTypeConverters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String fromUserStatus(UserStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static UserStatus toUserStatus(String status) {
        return status == null ? null : UserStatus.valueOf(status);
    }

    @TypeConverter
    public static String fromUserRole(UserRole role) {
        return role == null ? null : role.name();
    }

    @TypeConverter
    public static UserRole toUserRole(String role) {
        return role == null ? null : UserRole.valueOf(role);
    }
}