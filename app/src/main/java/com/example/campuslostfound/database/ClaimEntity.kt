package com.example.campuslostfound.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "claims")
data class ClaimEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val itemId: Int,

    val claimantUserId: Int,

    val ownerUserId: Int,

    val reason: String,

    val additionalDetails: String,

    val status: String = "PENDING",

    val createdAt: Long = System.currentTimeMillis()
)