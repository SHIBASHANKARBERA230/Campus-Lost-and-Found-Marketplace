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

    @Query(
        "SELECT * FROM users WHERE id = :userId LIMIT 1"
    )
    suspend fun getUserById(
        userId: Int
    ): UserEntity?

    // =============================================
    // PROFILE
    // =============================================

    @Query(
        """
        UPDATE users
        SET name = :name,
            phone = :phone
        WHERE id = :userId
        """
    )
    suspend fun updateUserProfile(
        userId: Int,
        name: String,
        phone: String
    )

    // =============================================
    // PASSWORD
    // =============================================

    @Query(
        """
        UPDATE users
        SET passwordHash = :passwordHash,
            passwordSalt = :passwordSalt
        WHERE id = :userId
        """
    )
    suspend fun updatePassword(
        userId: Int,
        passwordHash: String,
        passwordSalt: String
    )

    // =============================================
    // ADMIN CHECK
    // =============================================

    @Query(
        """
        SELECT * FROM users
        WHERE id = :userId
        AND isAdmin = 1
        LIMIT 1
        """
    )
    suspend fun getAdminById(
        userId: Int
    ): UserEntity?

    // =============================================
    // ADMIN USER MANAGEMENT
    // =============================================

    // Get all registered users
    @Query(
        """
        SELECT * FROM users
        ORDER BY id DESC
        """
    )
    suspend fun getAllUsers(): List<UserEntity>

    // Activate user
    @Query(
        """
        UPDATE users
        SET isActive = 1
        WHERE id = :userId
        """
    )
    suspend fun activateUser(
        userId: Int
    ): Int

    // Deactivate user
    @Query(
        """
        UPDATE users
        SET isActive = 0
        WHERE id = :userId
        """
    )
    suspend fun deactivateUser(
        userId: Int
    ): Int

    // Promote user to administrator
    @Query(
        """
        UPDATE users
        SET isAdmin = 1
        WHERE id = :userId
        """
    )
    suspend fun makeUserAdmin(
        userId: Int
    ): Int

    // Remove administrator privilege
    @Query(
        """
        UPDATE users
        SET isAdmin = 0
        WHERE id = :userId
        """
    )
    suspend fun removeAdmin(
        userId: Int
    ): Int
}