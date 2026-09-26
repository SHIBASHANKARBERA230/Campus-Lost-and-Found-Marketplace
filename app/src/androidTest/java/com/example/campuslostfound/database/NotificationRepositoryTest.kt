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
class NotificationRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: NotificationRepository

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
            NotificationRepository(
                database.notificationDao()
            )
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createNotification(
        userId: Int = 1,
        title: String = "New Match",
        message: String = "A matching item was found",
        itemId: Int = 101,
        isRead: Boolean = false
    ): NotificationEntity {
        return NotificationEntity(
            userId = userId,
            title = title,
            message = message,
            itemId = itemId,
            isRead = isRead
        )
    }

    @Test
    fun insertNotification_insertsSuccessfully() = runBlocking {

        repository.insertNotification(
            createNotification(
                userId = 1,
                itemId = 101
            )
        )

        val notifications =
            repository.getNotifications(1)

        assertEquals(1, notifications.size)
        assertEquals("New Match", notifications[0].title)
        assertEquals(101, notifications[0].itemId)
        assertFalse(notifications[0].isRead)
    }

    @Test
    fun getNotifications_returnsOnlyUserNotifications() = runBlocking {

        repository.insertNotification(
            createNotification(
                userId = 1,
                itemId = 101
            )
        )

        repository.insertNotification(
            createNotification(
                userId = 1,
                itemId = 102,
                title = "Another Match"
            )
        )

        repository.insertNotification(
            createNotification(
                userId = 2,
                itemId = 201
            )
        )

        val notifications =
            repository.getNotifications(1)

        assertEquals(2, notifications.size)

        assertTrue(
            notifications.all {
                it.userId == 1
            }
        )
    }

    @Test
    fun getUnreadCount_returnsCorrectCount() = runBlocking {

        repository.insertNotification(
            createNotification(
                userId = 1,
                itemId = 101,
                isRead = false
            )
        )

        repository.insertNotification(
            createNotification(
                userId = 1,
                itemId = 102,
                isRead = false
            )
        )

        repository.insertNotification(
            createNotification(
                userId = 1,
                itemId = 103,
                isRead = true
            )
        )

        assertEquals(
            2,
            repository.getUnreadCount(1)
        )
    }

    @Test
    fun markAsRead_marksNotificationAsRead() = runBlocking {

        repository.insertNotification(
            createNotification(
                userId = 1,
                itemId = 101
            )
        )

        val notification =
            repository.getNotifications(1)[0]

        repository.markAsRead(
            notification.id
        )

        val updated =
            repository.getNotifications(1)[0]

        assertTrue(updated.isRead)
        assertEquals(
            0,
            repository.getUnreadCount(1)
        )
    }

    @Test
    fun markAllAsRead_marksOnlySpecifiedUsersNotifications() =
        runBlocking {

            repository.insertNotification(
                createNotification(
                    userId = 1,
                    itemId = 101
                )
            )

            repository.insertNotification(
                createNotification(
                    userId = 1,
                    itemId = 102
                )
            )

            repository.insertNotification(
                createNotification(
                    userId = 2,
                    itemId = 201
                )
            )

            repository.markAllAsRead(1)

            assertEquals(
                0,
                repository.getUnreadCount(1)
            )

            assertEquals(
                1,
                repository.getUnreadCount(2)
            )
        }

    @Test
    fun deleteAllNotifications_deletesOnlySpecifiedUsersNotifications() =
        runBlocking {

            repository.insertNotification(
                createNotification(
                    userId = 1,
                    itemId = 101
                )
            )

            repository.insertNotification(
                createNotification(
                    userId = 1,
                    itemId = 102
                )
            )

            repository.insertNotification(
                createNotification(
                    userId = 2,
                    itemId = 201
                )
            )

            repository.deleteAllNotifications(1)

            assertEquals(
                0,
                repository.getNotifications(1).size
            )

            assertEquals(
                1,
                repository.getNotifications(2).size
            )
        }

    @Test
    fun notificationExists_returnsCorrectResult() = runBlocking {

        repository.insertNotification(
            createNotification(
                userId = 1,
                itemId = 101
            )
        )

        assertTrue(
            repository.notificationExists(
                userId = 1,
                itemId = 101
            )
        )

        assertFalse(
            repository.notificationExists(
                userId = 1,
                itemId = 202
            )
        )

        assertFalse(
            repository.notificationExists(
                userId = 2,
                itemId = 101
            )
        )
    }
}