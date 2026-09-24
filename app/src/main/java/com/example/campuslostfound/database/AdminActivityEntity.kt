package com.example.campuslostfound.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_activity")
data class AdminActivityEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val adminUserId: Int,

    val action: String,

    val targetUserId: Int? = null,

    val targetItemId: Int? = null,

    val details: String = "",

    val createdAt: Long = System.currentTimeMillis()
)