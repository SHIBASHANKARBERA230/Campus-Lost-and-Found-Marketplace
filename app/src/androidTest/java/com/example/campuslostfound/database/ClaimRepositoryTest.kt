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
class ClaimRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: ClaimRepository

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
            ClaimRepository(
                database.claimDao()
            )
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
    fun insertClaim_insertsClaimSuccessfully() = runBlocking {

        val id =
            repository.insertClaim(
                createClaim()
            )

        assertTrue(id > 0)

        val claim =
            repository.getClaimById(id.toInt())

        assertNotNull(claim)
        assertEquals(101, claim!!.itemId)
        assertEquals(1, claim.claimantUserId)
        assertEquals(2, claim.ownerUserId)
        assertEquals("PENDING", claim.status)
    }

    @Test
    fun getClaimById_returnsCorrectClaim() = runBlocking {

        val id =
            repository.insertClaim(
                createClaim(
                    itemId = 202,
                    claimantUserId = 5
                )
            )

        val claim =
            repository.getClaimById(id.toInt())

        assertNotNull(claim)
        assertEquals(202, claim!!.itemId)
        assertEquals(5, claim.claimantUserId)
    }

    @Test
    fun getClaimById_returnsNullForUnknownClaim() = runBlocking {

        val claim =
            repository.getClaimById(9999)

        assertEquals(null, claim)
    }

    @Test
    fun getClaimsForItem_returnsOnlyClaimsForItem() = runBlocking {

        repository.insertClaim(
            createClaim(
                itemId = 101,
                claimantUserId = 1
            )
        )

        repository.insertClaim(
            createClaim(
                itemId = 101,
                claimantUserId = 3
            )
        )

        repository.insertClaim(
            createClaim(
                itemId = 202,
                claimantUserId = 4
            )
        )

        val claims =
            repository.getClaimsForItem(101)

        assertEquals(2, claims.size)

        assertTrue(
            claims.all {
                it.itemId == 101
            }
        )
    }

    @Test
    fun getMyClaims_returnsOnlyUserClaims() = runBlocking {

        repository.insertClaim(
            createClaim(
                claimantUserId = 1,
                itemId = 101
            )
        )

        repository.insertClaim(
            createClaim(
                claimantUserId = 1,
                itemId = 202
            )
        )

        repository.insertClaim(
            createClaim(
                claimantUserId = 2,
                itemId = 303
            )
        )

        val claims =
            repository.getMyClaims(1)

        assertEquals(2, claims.size)

        assertTrue(
            claims.all {
                it.claimantUserId == 1
            }
        )
    }

    @Test
    fun getClaimsForOwner_returnsClaimsForOwner() = runBlocking {

        repository.insertClaim(
            createClaim(
                ownerUserId = 10,
                claimantUserId = 1,
                itemId = 101
            )
        )

        repository.insertClaim(
            createClaim(
                ownerUserId = 10,
                claimantUserId = 2,
                itemId = 202
            )
        )

        repository.insertClaim(
            createClaim(
                ownerUserId = 20,
                claimantUserId = 3,
                itemId = 303
            )
        )

        val claims =
            repository.getClaimsForOwner(10)

        assertEquals(2, claims.size)

        assertTrue(
            claims.all {
                it.ownerUserId == 10
            }
        )
    }

    @Test
    fun hasPendingClaim_returnsCorrectResult() = runBlocking {

        repository.insertClaim(
            createClaim(
                itemId = 101,
                claimantUserId = 1,
                status = "PENDING"
            )
        )

        assertTrue(
            repository.hasPendingClaim(
                itemId = 101,
                userId = 1
            )
        )

        assertFalse(
            repository.hasPendingClaim(
                itemId = 101,
                userId = 2
            )
        )

        assertFalse(
            repository.hasPendingClaim(
                itemId = 202,
                userId = 1
            )
        )
    }

    @Test
    fun updateClaimStatus_updatesStatusSuccessfully() = runBlocking {

        val id =
            repository.insertClaim(
                createClaim(
                    status = "PENDING"
                )
            )

        repository.updateClaimStatus(
            claimId = id.toInt(),
            status = "APPROVED"
        )

        val claim =
            repository.getClaimById(id.toInt())

        assertNotNull(claim)
        assertEquals("APPROVED", claim!!.status)
    }

    @Test
    fun rejectOtherPendingClaims_rejectsOtherClaimsForSameItem() =
        runBlocking {

            val approvedId =
                repository.insertClaim(
                    createClaim(
                        itemId = 101,
                        claimantUserId = 1,
                        status = "PENDING"
                    )
                )

            val otherId =
                repository.insertClaim(
                    createClaim(
                        itemId = 101,
                        claimantUserId = 2,
                        status = "PENDING"
                    )
                )

            repository.insertClaim(
                createClaim(
                    itemId = 202,
                    claimantUserId = 3,
                    status = "PENDING"
                )
            )

            repository.rejectOtherPendingClaims(
                itemId = 101,
                approvedClaimId = approvedId.toInt()
            )

            val approvedClaim =
                repository.getClaimById(
                    approvedId.toInt()
                )

            val rejectedClaim =
                repository.getClaimById(
                    otherId.toInt()
                )

            assertEquals(
                "PENDING",
                approvedClaim!!.status
            )

            assertEquals(
                "REJECTED",
                rejectedClaim!!.status
            )
        }
}