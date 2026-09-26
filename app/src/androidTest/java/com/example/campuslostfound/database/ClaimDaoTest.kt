package com.example.campuslostfound.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClaimDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var claimDao: ClaimDao

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

        claimDao =
            database.claimDao()
    }

    @After
    fun tearDown() {

        database.close()
    }

    private fun createClaim(
        itemId: Int = 101,
        claimantUserId: Int = 1,
        ownerUserId: Int = 2,
        reason: String = "This is my item",
        additionalDetails: String = "I can provide proof",
        status: String = "PENDING"
    ): ClaimEntity {

        return ClaimEntity(
            itemId = itemId,
            claimantUserId = claimantUserId,
            ownerUserId = ownerUserId,
            reason = reason,
            additionalDetails = additionalDetails,
            status = status
        )
    }

    @Test
    fun insertClaim_returnsGeneratedId() = runBlocking {

        val id =
            claimDao.insertClaim(
                createClaim()
            )

        assertTrue(id > 0)

        val claim =
            claimDao.getClaimById(
                id.toInt()
            )

        assertNotNull(claim)
    }

    @Test
    fun getClaimById_returnsCorrectClaim() = runBlocking {

        val id =
            claimDao.insertClaim(
                createClaim(
                    itemId = 101,
                    claimantUserId = 1,
                    ownerUserId = 2
                )
            )

        val claim =
            claimDao.getClaimById(
                id.toInt()
            )

        assertNotNull(claim)
        assertEquals(101, claim!!.itemId)
        assertEquals(1, claim.claimantUserId)
        assertEquals(2, claim.ownerUserId)
        assertEquals("PENDING", claim.status)
    }

    @Test
    fun getClaimById_unknownId_returnsNull() = runBlocking {

        val claim =
            claimDao.getClaimById(
                9999
            )

        assertEquals(null, claim)
    }

    @Test
    fun getClaimsForItem_returnsOnlyClaimsForItem() = runBlocking {

        claimDao.insertClaim(
            createClaim(
                itemId = 101,
                claimantUserId = 1
            )
        )

        claimDao.insertClaim(
            createClaim(
                itemId = 101,
                claimantUserId = 3
            )
        )

        claimDao.insertClaim(
            createClaim(
                itemId = 202,
                claimantUserId = 4
            )
        )

        val claims =
            claimDao.getClaimsForItem(
                101
            )

        assertEquals(2, claims.size)

        assertTrue(
            claims.all { it.itemId == 101 }
        )

        assertFalse(
            claims.any { it.itemId == 202 }
        )
    }

    @Test
    fun getMyClaims_returnsOnlyClaimsByUser() = runBlocking {

        claimDao.insertClaim(
            createClaim(
                itemId = 101,
                claimantUserId = 1
            )
        )

        claimDao.insertClaim(
            createClaim(
                itemId = 102,
                claimantUserId = 1
            )
        )

        claimDao.insertClaim(
            createClaim(
                itemId = 103,
                claimantUserId = 2
            )
        )

        val claims =
            claimDao.getMyClaims(
                1
            )

        assertEquals(2, claims.size)

        assertTrue(
            claims.all { it.claimantUserId == 1 }
        )

        assertFalse(
            claims.any { it.claimantUserId == 2 }
        )
    }

    @Test
    fun getClaimsForOwner_returnsOnlyOwnersClaims() = runBlocking {

        claimDao.insertClaim(
            createClaim(
                itemId = 101,
                ownerUserId = 2
            )
        )

        claimDao.insertClaim(
            createClaim(
                itemId = 102,
                ownerUserId = 2
            )
        )

        claimDao.insertClaim(
            createClaim(
                itemId = 103,
                ownerUserId = 3
            )
        )

        val claims =
            claimDao.getClaimsForOwner(
                2
            )

        assertEquals(2, claims.size)

        assertTrue(
            claims.all { it.ownerUserId == 2 }
        )

        assertFalse(
            claims.any { it.ownerUserId == 3 }
        )
    }

    @Test
    fun hasPendingClaim_pendingClaim_returnsOne() = runBlocking {

        claimDao.insertClaim(
            createClaim(
                itemId = 101,
                claimantUserId = 1,
                status = "PENDING"
            )
        )

        val result =
            claimDao.hasPendingClaim(
                itemId = 101,
                userId = 1
            )

        assertEquals(1, result)
    }

    @Test
    fun hasPendingClaim_rejectedClaim_returnsOne() = runBlocking {

        claimDao.insertClaim(
            createClaim(
                itemId = 101,
                claimantUserId = 1,
                status = "REJECTED"
            )
        )

        val result =
            claimDao.hasPendingClaim(
                itemId = 101,
                userId = 1
            )

        assertEquals(1, result)
    }

    @Test
    fun hasPendingClaim_differentUser_returnsZero() = runBlocking {

        claimDao.insertClaim(
            createClaim(
                itemId = 101,
                claimantUserId = 1,
                status = "PENDING"
            )
        )

        val result =
            claimDao.hasPendingClaim(
                itemId = 101,
                userId = 2
            )

        assertEquals(0, result)
    }

    @Test
    fun updateClaimStatus_updatesStatus() = runBlocking {

        val id =
            claimDao.insertClaim(
                createClaim(
                    status = "PENDING"
                )
            )

        claimDao.updateClaimStatus(
            claimId = id.toInt(),
            status = "APPROVED"
        )

        val claim =
            claimDao.getClaimById(
                id.toInt()
            )

        assertNotNull(claim)
        assertEquals("APPROVED", claim!!.status)
    }

    @Test
    fun rejectOtherPendingClaims_rejectsOtherPendingClaims() = runBlocking {

        val firstId =
            claimDao.insertClaim(
                createClaim(
                    itemId = 101,
                    claimantUserId = 1,
                    status = "PENDING"
                )
            )

        val secondId =
            claimDao.insertClaim(
                createClaim(
                    itemId = 101,
                    claimantUserId = 3,
                    status = "PENDING"
                )
            )

        val thirdId =
            claimDao.insertClaim(
                createClaim(
                    itemId = 101,
                    claimantUserId = 4,
                    status = "PENDING"
                )
            )

        claimDao.rejectOtherPendingClaims(
            itemId = 101,
            approvedClaimId = firstId.toInt()
        )

        val approvedClaim =
            claimDao.getClaimById(
                firstId.toInt()
            )

        val secondClaim =
            claimDao.getClaimById(
                secondId.toInt()
            )

        val thirdClaim =
            claimDao.getClaimById(
                thirdId.toInt()
            )

        assertEquals(
            "PENDING",
            approvedClaim!!.status
        )

        assertEquals(
            "REJECTED",
            secondClaim!!.status
        )

        assertEquals(
            "REJECTED",
            thirdClaim!!.status
        )
    }

    @Test
    fun rejectOtherPendingClaims_doesNotRejectClaimsForOtherItems() = runBlocking {

        val approvedId =
            claimDao.insertClaim(
                createClaim(
                    itemId = 101,
                    claimantUserId = 1,
                    status = "PENDING"
                )
            )

        val otherItemId =
            claimDao.insertClaim(
                createClaim(
                    itemId = 202,
                    claimantUserId = 3,
                    status = "PENDING"
                )
            )

        claimDao.rejectOtherPendingClaims(
            itemId = 101,
            approvedClaimId = approvedId.toInt()
        )

        val otherItemClaim =
            claimDao.getClaimById(
                otherItemId.toInt()
            )

        assertEquals(
            "PENDING",
            otherItemClaim!!.status
        )
    }

    @Test
    fun getPendingClaimsCount_returnsOnlyPendingClaims() = runBlocking {

        claimDao.insertClaim(
            createClaim(
                itemId = 101,
                status = "PENDING"
            )
        )

        claimDao.insertClaim(
            createClaim(
                itemId = 102,
                status = "PENDING"
            )
        )

        claimDao.insertClaim(
            createClaim(
                itemId = 103,
                status = "APPROVED"
            )
        )

        claimDao.insertClaim(
            createClaim(
                itemId = 104,
                status = "REJECTED"
            )
        )

        val count =
            claimDao.getPendingClaimsCount()

        assertEquals(2, count)
    }
}