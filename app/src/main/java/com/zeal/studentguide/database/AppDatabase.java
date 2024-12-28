package com.zeal.studentguide.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zeal.studentguide.models.*;
import com.zeal.studentguide.database.dao.*;

@Database(
        entities = {
                User.class,
                Student.class,
                Faculty.class,
                Course.class,
                Notification.class,
                CourseEnrollment.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "student_guide_db";

    // DAOs
    public abstract UserDao userDao();
    public abstract StudentDao studentDao();
    public abstract FacultyDao facultyDao();
    public abstract CourseDao courseDao();
    public abstract NotificationDao notificationDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}