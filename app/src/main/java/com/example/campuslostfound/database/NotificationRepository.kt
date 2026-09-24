package com.example.campuslostfound.database

class NotificationRepository(
    private val notificationDao: NotificationDao
) {

    suspend fun insertNotification(
        notification: NotificationEntity
    ) {
        notificationDao.insertNotification(
            notification
        )
    }

    suspend fun getNotifications(
        userId: Int
    ): List<NotificationEntity> {

        return notificationDao.getNotifications(
            userId
        )
    }

    suspend fun getUnreadCount(
        userId: Int
    ): Int {

        return notificationDao.getUnreadCount(
            userId
        )
    }

    suspend fun markAsRead(
        notificationId: Int
    ) {

        notificationDao.markAsRead(
            notificationId
        )
    }

    suspend fun markAllAsRead(
        userId: Int
    ) {

        notificationDao.markAllAsRead(
            userId
        )
    }

    suspend fun deleteAllNotifications(
        userId: Int
    ) {

        notificationDao.deleteAllNotifications(
            userId
        )
    }

    suspend fun notificationExists(
        userId: Int,
        itemId: Int
    ): Boolean {

        return notificationDao.notificationExists(
            userId,
            itemId
        ) > 0
    }
}