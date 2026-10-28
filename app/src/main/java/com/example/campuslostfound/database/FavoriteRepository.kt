package com.example.campuslostfound.database

class FavoriteRepository(
    private val favoriteDao: FavoriteDao
) {

    suspend fun addFavorite(
        userId: Int,
        itemId: Int
    ) {
        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = userId,
                itemId = itemId
            )
        )
    }

    suspend fun removeFavorite(
        userId: Int,
        itemId: Int
    ) {
        favoriteDao.removeFavorite(
            userId,
            itemId
        )
    }

    suspend fun isFavorite(
        userId: Int,
        itemId: Int
    ): Boolean {
        return favoriteDao.isFavorite(
            userId,
            itemId
        )
    }

    suspend fun getFavoriteItemIds(
        userId: Int
    ): List<Int> {
        return favoriteDao.getFavoriteItemIds(userId)
    }

    suspend fun removeAllFavorites(
        userId: Int
    ) {
        favoriteDao.removeAllFavorites(userId)
    }
}