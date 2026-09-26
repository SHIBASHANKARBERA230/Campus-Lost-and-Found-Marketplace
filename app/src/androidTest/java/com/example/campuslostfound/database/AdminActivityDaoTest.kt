package com.example.campuslostfound.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminActivityDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var adminActivityDao: AdminActivityDao

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

        adminActivityDao = database.adminActivityDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createActivity(
        adminUserId: Int = 1,
        action: String = "MAKE_ADMIN",
        targetUserId: Int? = null,
        targetItemId: Int? = null,
        details: String = "Admin activity",
        createdAt: Long = System.currentTimeMillis()
    ): AdminActivityEntity {
        return AdminActivityEntity(
            adminUserId = adminUserId,
            action = action,
            targetUserId = targetUserId,
            targetItemId = targetItemId,
            details = details,
            createdAt = createdAt
        )
    }

    @Test
    fun insertActivity_activityCanBeRetrieved() = runBlocking {

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 1,
                action = "MAKE_ADMIN",
                targetUserId = 2
            )
        )

        val activities =
            adminActivityDao.getAllActivities()

        assertEquals(1, activities.size)
        assertEquals(1, activities[0].adminUserId)
        assertEquals("MAKE_ADMIN", activities[0].action)
        assertEquals(2, activities[0].targetUserId)
    }

    @Test
    fun getAllActivities_returnsAllActivities() = runBlocking {

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 1,
                action = "MAKE_ADMIN"
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 2,
                action = "REMOVE_ADMIN"
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 3,
                action = "DEACTIVATE_USER"
            )
        )

        val activities =
            adminActivityDao.getAllActivities()

        assertEquals(3, activities.size)
    }

    @Test
    fun getAllActivities_returnsNewestActivityFirst() = runBlocking {

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 1,
                action = "OLD_ACTIVITY",
                createdAt = 1000L
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 2,
                action = "NEW_ACTIVITY",
                createdAt = 2000L
            )
        )

        val activities =
            adminActivityDao.getAllActivities()

        assertEquals(2, activities.size)
        assertEquals("NEW_ACTIVITY", activities[0].action)
        assertEquals("OLD_ACTIVITY", activities[1].action)
    }

    @Test
    fun getActivitiesByAdmin_returnsOnlyActivitiesByAdmin() = runBlocking {

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 1,
                action = "MAKE_ADMIN"
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 1,
                action = "DEACTIVATE_USER"
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 2,
                action = "REMOVE_ADMIN"
            )
        )

        val activities =
            adminActivityDao.getActivitiesByAdmin(
                adminUserId = 1
            )

        assertEquals(2, activities.size)
        assertTrue(
            activities.all { it.adminUserId == 1 }
        )
    }

    @Test
    fun getActivitiesForUser_returnsActivitiesTargetingUser() = runBlocking {

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 1,
                action = "MAKE_ADMIN",
                targetUserId = 10
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 2,
                action = "DEACTIVATE_USER",
                targetUserId = 10
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 3,
                action = "ACTIVATE_USER",
                targetUserId = 20
            )
        )

        val activities =
            adminActivityDao.getActivitiesForUser(
                userId = 10
            )

        assertEquals(2, activities.size)
        assertTrue(
            activities.all { it.targetUserId == 10 }
        )
    }

    @Test
    fun getActivitiesForItem_returnsActivitiesTargetingItem() = runBlocking {

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 1,
                action = "REMOVE_REPORTED_ITEM",
                targetItemId = 101
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 2,
                action = "DISMISS_REPORT",
                targetItemId = 101
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 3,
                action = "REMOVE_REPORTED_ITEM",
                targetItemId = 202
            )
        )

        val activities =
            adminActivityDao.getActivitiesForItem(
                itemId = 101
            )

        assertEquals(2, activities.size)
        assertTrue(
            activities.all { it.targetItemId == 101 }
        )
    }

    @Test
    fun getActivityCount_returnsTotalNumberOfActivities() = runBlocking {

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 1
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 2
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 3
            )
        )

        val count =
            adminActivityDao.getActivityCount()

        assertEquals(3, count)
    }

    @Test
    fun deleteAllActivities_removesAllActivities() = runBlocking {

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 1
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 2
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 3
            )
        )

        adminActivityDao.deleteAllActivities()

        val activities =
            adminActivityDao.getAllActivities()

        assertTrue(activities.isEmpty())

        assertEquals(
            0,
            adminActivityDao.getActivityCount()
        )
    }

    @Test
    fun getActivitiesForUser_ignoresActivitiesWithoutTargetUser() = runBlocking {

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 1,
                action = "MAKE_ADMIN",
                targetUserId = null
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 2,
                action = "DEACTIVATE_USER",
                targetUserId = 10
            )
        )

        val activities =
            adminActivityDao.getActivitiesForUser(
                userId = 10
            )

        assertEquals(1, activities.size)
        assertEquals(10, activities[0].targetUserId)
    }

    @Test
    fun getActivitiesForItem_ignoresActivitiesWithoutTargetItem() = runBlocking {

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 1,
                action = "MAKE_ADMIN",
                targetItemId = null
            )
        )

        adminActivityDao.insertActivity(
            createActivity(
                adminUserId = 2,
                action = "REMOVE_REPORTED_ITEM",
                targetItemId = 101
            )
        )

        val activities =
            adminActivityDao.getActivitiesForItem(
                itemId = 101
            )

        assertEquals(1, activities.size)
        assertEquals(101, activities[0].targetItemId)
    }
}