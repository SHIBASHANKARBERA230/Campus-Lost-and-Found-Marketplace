package com.example.campuslostfound.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var database: AppDatabase

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
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun database_createsSuccessfully() {
        assertNotNull(database)
        assertNotNull(database.openHelper.writableDatabase)
    }

    @Test
    fun itemDao_isAvailable() {
        assertNotNull(database.itemDao())
    }

    @Test
    fun userDao_isAvailable() {
        assertNotNull(database.userDao())
    }

    @Test
    fun notificationDao_isAvailable() {
        assertNotNull(database.notificationDao())
    }

    @Test
    fun claimDao_isAvailable() {
        assertNotNull(database.claimDao())
    }

    @Test
    fun favoriteDao_isAvailable() {
        assertNotNull(database.favoriteDao())
    }

    @Test
    fun reportDao_isAvailable() {
        assertNotNull(database.reportDao())
    }

    @Test
    fun adminActivityDao_isAvailable() {
        assertNotNull(database.adminActivityDao())
    }

    @Test
    fun allDaoInstances_areStable() {
        assertSame(database.itemDao(), database.itemDao())
        assertSame(database.userDao(), database.userDao())
        assertSame(database.notificationDao(), database.notificationDao())
        assertSame(database.claimDao(), database.claimDao())
        assertSame(database.favoriteDao(), database.favoriteDao())
        assertSame(database.reportDao(), database.reportDao())
        assertSame(database.adminActivityDao(), database.adminActivityDao())
    }
}