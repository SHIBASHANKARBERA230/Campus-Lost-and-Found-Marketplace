package com.example.campuslostfound.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var favoriteDao: FavoriteDao

    @Before
    fun setup() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        database =
            Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()

        favoriteDao = database.favoriteDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addFavorite_makesItemFavorite() = runBlocking {

        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = 1,
                itemId = 101
            )
        )

        val result =
            favoriteDao.isFavorite(
                userId = 1,
                itemId = 101
            )

        assertTrue(result)
    }

    @Test
    fun isFavorite_unknownFavorite_returnsFalse() = runBlocking {

        val result =
            favoriteDao.isFavorite(
                userId = 999,
                itemId = 999
            )

        assertFalse(result)
    }

    @Test
    fun removeFavorite_removesSpecificFavorite() = runBlocking {

        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = 1,
                itemId = 101
            )
        )

        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = 1,
                itemId = 102
            )
        )

        favoriteDao.removeFavorite(
            userId = 1,
            itemId = 101
        )

        assertFalse(
            favoriteDao.isFavorite(
                userId = 1,
                itemId = 101
            )
        )

        assertTrue(
            favoriteDao.isFavorite(
                userId = 1,
                itemId = 102
            )
        )
    }

    @Test
    fun getFavoriteItemIds_returnsUserFavorites() = runBlocking {

        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = 1,
                itemId = 101
            )
        )

        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = 1,
                itemId = 102
            )
        )

        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = 2,
                itemId = 201
            )
        )

        val result =
            favoriteDao.getFavoriteItemIds(
                userId = 1
            )

        assertEquals(2, result.size)
        assertTrue(result.contains(101))
        assertTrue(result.contains(102))
        assertFalse(result.contains(201))
    }

    @Test
    fun removeAllFavorites_removesOnlyUsersFavorites() = runBlocking {

        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = 1,
                itemId = 101
            )
        )

        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = 1,
                itemId = 102
            )
        )

        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = 2,
                itemId = 201
            )
        )

        favoriteDao.removeAllFavorites(
            userId = 1
        )

        val user1Favorites =
            favoriteDao.getFavoriteItemIds(
                userId = 1
            )

        val user2Favorites =
            favoriteDao.getFavoriteItemIds(
                userId = 2
            )

        assertTrue(user1Favorites.isEmpty())

        assertEquals(
            listOf(201),
            user2Favorites
        )
    }

    @Test
    fun addFavorite_sameFavorite_replacesExistingRecord() = runBlocking {

        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = 1,
                itemId = 101
            )
        )

        favoriteDao.addFavorite(
            FavoriteEntity(
                userId = 1,
                itemId = 101
            )
        )

        val result =
            favoriteDao.getFavoriteItemIds(
                userId = 1
            )

        assertEquals(
            1,
            result.size
        )

        assertEquals(
            101,
            result[0]
        )
    }
}