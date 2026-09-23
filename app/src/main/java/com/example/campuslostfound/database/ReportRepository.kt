package com.example.campuslostfound.database

class ReportRepository(
    private val reportDao: ReportDao
) {

    suspend fun submitReport(
        report: ReportEntity
    ): Long {
        return reportDao.insertReport(report)
    }


    suspend fun getReportsByUser(
        userId: Int
    ): List<ReportEntity> {
        return reportDao.getReportsByUser(userId)
    }


    suspend fun getPendingReports(): List<ReportEntity> {
        return reportDao.getPendingReports()
    }


    suspend fun getExistingReport(
        itemId: Int,
        userId: Int
    ): ReportEntity? {
        return reportDao.getExistingReport(
            itemId,
            userId
        )
    }


    suspend fun updateReportStatus(
        reportId: Int,
        status: String
    ): Int {
        return reportDao.updateReportStatus(
            reportId,
            status
        )
    }


    suspend fun deleteReportsForItem(
        itemId: Int
    ) {
        reportDao.deleteReportsForItem(
            itemId
        )
    }
}