package com.example.campuslostfound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemDao {

    @Insert
    suspend fun insertItem(item: ItemEntity)

    @Update
    suspend fun updateItem(item: ItemEntity): Int

    // Get all items
    @Query("SELECT * FROM items ORDER BY id DESC")
    suspend fun getAllItems(): List<ItemEntity>

    // Get LOST items
    @Query("""
        SELECT * FROM items
        WHERE type = 'LOST'
        ORDER BY id DESC
    """)
    suspend fun getLostItems(): List<ItemEntity>

    // Get FOUND items
    @Query("""
        SELECT * FROM items
        WHERE type = 'FOUND'
        ORDER BY id DESC
    """)
    suspend fun getFoundItems(): List<ItemEntity>

    // Search items
    @Query("""
        SELECT * FROM items
        WHERE name LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        OR category LIKE '%' || :query || '%'
        OR location LIKE '%' || :query || '%'
        ORDER BY id DESC
    """)
    suspend fun searchItems(query: String): List<ItemEntity>

    // Get items by category
    @Query("""
        SELECT * FROM items
        WHERE category = :category
        ORDER BY id DESC
    """)
    suspend fun getItemsByCategory(category: String): List<ItemEntity>

    // Get items by type + category
    @Query("""
        SELECT * FROM items
        WHERE type = :type
        AND category = :category
        ORDER BY id DESC
    """)
    suspend fun getItemsByTypeAndCategory(
        type: String,
        category: String
    ): List<ItemEntity>

    // Get item owned by user
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

    // Delete item owned by user
    @Query("""
        DELETE FROM items
        WHERE id = :itemId
        AND userId = :userId
    """)
    suspend fun deleteItemByOwner(
        itemId: Int,
        userId: Int
    ): Int

    // Update item owned by user
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

    // Get current user's items
    @Query("""
        SELECT * FROM items
        WHERE userId = :userId
        ORDER BY id DESC
    """)
    suspend fun getItemsByUser(userId: Int): List<ItemEntity>

    // Get user by ID
    @Query("""
        SELECT * FROM users
        WHERE id = :userId
        LIMIT 1
    """)
    suspend fun getUserById(userId: Int): UserEntity?
}