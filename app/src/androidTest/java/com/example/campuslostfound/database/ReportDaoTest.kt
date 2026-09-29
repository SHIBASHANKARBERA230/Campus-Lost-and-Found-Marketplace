package com.example.campuslostfound.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReportDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var reportDao: ReportDao

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

        reportDao = database.reportDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createReport(
        itemId: Int = 101,
        reporterUserId: Int = 1,
        ownerUserId: Int = 2,
        reason: String = "Inappropriate item",
        details: String = "This item violates the rules",
        status: String = "PENDING",
        createdAt: Long = System.currentTimeMillis()
    ): ReportEntity {
        return ReportEntity(
            itemId = itemId,
            reporterUserId = reporterUserId,
            ownerUserId = ownerUserId,
            reason = reason,
            details = details,
            status = status,
            createdAt = createdAt
        )
    }

    @Test
    fun insertReport_returnsGeneratedId() = runBlocking {

        val id =
            reportDao.insertReport(
                createReport()
            )

        assertTrue(id > 0)

        val reports =
            reportDao.getReportsByUser(
                userId = 1
            )

        assertEquals(1, reports.size)
        assertEquals(id.toInt(), reports[0].id)
    }

    @Test
    fun getReportsByUser_returnsOnlyUsersReports() = runBlocking {

        reportDao.insertReport(
            createReport(
                itemId = 101,
                reporterUserId = 1
            )
        )

        reportDao.insertReport(
            createReport(
                itemId = 102,
                reporterUserId = 1
            )
        )

        reportDao.insertReport(
            createReport(
                itemId = 103,
                reporterUserId = 2
            )
        )

        val reports =
            reportDao.getReportsByUser(
                userId = 1
            )

        assertEquals(2, reports.size)
        assertTrue(
            reports.all { it.reporterUserId == 1 }
        )
    }

    @Test
    fun getReportsByUser_returnsNewestReportFirst() = runBlocking {

        reportDao.insertReport(
            createReport(
                itemId = 101,
                reporterUserId = 1,
                createdAt = 1000L
            )
        )

        reportDao.insertReport(
            createReport(
                itemId = 102,
                reporterUserId = 1,
                createdAt = 2000L
            )
        )

        val reports =
            reportDao.getReportsByUser(
                userId = 1
            )

        assertEquals(2, reports.size)
        assertEquals(102, reports[0].itemId)
        assertEquals(101, reports[1].itemId)
    }

    @Test
    fun getPendingReports_returnsOnlyPendingReports() = runBlocking {

        reportDao.insertReport(
            createReport(
                itemId = 101,
                status = "PENDING"
            )
        )

        reportDao.insertReport(
            createReport(
                itemId = 102,
                status = "RESOLVED"
            )
        )

        reportDao.insertReport(
            createReport(
                itemId = 103,
                status = "PENDING"
            )
        )

        val reports =
            reportDao.getPendingReports()

        assertEquals(2, reports.size)
        assertTrue(
            reports.all { it.status == "PENDING" }
        )
    }

    @Test
    fun getExistingReport_returnsMatchingReport() = runBlocking {

        reportDao.insertReport(
            createReport(
                itemId = 101,
                reporterUserId = 1
            )
        )

        val report =
            reportDao.getExistingReport(
                itemId = 101,
                userId = 1
            )

        assertNotNull(report)
        assertEquals(101, report!!.itemId)
        assertEquals(1, report.reporterUserId)
    }

    @Test
    fun getExistingReport_unknownReport_returnsNull() = runBlocking {

        val report =
            reportDao.getExistingReport(
                itemId = 999,
                userId = 999
            )

        assertEquals(null, report)
    }

    @Test
    fun getExistingReport_differentUser_returnsNull() = runBlocking {

        reportDao.insertReport(
            createReport(
                itemId = 101,
                reporterUserId = 1
            )
        )

        val report =
            reportDao.getExistingReport(
                itemId = 101,
                userId = 2
            )

        assertEquals(null, report)
    }

    @Test
    fun updateReportStatus_updatesStatusAndReturnsOne() = runBlocking {

        val id =
            reportDao.insertReport(
                createReport(
                    status = "PENDING"
                )
            )

        val updatedRows =
            reportDao.updateReportStatus(
                reportId = id.toInt(),
                status = "RESOLVED"
            )

        assertEquals(1, updatedRows)

        val reports =
            reportDao.getReportsByUser(
                userId = 1
            )

        assertEquals("RESOLVED", reports[0].status)
    }

    @Test
    fun updateReportStatus_unknownReport_returnsZero() = runBlocking {

        val updatedRows =
            reportDao.updateReportStatus(
                reportId = 9999,
                status = "RESOLVED"
            )

        assertEquals(0, updatedRows)
    }

    @Test
    fun deleteReportsForItem_deletesReportsForSpecifiedItem() = runBlocking {

        reportDao.insertReport(
            createReport(
                itemId = 101,
                reporterUserId = 1
            )
        )

        reportDao.insertReport(
            createReport(
                itemId = 101,
                reporterUserId = 2
            )
        )

        reportDao.insertReport(
            createReport(
                itemId = 202,
                reporterUserId = 3
            )
        )

        reportDao.deleteReportsForItem(
            itemId = 101
        )

        val user1Reports =
            reportDao.getReportsByUser(
                userId = 1
            )

        val user2Reports =
            reportDao.getReportsByUser(
                userId = 2
            )

        val user3Reports =
            reportDao.getReportsByUser(
                userId = 3
            )

        assertTrue(user1Reports.isEmpty())
        assertTrue(user2Reports.isEmpty())
        assertEquals(1, user3Reports.size)
        assertEquals(202, user3Reports[0].itemId)
    }

    @Test
    fun getPendingReportsCount_returnsOnlyPendingReports() = runBlocking {

        reportDao.insertReport(
            createReport(
                itemId = 101,
                status = "PENDING"
            )
        )

        reportDao.insertReport(
            createReport(
                itemId = 102,
                status = "PENDING"
            )
        )

        reportDao.insertReport(
            createReport(
                itemId = 103,
                status = "RESOLVED"
            )
        )

        reportDao.insertReport(
            createReport(
                itemId = 104,
                status = "REJECTED"
            )
        )

        val count =
            reportDao.getPendingReportsCount()

        assertEquals(2, count)
    }
}