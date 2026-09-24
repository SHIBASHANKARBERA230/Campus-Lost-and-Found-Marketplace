package com.example.campuslostfound.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: Int,

    val name: String,

    val description: String,

    val category: String,

    val type: String,

    val location: String,

    val date: String,

    val status: String,

    val imageUri: String? = null
)