package com.example.campuslostfound.database

class AdminActivityRepository(
    private val dao: AdminActivityDao
) {

    suspend fun logActivity(
        adminUserId: Int,
        action: String,
        targetUserId: Int? = null,
        targetItemId: Int? = null,
        details: String = ""
    ) {
        dao.insertActivity(
            AdminActivityEntity(
                adminUserId = adminUserId,
                action = action,
                targetUserId = targetUserId,
                targetItemId = targetItemId,
                details = details
            )
        )
    }

    suspend fun getAllActivities(): List<AdminActivityEntity> {
        return dao.getAllActivities()
    }

    suspend fun getActivitiesByAdmin(
        adminUserId: Int
    ): List<AdminActivityEntity> {
        return dao.getActivitiesByAdmin(adminUserId)
    }
}