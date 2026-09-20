package com.example.campuslostfound.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: Int,

    val title: String,

    val message: String,

    val itemId: Int,

    val isRead: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()
)