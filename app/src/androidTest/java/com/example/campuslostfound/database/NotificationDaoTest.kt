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
class NotificationDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var notificationDao: NotificationDao

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

        notificationDao = database.notificationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertNotification_notificationCanBeRetrieved() = runBlocking {

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Item Match",
                message = "A matching item was found",
                itemId = 101
            )
        )

        val notifications =
            notificationDao.getNotifications(
                userId = 1
            )

        assertEquals(1, notifications.size)
        assertEquals("Item Match", notifications[0].title)
        assertEquals("A matching item was found", notifications[0].message)
        assertEquals(101, notifications[0].itemId)
        assertFalse(notifications[0].isRead)
    }

    @Test
    fun getNotifications_returnsOnlyUsersNotifications() = runBlocking {

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Notification 1",
                message = "Message 1",
                itemId = 101
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Notification 2",
                message = "Message 2",
                itemId = 102
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 2,
                title = "Notification 3",
                message = "Message 3",
                itemId = 201
            )
        )

        val notifications =
            notificationDao.getNotifications(
                userId = 1
            )

        assertEquals(2, notifications.size)
        assertTrue(
            notifications.all { it.userId == 1 }
        )
        assertTrue(
            notifications.any { it.itemId == 101 }
        )
        assertTrue(
            notifications.any { it.itemId == 102 }
        )
        assertFalse(
            notifications.any { it.itemId == 201 }
        )
    }

    @Test
    fun getUnreadCount_returnsNumberOfUnreadNotifications() = runBlocking {

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Unread 1",
                message = "Message 1",
                itemId = 101,
                isRead = false
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Read",
                message = "Message 2",
                itemId = 102,
                isRead = true
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Unread 2",
                message = "Message 3",
                itemId = 103,
                isRead = false
            )
        )

        val unreadCount =
            notificationDao.getUnreadCount(
                userId = 1
            )

        assertEquals(2, unreadCount)
    }

    @Test
    fun getUnreadCount_ignoresOtherUsers() = runBlocking {

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "User 1",
                message = "Message 1",
                itemId = 101,
                isRead = false
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 2,
                title = "User 2",
                message = "Message 2",
                itemId = 201,
                isRead = false
            )
        )

        val unreadCount =
            notificationDao.getUnreadCount(
                userId = 1
            )

        assertEquals(1, unreadCount)
    }

    @Test
    fun markAsRead_marksSpecificNotificationAsRead() = runBlocking {

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Test",
                message = "Test message",
                itemId = 101,
                isRead = false
            )
        )

        val notification =
            notificationDao.getNotifications(
                userId = 1
            )[0]

        notificationDao.markAsRead(
            notificationId = notification.id
        )

        val updatedNotification =
            notificationDao.getNotifications(
                userId = 1
            )[0]

        assertTrue(updatedNotification.isRead)
    }

    @Test
    fun markAsRead_doesNotMarkOtherNotifications() = runBlocking {

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Notification 1",
                message = "Message 1",
                itemId = 101,
                isRead = false
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Notification 2",
                message = "Message 2",
                itemId = 102,
                isRead = false
            )
        )

        val notifications =
            notificationDao.getNotifications(
                userId = 1
            )

        notificationDao.markAsRead(
            notificationId = notifications[0].id
        )

        val updatedNotifications =
            notificationDao.getNotifications(
                userId = 1
            )

        assertTrue(
            updatedNotifications.any { it.id == notifications[0].id && it.isRead }
        )

        assertTrue(
            updatedNotifications.any { it.id == notifications[1].id && !it.isRead }
        )
    }

    @Test
    fun markAllAsRead_marksOnlyUsersNotifications() = runBlocking {

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "User 1 - 1",
                message = "Message 1",
                itemId = 101,
                isRead = false
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "User 1 - 2",
                message = "Message 2",
                itemId = 102,
                isRead = false
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 2,
                title = "User 2",
                message = "Message 3",
                itemId = 201,
                isRead = false
            )
        )

        notificationDao.markAllAsRead(
            userId = 1
        )

        assertEquals(
            0,
            notificationDao.getUnreadCount(
                userId = 1
            )
        )

        assertEquals(
            1,
            notificationDao.getUnreadCount(
                userId = 2
            )
        )
    }

    @Test
    fun deleteAllNotifications_deletesOnlyUsersNotifications() = runBlocking {

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "User 1 - 1",
                message = "Message 1",
                itemId = 101
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "User 1 - 2",
                message = "Message 2",
                itemId = 102
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 2,
                title = "User 2",
                message = "Message 3",
                itemId = 201
            )
        )

        notificationDao.deleteAllNotifications(
            userId = 1
        )

        assertTrue(
            notificationDao.getNotifications(
                userId = 1
            ).isEmpty()
        )

        assertEquals(
            1,
            notificationDao.getNotifications(
                userId = 2
            ).size
        )
    }

    @Test
    fun notificationExists_existingNotification_returnsOne() = runBlocking {

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Item Match",
                message = "Matching item found",
                itemId = 101
            )
        )

        val result =
            notificationDao.notificationExists(
                userId = 1,
                itemId = 101
            )

        assertEquals(1, result)
    }

    @Test
    fun notificationExists_unknownNotification_returnsZero() = runBlocking {

        val result =
            notificationDao.notificationExists(
                userId = 1,
                itemId = 999
            )

        assertEquals(0, result)
    }

    @Test
    fun notificationExists_differentUser_returnsZero() = runBlocking {

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Item Match",
                message = "Matching item found",
                itemId = 101
            )
        )

        val result =
            notificationDao.notificationExists(
                userId = 2,
                itemId = 101
            )

        assertEquals(0, result)
    }

    @Test
    fun getNotifications_returnsNewestNotificationFirst() = runBlocking {

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Older",
                message = "Older notification",
                itemId = 101,
                createdAt = 1000L
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "Newer",
                message = "Newer notification",
                itemId = 102,
                createdAt = 2000L
            )
        )

        val notifications =
            notificationDao.getNotifications(
                userId = 1
            )

        assertEquals(2, notifications.size)
        assertEquals("Newer", notifications[0].title)
        assertEquals("Older", notifications[1].title)
    }
}