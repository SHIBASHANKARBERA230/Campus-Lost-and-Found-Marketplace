package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminDashboardActivity : AppCompatActivity() {

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_admin_dashboard
        )

        val preferences =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        val currentUserId =
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

        setupQuickActions()

        checkAdmin(currentUserId)
    }

    private fun setupQuickActions() {

        // MANAGE REPORTS
        findViewById<Button>(
            R.id.btnManageReports
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AdminReportsActivity::class.java
                )
            )
        }

        // MANAGE CLAIMS
        findViewById<Button>(
            R.id.btnManageClaims
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ClaimRequestsActivity::class.java
                )
            )
        }

        // MANAGE USERS
        findViewById<Button>(
            R.id.btnManageUsers
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AdminUsersActivity::class.java
                )
            )
        }

        // ACTIVITY LOG
        findViewById<Button>(
            R.id.btnActivityLog
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AdminActivityLogActivity::class.java
                )
            )
        }

        // PENDING REPORTS
        findViewById<TextView>(
            R.id.tvPendingReports
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AdminReportsActivity::class.java
                )
            )
        }

        // PENDING CLAIMS
        findViewById<TextView>(
            R.id.tvPendingClaims
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ClaimRequestsActivity::class.java
                )
            )
        }

        // REFRESH DASHBOARD
        findViewById<Button>(
            R.id.btnRefreshDashboard
        ).setOnClickListener {

            loadDashboard()

            Toast.makeText(
                this,
                "Dashboard refreshed ✅",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkAdmin(
        userId: Int
    ) {

        lifecycleScope.launch {

            val admin =
                withContext(Dispatchers.IO) {

                    database
                        .userDao()
                        .getAdminById(userId)
                }

            if (admin == null) {

                Toast.makeText(
                    this@AdminDashboardActivity,
                    "Admin access required",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

                return@launch
            }

            loadDashboard()
        }
    }

    private fun loadDashboard() {

        lifecycleScope.launch {

            val data =
                withContext(Dispatchers.IO) {

                    val totalUsers =
                        database
                            .userDao()
                            .getTotalUsers()

                    val activeUsers =
                        database
                            .userDao()
                            .getActiveUsers()

                    val inactiveUsers =
                        database
                            .userDao()
                            .getInactiveUsers()

                    val adminUsers =
                        database
                            .userDao()
                            .getAdminUsers()

                    val totalItems =
                        database
                            .itemDao()
                            .getTotalItems()

                    val lostItems =
                        database
                            .itemDao()
                            .getTotalLostItems()

                    val foundItems =
                        database
                            .itemDao()
                            .getTotalFoundItems()

                    val pendingReports =
                        database
                            .reportDao()
                            .getPendingReportsCount()

                    val pendingClaims =
                        database
                            .claimDao()
                            .getPendingClaimsCount()

                    DashboardData(
                        totalUsers,
                        activeUsers,
                        inactiveUsers,
                        adminUsers,
                        totalItems,
                        lostItems,
                        foundItems,
                        pendingReports,
                        pendingClaims
                    )
                }

            findViewById<TextView>(
                R.id.tvTotalUsers
            ).text =
                "👥 Total Users: ${data.totalUsers}"

            findViewById<TextView>(
                R.id.tvActiveUsers
            ).text =
                "🟢 Active Users: ${data.activeUsers}"

            findViewById<TextView>(
                R.id.tvInactiveUsers
            ).text =
                "🔴 Inactive Users: ${data.inactiveUsers}"

            findViewById<TextView>(
                R.id.tvAdminUsers
            ).text =
                "👑 Administrators: ${data.adminUsers}"

            findViewById<TextView>(
                R.id.tvTotalItems
            ).text =
                "📦 Total Items: ${data.totalItems}"

            findViewById<TextView>(
                R.id.tvLostItems
            ).text =
                "🔴 Lost Items: ${data.lostItems}"

            findViewById<TextView>(
                R.id.tvFoundItems
            ).text =
                "🟢 Found Items: ${data.foundItems}"

            findViewById<TextView>(
                R.id.tvPendingReports
            ).text =
                "🚩 Pending Reports: ${data.pendingReports}"

            findViewById<TextView>(
                R.id.tvPendingClaims
            ).text =
                "⚠️ Pending Claims: ${data.pendingClaims}"

            val currentTime =
                SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
                ).format(Date())

            findViewById<TextView>(
                R.id.tvLastUpdated
            ).text =
                "Last updated: $currentTime"
        }
    }

    override fun onResume() {

        super.onResume()

        val preferences =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        val userId =
            preferences.getInt(
                "userId",
                0
            )

        if (userId != 0) {
            loadDashboard()
        }
    }

    private data class DashboardData(
        val totalUsers: Int,
        val activeUsers: Int,
        val inactiveUsers: Int,
        val adminUsers: Int,
        val totalItems: Int,
        val lostItems: Int,
        val foundItems: Int,
        val pendingReports: Int,
        val pendingClaims: Int
    )
}