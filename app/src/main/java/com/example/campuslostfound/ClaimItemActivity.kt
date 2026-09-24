package com.example.campuslostfound

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ClaimEntity
import com.example.campuslostfound.database.ClaimRepository
import com.example.campuslostfound.database.ItemRepository
import com.example.campuslostfound.database.NotificationEntity
import com.example.campuslostfound.database.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClaimItemActivity : AppCompatActivity() {

    private lateinit var etClaimReason: EditText
    private lateinit var etAdditionalDetails: EditText
    private lateinit var btnSubmitClaim: Button

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    private val itemRepository by lazy {
        ItemRepository(database.itemDao())
    }

    private val claimRepository by lazy {
        ClaimRepository(database.claimDao())
    }

    private val notificationRepository by lazy {
        NotificationRepository(database.notificationDao())
    }

    private var itemId: Int = -1
    private var ownerUserId: Int = -1
    private var itemName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_claim_item)

        etClaimReason =
            findViewById(R.id.etClaimReason)

        etAdditionalDetails =
            findViewById(R.id.etAdditionalDetails)

        btnSubmitClaim =
            findViewById(R.id.btnSubmitClaim)

        itemId = intent.getIntExtra(
            "itemId",
            -1
        )

        if (itemId == -1) {

            Toast.makeText(
                this,
                getString(R.string.invalid_item),
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        loadItem()

        btnSubmitClaim.setOnClickListener {
            submitClaim()
        }
    }

    private fun loadItem() {

        lifecycleScope.launch {

            val item =
                withContext(Dispatchers.IO) {
                    itemRepository.getItemById(itemId)
                }

            if (item == null) {

                Toast.makeText(
                    this@ClaimItemActivity,
                    getString(R.string.item_not_found),
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return@launch
            }

            ownerUserId = item.userId
            itemName = item.name
        }
    }

    private fun submitClaim() {

        val reason =
            etClaimReason.text
                .toString()
                .trim()

        val additionalDetails =
            etAdditionalDetails.text
                .toString()
                .trim()

        if (reason.isEmpty()) {

            etClaimReason.error =
                getString(R.string.claim_reason_required)

            etClaimReason.requestFocus()

            return
        }

        if (additionalDetails.isEmpty()) {

            etAdditionalDetails.error =
                getString(
                    R.string.claim_details_required
                )

            etAdditionalDetails.requestFocus()

            return
        }

        val preferences =
            getSharedPreferences(
                "user_session",
                Context.MODE_PRIVATE
            )

        val claimantUserId =
            preferences.getInt(
                "userId",
                -1
            )

        if (claimantUserId == -1) {

            Toast.makeText(
                this,
                getString(R.string.login_again),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (ownerUserId == -1) {

            Toast.makeText(
                this,
                getString(R.string.owner_not_found),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (claimantUserId == ownerUserId) {

            Toast.makeText(
                this,
                getString(R.string.cannot_claim_own_item),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        btnSubmitClaim.isEnabled = false

        lifecycleScope.launch {

            val alreadySubmitted =
                withContext(Dispatchers.IO) {

                    claimRepository.hasPendingClaim(
                        itemId,
                        claimantUserId
                    )
                }

            if (alreadySubmitted) {

                btnSubmitClaim.isEnabled = true

                Toast.makeText(
                    this@ClaimItemActivity,
                    getString(
                        R.string.claim_already_submitted
                    ),
                    Toast.LENGTH_LONG
                ).show()

                return@launch
            }

            val claim =
                ClaimEntity(

                    itemId = itemId,

                    claimantUserId =
                        claimantUserId,

                    ownerUserId =
                        ownerUserId,

                    reason =
                        reason,

                    additionalDetails =
                        additionalDetails,

                    status =
                        "PENDING"
                )

            val claimId =
                withContext(Dispatchers.IO) {

                    claimRepository.insertClaim(
                        claim
                    )
                }

            if (claimId <= 0) {

                btnSubmitClaim.isEnabled = true

                Toast.makeText(
                    this@ClaimItemActivity,
                    getString(
                        R.string.claim_submit_failed
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                return@launch
            }

            val notification =
                NotificationEntity(

                    userId =
                        ownerUserId,

                    title =
                        getString(
                            R.string.new_claim_request
                        ),

                    message =
                        getString(
                            R.string.claim_notification_message,
                            itemName
                        ),

                    itemId =
                        itemId,

                    isRead =
                        false,

                    notificationType =
                        "CLAIM_REQUEST"
                )

            withContext(Dispatchers.IO) {

                notificationRepository
                    .insertNotification(
                        notification
                    )
            }

            Toast.makeText(
                this@ClaimItemActivity,
                getString(
                    R.string.claim_submitted_successfully
                ),
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }
}