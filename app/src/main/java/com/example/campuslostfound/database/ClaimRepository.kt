package com.example.campuslostfound.database

class ClaimRepository(
    private val claimDao: ClaimDao
) {

    suspend fun insertClaim(
        claim: ClaimEntity
    ): Long {
        return claimDao.insertClaim(
            claim
        )
    }

    suspend fun getClaimById(
        claimId: Int
    ): ClaimEntity? {
        return claimDao.getClaimById(
            claimId
        )
    }

    suspend fun getClaimsForItem(
        itemId: Int
    ): List<ClaimEntity> {
        return claimDao.getClaimsForItem(
            itemId
        )
    }

    suspend fun getMyClaims(
        userId: Int
    ): List<ClaimEntity> {
        return claimDao.getMyClaims(
            userId
        )
    }

    suspend fun getClaimsForOwner(
        userId: Int
    ): List<ClaimEntity> {
        return claimDao.getClaimsForOwner(
            userId
        )
    }

    suspend fun hasPendingClaim(
        itemId: Int,
        userId: Int
    ): Boolean {

        return claimDao.hasPendingClaim(
            itemId,
            userId
        ) > 0
    }

    suspend fun updateClaimStatus(
        claimId: Int,
        status: String
    ) {
        claimDao.updateClaimStatus(
            claimId,
            status
        )
    }

    suspend fun rejectOtherPendingClaims(
        itemId: Int,
        approvedClaimId: Int
    ) {
        claimDao.rejectOtherPendingClaims(
            itemId,
            approvedClaimId
        )
    }
}