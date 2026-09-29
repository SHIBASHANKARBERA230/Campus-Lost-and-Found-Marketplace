package com.example.campuslostfound.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminActivityRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: AdminActivityRepository

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
            AdminActivityRepository(
                database.adminActivityDao()
            )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun logActivity_insertsActivity() = runBlocking {

        repository.logActivity(
            adminUserId = 1,
            action = "MAKE_ADMIN",
            targetUserId = 2,
            details = "Made user admin"
        )

        val activities =
            repository.getAllActivities()

        assertEquals(1, activities.size)
        assertEquals(1, activities[0].adminUserId)
        assertEquals("MAKE_ADMIN", activities[0].action)
        assertEquals(2, activities[0].targetUserId)
        assertEquals("Made user admin", activities[0].details)
    }

    @Test
    fun logActivity_supportsTargetItem() = runBlocking {

        repository.logActivity(
            adminUserId = 1,
            action = "REMOVE_REPORTED_ITEM",
            targetItemId = 101,
            details = "Removed reported item"
        )

        val activities =
            repository.getAllActivities()

        assertEquals(1, activities.size)
        assertEquals(101, activities[0].targetItemId)
        assertEquals("REMOVE_REPORTED_ITEM", activities[0].action)
    }

    @Test
    fun logActivity_supportsDefaultOptionalValues() = runBlocking {

        repository.logActivity(
            adminUserId = 5,
            action = "RESET_PASSWORD"
        )

        val activities =
            repository.getAllActivities()

        assertEquals(1, activities.size)
        assertEquals(5, activities[0].adminUserId)
        assertEquals("RESET_PASSWORD", activities[0].action)
        assertEquals(null, activities[0].targetUserId)
        assertEquals(null, activities[0].targetItemId)
        assertEquals("", activities[0].details)
    }

    @Test
    fun getActivitiesByAdmin_returnsOnlyRequestedAdminActivities() =
        runBlocking {

            repository.logActivity(
                adminUserId = 1,
                action = "MAKE_ADMIN"
            )

            repository.logActivity(
                adminUserId = 1,
                action = "DEACTIVATE_USER"
            )

            repository.logActivity(
                adminUserId = 2,
                action = "REMOVE_ADMIN"
            )

            val activities =
                repository.getActivitiesByAdmin(1)

            assertEquals(2, activities.size)

            assertTrue(
                activities.all {
                    it.adminUserId == 1
                }
            )
        }

    @Test
    fun getAllActivities_returnsAllLoggedActivities() =
        runBlocking {

            repository.logActivity(
                adminUserId = 1,
                action = "MAKE_ADMIN"
            )

            repository.logActivity(
                adminUserId = 2,
                action = "REMOVE_ADMIN"
            )

            repository.logActivity(
                adminUserId = 3,
                action = "ACTIVATE_USER"
            )

            val activities =
                repository.getAllActivities()

            assertEquals(3, activities.size)
        }
}