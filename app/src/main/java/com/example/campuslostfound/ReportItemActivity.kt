package com.example.campuslostfound

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ReportEntity
import com.example.campuslostfound.database.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportItemActivity : AppCompatActivity() {

    private lateinit var spinnerReportReason: Spinner
    private lateinit var etReportDetails: EditText
    private lateinit var btnSubmitReport: Button

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    private val reportRepository by lazy {
        ReportRepository(
            database.reportDao()
        )
    }

    private var itemId: Int = 0
    private var ownerUserId: Int = 0
    private var currentUserId: Int = 0

    private val reportReasons = arrayOf(
        "Fake / Incorrect item",
        "Inappropriate content",
        "Duplicate item",
        "Wrong information",
        "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_report_item)

        spinnerReportReason =
            findViewById(R.id.spinnerReportReason)

        etReportDetails =
            findViewById(R.id.etReportDetails)

        btnSubmitReport =
            findViewById(R.id.btnSubmitReport)

        itemId = intent.getIntExtra(
            "itemId",
            0
        )

        ownerUserId = intent.getIntExtra(
            "ownerUserId",
            0
        )

        val preferences = getSharedPreferences(
            "user_session",
            MODE_PRIVATE
        )

        currentUserId = preferences.getInt(
            "userId",
            0
        )

        if (itemId == 0) {
            Toast.makeText(
                this,
                "Invalid item ❌",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        if (currentUserId == 0) {
            Toast.makeText(
                this,
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        if (
            ownerUserId != 0 &&
            ownerUserId == currentUserId
        ) {
            Toast.makeText(
                this,
                "You cannot report your own item",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setupReasonSpinner()

        btnSubmitReport.setOnClickListener {
            submitReport()
        }
    }

    private fun setupReasonSpinner() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            reportReasons
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerReportReason.adapter = adapter
    }

    private fun submitReport() {

        val reason =
            spinnerReportReason
                .selectedItem
                ?.toString()
                ?.trim()
                ?: ""

        val details =
            etReportDetails
                .text
                .toString()
                .trim()

        if (reason.isEmpty()) {
            Toast.makeText(
                this,
                "Please select a reason",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (details.isEmpty()) {
            Toast.makeText(
                this,
                "Please provide additional details",
                Toast.LENGTH_SHORT
            ).show()

            etReportDetails.requestFocus()

            return
        }

        if (details.length < 5) {
            Toast.makeText(
                this,
                "Please provide more details",
                Toast.LENGTH_SHORT
            ).show()

            etReportDetails.requestFocus()

            return
        }

        btnSubmitReport.isEnabled = false

        lifecycleScope.launch {

            val existingReport =
                withContext(Dispatchers.IO) {

                    reportRepository.getExistingReport(
                        itemId = itemId,
                        userId = currentUserId
                    )
                }

            if (existingReport != null) {

                Toast.makeText(
                    this@ReportItemActivity,
                    "You have already reported this item",
                    Toast.LENGTH_LONG
                ).show()

                btnSubmitReport.isEnabled = true

                return@launch
            }

            val item =
                withContext(Dispatchers.IO) {

                    database.itemDao()
                        .getItemById(itemId)
                }

            if (item == null) {

                Toast.makeText(
                    this@ReportItemActivity,
                    "Item not found ❌",
                    Toast.LENGTH_SHORT
                ).show()

                btnSubmitReport.isEnabled = true

                return@launch
            }

            if (item.userId == currentUserId) {

                Toast.makeText(
                    this@ReportItemActivity,
                    "You cannot report your own item",
                    Toast.LENGTH_SHORT
                ).show()

                btnSubmitReport.isEnabled = true

                return@launch
            }

            val report = ReportEntity(

                itemId = itemId,

                reporterUserId =
                    currentUserId,

                ownerUserId =
                    item.userId,

                reason =
                    reason,

                details =
                    details,

                status =
                    "PENDING"
            )

            val result =
                withContext(Dispatchers.IO) {

                    reportRepository.submitReport(
                        report
                    )
                }

            if (result > 0) {

                Toast.makeText(
                    this@ReportItemActivity,
                    "Report submitted successfully 🚩",
                    Toast.LENGTH_LONG
                ).show()

                finish()

            } else {

                Toast.makeText(
                    this@ReportItemActivity,
                    "Failed to submit report ❌",
                    Toast.LENGTH_SHORT
                ).show()

                btnSubmitReport.isEnabled = true
            }
        }
    }
}