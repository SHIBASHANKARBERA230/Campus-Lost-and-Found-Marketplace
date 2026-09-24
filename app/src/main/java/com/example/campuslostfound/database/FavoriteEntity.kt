package com.example.campuslostfound.database

import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "itemId"]
)
data class FavoriteEntity(
    val userId: Int,
    val itemId: Int,
    val createdAt: Long = System.currentTimeMillis()
)