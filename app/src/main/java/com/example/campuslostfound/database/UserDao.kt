package com.example.campuslostfound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(
        user: UserEntity
    ): Long

    @Query(
        "SELECT * FROM users WHERE email = :email LIMIT 1"
    )
    suspend fun getUserByEmail(
        email: String
    ): UserEntity?

    @Query(
        "SELECT COUNT(*) FROM users WHERE email = :email"
    )
    suspend fun emailExists(
        email: String
    ): Int

    // Get owner information using user ID
    @Query(
        "SELECT * FROM users WHERE id = :userId LIMIT 1"
    )
    suspend fun getUserById(
        userId: Int
    ): UserEntity?
}