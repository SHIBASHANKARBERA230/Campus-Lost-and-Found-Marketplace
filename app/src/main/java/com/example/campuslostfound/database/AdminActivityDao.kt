package com.example.campuslostfound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AdminActivityDao {

    @Insert
    suspend fun insertActivity(
        activity: AdminActivityEntity
    )

    @Query("""
        SELECT *
        FROM admin_activity
        ORDER BY createdAt DESC
    """)
    suspend fun getAllActivities(): List<AdminActivityEntity>

    @Query("""
        SELECT *
        FROM admin_activity
        WHERE adminUserId = :adminUserId
        ORDER BY createdAt DESC
    """)
    suspend fun getActivitiesByAdmin(
        adminUserId: Int
    ): List<AdminActivityEntity>

    @Query("""
        SELECT *
        FROM admin_activity
        WHERE targetUserId = :userId
        ORDER BY createdAt DESC
    """)
    suspend fun getActivitiesForUser(
        userId: Int
    ): List<AdminActivityEntity>

    @Query("""
        SELECT *
        FROM admin_activity
        WHERE targetItemId = :itemId
        ORDER BY createdAt DESC
    """)
    suspend fun getActivitiesForItem(
        itemId: Int
    ): List<AdminActivityEntity>

    @Query("""
        SELECT COUNT(*)
        FROM admin_activity
    """)
    suspend fun getActivityCount(): Int

    @Query("""
        DELETE FROM admin_activity
    """)
    suspend fun deleteAllActivities()
}