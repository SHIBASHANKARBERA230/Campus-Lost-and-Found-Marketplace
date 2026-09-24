package com.example.campuslostfound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemDao {

    // =========================================
    // INSERT ITEM
    // =========================================

    @Insert
    suspend fun insertItem(
        item: ItemEntity
    ): Long

    // =========================================
    // UPDATE ITEM
    // =========================================

    @Update
    suspend fun updateItem(
        item: ItemEntity
    ): Int

    // =========================================
    // GET ITEM BY ID
    // =========================================

    @Query(
        """
        SELECT * FROM items
        WHERE id = :itemId
        LIMIT 1
        """
    )
    suspend fun getItemById(
        itemId: Int
    ): ItemEntity?

    // =========================================
    // GET ALL ITEMS
    // =========================================

    @Query(
        """
        SELECT * FROM items
        ORDER BY id DESC
        """
    )
    suspend fun getAllItems(): List<ItemEntity>

    // =========================================
    // GET LOST ITEMS
    // =========================================

    @Query(
        """
        SELECT * FROM items
        WHERE type = 'LOST'
        ORDER BY id DESC
        """
    )
    suspend fun getLostItems(): List<ItemEntity>

    // =========================================
    // GET FOUND ITEMS
    // =========================================

    @Query(
        """
        SELECT * FROM items
        WHERE type = 'FOUND'
        ORDER BY id DESC
        """
    )
    suspend fun getFoundItems(): List<ItemEntity>

    // =========================================
    // SEARCH ITEMS
    // =========================================

    @Query(
        """
        SELECT * FROM items
        WHERE name LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        OR category LIKE '%' || :query || '%'
        OR location LIKE '%' || :query || '%'
        ORDER BY id DESC
        """
    )
    suspend fun searchItems(
        query: String
    ): List<ItemEntity>

    // =========================================
    // GET ITEMS BY CATEGORY
    // =========================================

    @Query(
        """
        SELECT * FROM items
        WHERE category = :category
        ORDER BY id DESC
        """
    )
    suspend fun getItemsByCategory(
        category: String
    ): List<ItemEntity>

    // =========================================
    // GET ITEMS BY TYPE AND CATEGORY
    // =========================================

    @Query(
        """
        SELECT * FROM items
        WHERE type = :type
        AND category = :category
        ORDER BY id DESC
        """
    )
    suspend fun getItemsByTypeAndCategory(
        type: String,
        category: String
    ): List<ItemEntity>

    // =========================================
    // GET ITEM BY OWNER
    // =========================================

    @Query(
        """
        SELECT * FROM items
        WHERE id = :itemId
        AND userId = :userId
        LIMIT 1
        """
    )
    suspend fun getItemByOwner(
        itemId: Int,
        userId: Int
    ): ItemEntity?

    // =========================================
    // DELETE ITEM BY OWNER
    // =========================================

    @Query(
        """
        DELETE FROM items
        WHERE id = :itemId
        AND userId = :userId
        """
    )
    suspend fun deleteItemByOwner(
        itemId: Int,
        userId: Int
    ): Int

    // =========================================
    // DELETE ITEM BY ID
    // Admin moderation
    // =========================================

    @Query(
        """
        DELETE FROM items
        WHERE id = :itemId
        """
    )
    suspend fun deleteItemById(
        itemId: Int
    ): Int

    // =========================================
    // UPDATE ITEM BY OWNER
    // =========================================

    @Query(
        """
        UPDATE items
        SET name = :name,
            description = :description,
            category = :category,
            location = :location,
            date = :date
        WHERE id = :itemId
        AND userId = :userId
        """
    )
    suspend fun updateItemByOwner(
        itemId: Int,
        userId: Int,
        name: String,
        description: String,
        category: String,
        location: String,
        date: String
    ): Int

    // =========================================
    // UPDATE ITEM STATUS BY OWNER
    // =========================================

    @Query(
        """
        UPDATE items
        SET status = :status
        WHERE id = :itemId
        AND userId = :userId
        """
    )
    suspend fun updateItemStatusByOwner(
        itemId: Int,
        userId: Int,
        status: String
    ): Int

    // =========================================
    // GET ITEMS BY USER
    // =========================================

    @Query(
        """
        SELECT * FROM items
        WHERE userId = :userId
        ORDER BY id DESC
        """
    )
    suspend fun getItemsByUser(
        userId: Int
    ): List<ItemEntity>

    // =========================================
    // FIND POSSIBLE LOST / FOUND MATCHES
    // =========================================

    @Query(
        """
        SELECT * FROM items
        WHERE type = :oppositeType
        AND category = :category
        AND userId != :userId
        AND (
            name LIKE '%' || :name || '%'
            OR :name LIKE '%' || name || '%'
        )
        ORDER BY id DESC
        """
    )
    suspend fun findMatchingItems(
        oppositeType: String,
        category: String,
        name: String,
        userId: Int
    ): List<ItemEntity>

    // =========================================
    // GET RECENT ITEMS
    // =========================================

    @Query(
        """
        SELECT * FROM items
        ORDER BY id DESC
        LIMIT 5
        """
    )
    suspend fun getRecentItems(): List<ItemEntity>

    // =========================================
    // ADMIN DASHBOARD COUNTS
    // =========================================

    @Query(
        "SELECT COUNT(*) FROM items"
    )
    suspend fun getTotalItems(): Int

    @Query(
        """
        SELECT COUNT(*) FROM items
        WHERE type = 'LOST'
        """
    )
    suspend fun getTotalLostItems(): Int

    @Query(
        """
        SELECT COUNT(*) FROM items
        WHERE type = 'FOUND'
        """
    )
    suspend fun getTotalFoundItems(): Int
}