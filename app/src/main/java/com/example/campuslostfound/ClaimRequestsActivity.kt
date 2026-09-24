package com.example.campuslostfound

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.adapter.ClaimAdapter
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ClaimEntity
import com.example.campuslostfound.database.ClaimRepository
import com.example.campuslostfound.database.NotificationEntity
import com.example.campuslostfound.database.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClaimRequestsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    private val claimRepository by lazy {
        ClaimRepository(database.claimDao())
    }

    private val notificationRepository by lazy {
        NotificationRepository(database.notificationDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_claim_requests)

        recyclerView =
            findViewById(R.id.recyclerClaimRequests)

        tvEmpty =
            findViewById(R.id.tvEmptyClaims)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        loadClaims()
    }

    override fun onResume() {
        super.onResume()
        loadClaims()
    }

    private fun loadClaims() {

        val preferences =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        val userId =
            preferences.getInt(
                "userId",
                -1
            )

        if (userId == -1) {

            Toast.makeText(
                this,
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        lifecycleScope.launch {

            val claims =
                withContext(Dispatchers.IO) {
                    claimRepository
                        .getClaimsForOwner(userId)
                }

            if (claims.isEmpty()) {

                tvEmpty.text =
                    "No claim requests yet"

                tvEmpty.visibility =
                    TextView.VISIBLE

                recyclerView.visibility =
                    RecyclerView.GONE

            } else {

                tvEmpty.visibility =
                    TextView.GONE

                recyclerView.visibility =
                    RecyclerView.VISIBLE

                recyclerView.adapter =
                    ClaimAdapter(
                        claims = claims,

                        onApprove = { claim ->
                            approveClaim(claim)
                        },

                        onReject = { claim ->
                            rejectClaim(claim)
                        }
                    )
            }
        }
    }

    private fun approveClaim(
        claim: ClaimEntity
    ) {

        if (claim.status != "PENDING") {
            Toast.makeText(
                this,
                "This claim has already been processed",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        lifecycleScope.launch {

            val result =
                withContext(Dispatchers.IO) {

                    val currentClaim =
                        claimRepository
                            .getClaimById(claim.id)

                    if (
                        currentClaim == null ||
                        currentClaim.status != "PENDING"
                    ) {
                        return@withContext false
                    }

                    /*
                     * Get all pending claims for this item
                     * BEFORE rejecting the other claims.
                     */
                    val allClaims =
                        claimRepository
                            .getClaimsForItem(
                                claim.itemId
                            )

                    val otherPendingClaims =
                        allClaims.filter {
                            it.status == "PENDING" &&
                                    it.id != claim.id
                        }

                    /*
                     * Approve selected claim.
                     */
                    claimRepository
                        .updateClaimStatus(
                            claim.id,
                            "APPROVED"
                        )

                    /*
                     * Reject all other pending claims.
                     */
                    claimRepository
                        .rejectOtherPendingClaims(
                            itemId = claim.itemId,
                            approvedClaimId = claim.id
                        )

                    /*
                     * Notify approved claimant.
                     */
                    notificationRepository
                        .insertNotification(
                            NotificationEntity(
                                userId = claim.claimantUserId,
                                title = getString(
                                    R.string.claim_approved_title
                                ),
                                message = getString(
                                    R.string.claim_approved_message
                                ),
                                itemId = claim.itemId,
                                isRead = false,
                                notificationType =
                                    "CLAIM_APPROVED"
                            )
                        )

                    /*
                     * Notify every claimant whose
                     * pending claim was automatically rejected.
                     */
                    for (rejectedClaim in otherPendingClaims) {

                        notificationRepository
                            .insertNotification(
                                NotificationEntity(
                                    userId =
                                        rejectedClaim.claimantUserId,

                                    title = getString(
                                        R.string.claim_rejected_title
                                    ),

                                    message = getString(
                                        R.string.claim_rejected_message
                                    ),

                                    itemId =
                                        rejectedClaim.itemId,

                                    isRead = false,

                                    notificationType =
                                        "CLAIM_REJECTED"
                                )
                            )
                    }

                    true
                }

            if (result) {

                Toast.makeText(
                    this@ClaimRequestsActivity,
                    "Claim approved",
                    Toast.LENGTH_SHORT
                ).show()

                loadClaims()
            }
        }
    }

    private fun rejectClaim(
        claim: ClaimEntity
    ) {

        if (claim.status != "PENDING") {

            Toast.makeText(
                this,
                "This claim has already been processed",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        lifecycleScope.launch {

            val success =
                withContext(Dispatchers.IO) {

                    val currentClaim =
                        claimRepository
                            .getClaimById(claim.id)

                    if (
                        currentClaim == null ||
                        currentClaim.status != "PENDING"
                    ) {
                        return@withContext false
                    }

                    /*
                     * Change claim status.
                     */
                    claimRepository
                        .updateClaimStatus(
                            claim.id,
                            "REJECTED"
                        )

                    /*
                     * Notify claimant.
                     */
                    notificationRepository
                        .insertNotification(
                            NotificationEntity(
                                userId =
                                    claim.claimantUserId,

                                title = getString(
                                    R.string.claim_rejected_title
                                ),

                                message = getString(
                                    R.string.claim_rejected_message
                                ),

                                itemId =
                                    claim.itemId,

                                isRead = false,

                                notificationType =
                                    "CLAIM_REJECTED"
                            )
                        )

                    true
                }

            if (success) {

                Toast.makeText(
                    this@ClaimRequestsActivity,
                    "Claim rejected",
                    Toast.LENGTH_SHORT
                ).show()

                loadClaims()
            }
        }
    }
}