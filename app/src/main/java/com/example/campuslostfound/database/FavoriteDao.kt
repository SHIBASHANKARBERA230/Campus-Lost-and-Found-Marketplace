package com.example.campuslostfound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("""
        DELETE FROM favorites
        WHERE userId = :userId
        AND itemId = :itemId
    """)
    suspend fun removeFavorite(
        userId: Int,
        itemId: Int
    )

    @Query("""
        SELECT EXISTS(
            SELECT 1
            FROM favorites
            WHERE userId = :userId
            AND itemId = :itemId
        )
    """)
    suspend fun isFavorite(
        userId: Int,
        itemId: Int
    ): Boolean

    @Query("""
        SELECT itemId
        FROM favorites
        WHERE userId = :userId
        ORDER BY createdAt DESC
    """)
    suspend fun getFavoriteItemIds(
        userId: Int
    ): List<Int>

    @Query("""
        DELETE FROM favorites
        WHERE userId = :userId
    """)
    suspend fun removeAllFavorites(
        userId: Int
    )
}