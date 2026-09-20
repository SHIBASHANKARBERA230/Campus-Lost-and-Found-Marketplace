package com.example.campuslostfound.model

data class Item(

    val id: Int = 0,

    val userId: Int = 0,

    val name: String,

    val description: String,

    val category: String,

    val type: String,

    val location: String,

    val date: String,

    val status: String = "LOST",

    val imageUri: String? = null
)