package com.zeal.studentguide.database.dao;

import androidx.room.*;

import com.zeal.studentguide.models.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserById(String userId);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE role = :role")
    List<User> getUsersByRole(String role);
}
