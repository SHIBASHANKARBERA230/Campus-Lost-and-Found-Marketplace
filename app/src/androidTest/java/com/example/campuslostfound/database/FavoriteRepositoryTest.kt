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
class FavoriteRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: FavoriteRepository

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

        repository =
            FavoriteRepository(
                database.favoriteDao()
            )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addFavorite_addsFavoriteSuccessfully() = runBlocking {

        repository.addFavorite(
            userId = 1,
            itemId = 101
        )

        assertTrue(
            repository.isFavorite(
                userId = 1,
                itemId = 101
            )
        )
    }

    @Test
    fun removeFavorite_removesFavoriteSuccessfully() = runBlocking {

        repository.addFavorite(
            userId = 1,
            itemId = 101
        )

        repository.removeFavorite(
            userId = 1,
            itemId = 101
        )

        assertFalse(
            repository.isFavorite(
                userId = 1,
                itemId = 101
            )
        )
    }

    @Test
    fun isFavorite_returnsCorrectResult() = runBlocking {

        repository.addFavorite(
            userId = 1,
            itemId = 101
        )

        assertTrue(
            repository.isFavorite(
                userId = 1,
                itemId = 101
            )
        )

        assertFalse(
            repository.isFavorite(
                userId = 1,
                itemId = 202
            )
        )
    }

    @Test
    fun getFavoriteItemIds_returnsUserFavoriteItems() = runBlocking {

        repository.addFavorite(
            userId = 1,
            itemId = 101
        )

        repository.addFavorite(
            userId = 1,
            itemId = 202
        )

        repository.addFavorite(
            userId = 2,
            itemId = 303
        )

        val favoriteIds =
            repository.getFavoriteItemIds(
                userId = 1
            )

        assertEquals(2, favoriteIds.size)
        assertTrue(favoriteIds.contains(101))
        assertTrue(favoriteIds.contains(202))
    }

    @Test
    fun removeAllFavorites_removesOnlySpecifiedUsersFavorites() =
        runBlocking {

            repository.addFavorite(
                userId = 1,
                itemId = 101
            )

            repository.addFavorite(
                userId = 1,
                itemId = 202
            )

            repository.addFavorite(
                userId = 2,
                itemId = 303
            )

            repository.removeAllFavorites(
                userId = 1
            )

            assertEquals(
                0,
                repository.getFavoriteItemIds(1).size
            )

            assertEquals(
                1,
                repository.getFavoriteItemIds(2).size
            )

            assertTrue(
                repository.isFavorite(
                    userId = 2,
                    itemId = 303
                )
            )
        }
}