package com.example.campuslostfound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ClaimDao {

    @Insert
    suspend fun insertClaim(
        claim: ClaimEntity
    ): Long

    @Query(
        """
        SELECT * FROM claims
        WHERE id = :claimId
        LIMIT 1
        """
    )
    suspend fun getClaimById(
        claimId: Int
    ): ClaimEntity?

    @Query(
        """
        SELECT * FROM claims
        WHERE itemId = :itemId
        ORDER BY id DESC
        """
    )
    suspend fun getClaimsForItem(
        itemId: Int
    ): List<ClaimEntity>

    @Query(
        """
        SELECT * FROM claims
        WHERE claimantUserId = :userId
        ORDER BY id DESC
        """
    )
    suspend fun getMyClaims(
        userId: Int
    ): List<ClaimEntity>

    @Query(
        """
        SELECT * FROM claims
        WHERE ownerUserId = :userId
        ORDER BY id DESC
        """
    )
    suspend fun getClaimsForOwner(
        userId: Int
    ): List<ClaimEntity>

    @Query(
        """
        SELECT COUNT(*) FROM claims
        WHERE itemId = :itemId
        AND claimantUserId = :userId
        AND status = 'PENDING'
        """
    )
    suspend fun hasPendingClaim(
        itemId: Int,
        userId: Int
    ): Int

    @Query(
        """
        UPDATE claims
        SET status = :status
        WHERE id = :claimId
        """
    )
    suspend fun updateClaimStatus(
        claimId: Int,
        status: String
    )

    @Query(
        """
        UPDATE claims
        SET status = 'REJECTED'
        WHERE itemId = :itemId
        AND id != :approvedClaimId
        AND status = 'PENDING'
        """
    )
    suspend fun rejectOtherPendingClaims(
        itemId: Int,
        approvedClaimId: Int
    )

    // =========================================
    // ADMIN DASHBOARD COUNTS
    // =========================================

    @Query(
        """
        SELECT COUNT(*) FROM claims
        WHERE status = 'PENDING'
        """
    )
    suspend fun getPendingClaimsCount(): Int
}