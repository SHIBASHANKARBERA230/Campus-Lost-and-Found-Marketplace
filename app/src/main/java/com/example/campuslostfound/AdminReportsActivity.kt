package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AdminActivityRepository
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ReportEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminReportsActivity : AppCompatActivity() {

    private lateinit var reportsContainer: LinearLayout

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    private val activityRepository by lazy {
        AdminActivityRepository(
            database.adminActivityDao()
        )
    }

    private var currentUserId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_admin_reports
        )

        reportsContainer =
            findViewById(
                R.id.reportsContainer
            )

        val preferences =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        currentUserId =
            preferences.getInt(
                "userId",
                0
            )

        if (currentUserId == 0) {

            Toast.makeText(
                this,
                "Please login first",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        checkAdmin()
    }

    private fun checkAdmin() {

        lifecycleScope.launch {

            val admin =
                withContext(Dispatchers.IO) {

                    database
                        .userDao()
                        .getAdminById(
                            currentUserId
                        )
                }

            if (admin == null) {

                Toast.makeText(
                    this@AdminReportsActivity,
                    "Admin access required",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

                return@launch
            }

            loadReports()
        }
    }

    private fun loadReports() {

        lifecycleScope.launch {

            val reports =
                withContext(Dispatchers.IO) {

                    database
                        .reportDao()
                        .getPendingReports()
                }

            displayReports(reports)
        }
    }

    private fun displayReports(
        reports: List<ReportEntity>
    ) {

        reportsContainer.removeAllViews()

        if (reports.isEmpty()) {

            val emptyText =
                TextView(this)

            emptyText.text =
                "No pending reports 🎉"

            emptyText.textSize =
                18f

            emptyText.setPadding(
                16,
                32,
                16,
                32
            )

            reportsContainer.addView(
                emptyText
            )

            return
        }

        for (report in reports) {

            addReportCard(report)
        }
    }

    private fun addReportCard(
        report: ReportEntity
    ) {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            20,
            20,
            20,
            20
        )

        val title =
            TextView(this)

        title.text =
            "🚩 Report #${report.id}"

        title.textSize =
            20f

        title.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        val reason =
            TextView(this)

        reason.text =
            "Reason: ${report.reason}"

        reason.textSize =
            16f

        val details =
            TextView(this)

        details.text =
            "Details: ${report.details}"

        details.textSize =
            15f

        val itemId =
            TextView(this)

        itemId.text =
            "Item ID: ${report.itemId}"

        itemId.textSize =
            14f

        // VIEW ITEM

        val viewButton =
            Button(this)

        viewButton.text =
            "VIEW ITEM"

        viewButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    ItemDetailsActivity::class.java
                )

            intent.putExtra(
                "itemId",
                report.itemId
            )

            startActivity(intent)
        }

        // DISMISS REPORT

        val dismissButton =
            Button(this)

        dismissButton.text =
            "DISMISS REPORT"

        dismissButton.setOnClickListener {

            updateReportStatus(
                report,
                "DISMISSED"
            )
        }

        // REMOVE REPORTED ITEM

        val removeButton =
            Button(this)

        removeButton.text =
            "REMOVE REPORTED ITEM"

        removeButton.setOnClickListener {

            removeReportedItem(report)
        }

        card.addView(title)
        card.addView(reason)
        card.addView(details)
        card.addView(itemId)
        card.addView(viewButton)
        card.addView(dismissButton)
        card.addView(removeButton)

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.setMargins(
            0,
            0,
            0,
            24
        )

        card.layoutParams =
            params

        reportsContainer.addView(
            card
        )
    }

    // ============================================
    // UPDATE REPORT STATUS
    // ============================================

    private fun updateReportStatus(
        report: ReportEntity,
        status: String
    ) {

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {

                database
                    .reportDao()
                    .updateReportStatus(
                        report.id,
                        status
                    )
            }

            // LOG ADMIN ACTION

            if (status == "DISMISSED") {

                logAdminActivity(
                    action = "DISMISS_REPORT",
                    targetUserId = report.reporterUserId,
                    targetItemId = report.itemId,
                    details =
                        "Administrator dismissed report #${report.id} for item ${report.itemId}"
                )
            }

            Toast.makeText(
                this@AdminReportsActivity,
                "Report dismissed",
                Toast.LENGTH_SHORT
            ).show()

            loadReports()
        }
    }

    // ============================================
    // REMOVE REPORTED ITEM
    // ============================================

    private fun removeReportedItem(
        report: ReportEntity
    ) {

        lifecycleScope.launch {

            val deleted =
                withContext(Dispatchers.IO) {

                    database
                        .itemDao()
                        .deleteItemById(
                            report.itemId
                        )
                }

            if (deleted > 0) {

                withContext(Dispatchers.IO) {

                    database
                        .reportDao()
                        .updateReportStatus(
                            report.id,
                            "RESOLVED"
                        )
                }

                // LOG ADMIN ACTION

                logAdminActivity(
                    action = "REMOVE_REPORTED_ITEM",
                    targetUserId = report.reporterUserId,
                    targetItemId = report.itemId,
                    details =
                        "Administrator removed reported item ${report.itemId} from report #${report.id}"
                )

                Toast.makeText(
                    this@AdminReportsActivity,
                    "Reported item removed",
                    Toast.LENGTH_SHORT
                ).show()

                loadReports()

            } else {

                Toast.makeText(
                    this@AdminReportsActivity,
                    "Item no longer exists",
                    Toast.LENGTH_SHORT
                ).show()

                updateReportStatus(
                    report,
                    "RESOLVED"
                )
            }
        }
    }

    // ============================================
    // ADMIN ACTIVITY LOG
    // ============================================

    private fun logAdminActivity(
        action: String,
        targetUserId: Int? = null,
        targetItemId: Int? = null,
        details: String
    ) {

        lifecycleScope.launch {

            if (currentUserId == 0) {
                return@launch
            }

            withContext(Dispatchers.IO) {

                activityRepository.logActivity(
                    adminUserId = currentUserId,
                    action = action,
                    targetUserId = targetUserId,
                    targetItemId = targetItemId,
                    details = details
                )
            }
        }
    }

    override fun onResume() {

        super.onResume()

        if (currentUserId != 0) {

            loadReports()
        }
    }
}