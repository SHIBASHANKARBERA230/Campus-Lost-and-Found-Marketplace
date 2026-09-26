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
class ReportRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: ReportRepository

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
            ReportRepository(
                database.reportDao()
            )
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createReport(
        itemId: Int = 101,
        reporterUserId: Int = 1,
        ownerUserId: Int = 2,
        reason: String = "Incorrect information",
        details: String = "This item contains incorrect information",
        status: String = "PENDING"
    ): ReportEntity {
        return ReportEntity(
            itemId = itemId,
            reporterUserId = reporterUserId,
            ownerUserId = ownerUserId,
            reason = reason,
            details = details,
            status = status
        )
    }

    @Test
    fun submitReport_insertsReportSuccessfully() = runBlocking {

        val id =
            repository.submitReport(
                createReport()
            )

        assertTrue(id > 0)

        val reports =
            repository.getReportsByUser(1)

        assertEquals(1, reports.size)
        assertEquals(101, reports[0].itemId)
        assertEquals(1, reports[0].reporterUserId)
        assertEquals("PENDING", reports[0].status)
    }

    @Test
    fun getReportsByUser_returnsOnlyUserReports() = runBlocking {

        repository.submitReport(
            createReport(
                itemId = 101,
                reporterUserId = 1
            )
        )

        repository.submitReport(
            createReport(
                itemId = 202,
                reporterUserId = 1
            )
        )

        repository.submitReport(
            createReport(
                itemId = 303,
                reporterUserId = 2
            )
        )

        val reports =
            repository.getReportsByUser(1)

        assertEquals(2, reports.size)

        assertTrue(
            reports.all {
                it.reporterUserId == 1
            }
        )
    }

    @Test
    fun getPendingReports_returnsOnlyPendingReports() = runBlocking {

        repository.submitReport(
            createReport(
                itemId = 101,
                status = "PENDING"
            )
        )

        repository.submitReport(
            createReport(
                itemId = 202,
                status = "RESOLVED"
            )
        )

        repository.submitReport(
            createReport(
                itemId = 303,
                status = "PENDING"
            )
        )

        val reports =
            repository.getPendingReports()

        assertEquals(2, reports.size)

        assertTrue(
            reports.all {
                it.status == "PENDING"
            }
        )
    }

    @Test
    fun getExistingReport_returnsMatchingReport() = runBlocking {

        repository.submitReport(
            createReport(
                itemId = 101,
                reporterUserId = 5
            )
        )

        val report =
            repository.getExistingReport(
                itemId = 101,
                userId = 5
            )

        assertNotNull(report)
        assertEquals(101, report!!.itemId)
        assertEquals(5, report.reporterUserId)
    }

    @Test
    fun getExistingReport_returnsNullWhenNoMatchingReport() =
        runBlocking {

            repository.submitReport(
                createReport(
                    itemId = 101,
                    reporterUserId = 5
                )
            )

            val report =
                repository.getExistingReport(
                    itemId = 202,
                    userId = 5
                )

            assertEquals(null, report)
        }

    @Test
    fun updateReportStatus_updatesReportSuccessfully() =
        runBlocking {

            val id =
                repository.submitReport(
                    createReport(
                        status = "PENDING"
                    )
                )

            val rowsUpdated =
                repository.updateReportStatus(
                    reportId = id.toInt(),
                    status = "RESOLVED"
                )

            assertEquals(1, rowsUpdated)

            val reports =
                repository.getReportsByUser(1)

            assertEquals(
                "RESOLVED",
                reports[0].status
            )
        }

    @Test
    fun deleteReportsForItem_deletesOnlySpecifiedItemReports() =
        runBlocking {

            repository.submitReport(
                createReport(
                    itemId = 101,
                    reporterUserId = 1
                )
            )

            repository.submitReport(
                createReport(
                    itemId = 101,
                    reporterUserId = 2
                )
            )

            repository.submitReport(
                createReport(
                    itemId = 202,
                    reporterUserId = 3
                )
            )

            repository.deleteReportsForItem(101)

            assertEquals(
                0,
                repository.getReportsByUser(1).size
            )

            assertEquals(
                0,
                repository.getReportsByUser(2).size
            )

            assertEquals(
                1,
                repository.getReportsByUser(3).size
            )
        }
}