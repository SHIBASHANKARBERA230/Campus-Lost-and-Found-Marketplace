package com.example.campuslostfound.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemDao {

    @Insert
    suspend fun insertItem(item: ItemEntity)

    @Update
    suspend fun updateItem(item: ItemEntity): Int

    @Query("SELECT * FROM items ORDER BY id DESC")
    suspend fun getAllItems(): List<ItemEntity>

    @Query("SELECT * FROM items WHERE type = 'LOST' ORDER BY id DESC")
    suspend fun getLostItems(): List<ItemEntity>

    @Query("SELECT * FROM items WHERE type = 'FOUND' ORDER BY id DESC")
    suspend fun getFoundItems(): List<ItemEntity>

    @Query("""
        SELECT * FROM items
        WHERE name LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        OR category LIKE '%' || :query || '%'
        OR location LIKE '%' || :query || '%'
        ORDER BY id DESC
    """)
    suspend fun searchItems(query: String): List<ItemEntity>

    @Query("""
        SELECT * FROM items
        WHERE id = :itemId
        AND userId = :userId
        LIMIT 1
    """)
    suspend fun getItemByOwner(
        itemId: Int,
        userId: Int
    ): ItemEntity?

    @Query("""
        DELETE FROM items
        WHERE id = :itemId
        AND userId = :userId
    """)
    suspend fun deleteItemByOwner(
        itemId: Int,
        userId: Int
    ): Int

    @Query("""
        UPDATE items
        SET name = :name,
            description = :description,
            category = :category,
            location = :location,
            date = :date
        WHERE id = :itemId
        AND userId = :userId
    """)
    suspend fun updateItemByOwner(
        itemId: Int,
        userId: Int,
        name: String,
        description: String,
        category: String,
        location: String,
        date: String
    ): Int
}