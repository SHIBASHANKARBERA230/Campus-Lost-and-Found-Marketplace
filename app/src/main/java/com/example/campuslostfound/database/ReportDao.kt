package com.example.campuslostfound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReportDao {

    @Insert
    suspend fun insertReport(
        report: ReportEntity
    ): Long

    @Query(
        """
        SELECT * FROM reports
        WHERE reporterUserId = :userId
        ORDER BY createdAt DESC
        """
    )
    suspend fun getReportsByUser(
        userId: Int
    ): List<ReportEntity>

    @Query(
        """
        SELECT * FROM reports
        WHERE status = 'PENDING'
        ORDER BY createdAt DESC
        """
    )
    suspend fun getPendingReports(): List<ReportEntity>

    @Query(
        """
        SELECT * FROM reports
        WHERE itemId = :itemId
        AND reporterUserId = :userId
        LIMIT 1
        """
    )
    suspend fun getExistingReport(
        itemId: Int,
        userId: Int
    ): ReportEntity?

    @Query(
        """
        UPDATE reports
        SET status = :status
        WHERE id = :reportId
        """
    )
    suspend fun updateReportStatus(
        reportId: Int,
        status: String
    ): Int

    @Query(
        """
        DELETE FROM reports
        WHERE itemId = :itemId
        """
    )
    suspend fun deleteReportsForItem(
        itemId: Int
    )

    // =========================================
    // ADMIN DASHBOARD COUNTS
    // =========================================

    @Query(
        """
        SELECT COUNT(*) FROM reports
        WHERE status = 'PENDING'
        """
    )
    suspend fun getPendingReportsCount(): Int
}