package com.example.campuslostfound.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val itemId: Int,

    val reporterUserId: Int,

    val ownerUserId: Int,

    val reason: String,

    val details: String,

    val status: String = "PENDING",

    val createdAt: Long = System.currentTimeMillis()
)