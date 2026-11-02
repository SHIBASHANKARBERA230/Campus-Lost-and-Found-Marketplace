package com.example.campuslostfound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao {

    @Insert
    suspend fun insertNotification(
        notification: NotificationEntity
    )

    @Query("""
        SELECT * FROM notifications
        WHERE userId = :userId
        ORDER BY createdAt DESC
    """)
    suspend fun getNotifications(
        userId: Int
    ): List<NotificationEntity>

    @Query("""
        SELECT COUNT(*) FROM notifications
        WHERE userId = :userId
        AND isRead = 0
    """)
    suspend fun getUnreadCount(
        userId: Int
    ): Int

    @Query("""
        UPDATE notifications
        SET isRead = 1
        WHERE id = :notificationId
    """)
    suspend fun markAsRead(
        notificationId: Int
    )

    @Query("""
        UPDATE notifications
        SET isRead = 1
        WHERE userId = :userId
    """)
    suspend fun markAllAsRead(
        userId: Int
    )

    @Query("""
        DELETE FROM notifications
        WHERE userId = :userId
    """)
    suspend fun deleteAllNotifications(
        userId: Int
    )

    @Query("""
        SELECT COUNT(*) FROM notifications
        WHERE userId = :userId
        AND itemId = :itemId
    """)
    suspend fun notificationExists(
        userId: Int,
        itemId: Int
    ): Int
}