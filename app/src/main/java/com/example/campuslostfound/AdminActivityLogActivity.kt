package com.example.campuslostfound

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.adapter.AdminActivityLogAdapter
import com.example.campuslostfound.database.AdminActivityEntity
import com.example.campuslostfound.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminActivityLogActivity : AppCompatActivity() {

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    private lateinit var adapter: AdminActivityLogAdapter

    private var allActivities =
        emptyList<AdminActivityEntity>()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        /*
         * Set the status bar color to match
         * the Activity Log background.
         */
        window.statusBarColor =
            Color.rgb(
                245,
                245,
                245
            )

        setContentView(
            R.layout.activity_admin_activity_log
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

        setupRecyclerView()

        setupFilterButtons()

        checkAdmin(
            currentUserId
        )
    }

    private fun setupRecyclerView() {

        val recyclerView =
            findViewById<RecyclerView>(
                R.id.recyclerActivityLog
            )

        adapter =
            AdminActivityLogAdapter(
                emptyList()
            )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            adapter
    }

    private fun setupFilterButtons() {

        findViewById<Button>(
            R.id.btnFilterAll
        ).setOnClickListener {

            showAllActivities()
        }

        findViewById<Button>(
            R.id.btnFilterUsers
        ).setOnClickListener {

            showUserActivities()
        }

        findViewById<Button>(
            R.id.btnFilterReports
        ).setOnClickListener {

            showReportActivities()
        }

        findViewById<Button>(
            R.id.btnFilterItems
        ).setOnClickListener {

            showItemActivities()
        }

        findViewById<Button>(
            R.id.btnFilterSecurity
        ).setOnClickListener {

            showSecurityActivities()
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
                    this@AdminActivityLogActivity,
                    "Admin access required",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

                return@launch
            }

            loadActivities()
        }
    }

    private fun loadActivities() {

        lifecycleScope.launch {

            val activities =
                withContext(Dispatchers.IO) {

                    database
                        .adminActivityDao()
                        .getAllActivities()
                }

            allActivities =
                activities

            showAllActivities()
        }
    }

    private fun showAllActivities() {

        adapter.updateActivities(
            allActivities
        )
    }

    private fun showUserActivities() {

        val filteredActivities =
            allActivities.filter {

                when (it.action) {

                    "MAKE_ADMIN",
                    "REMOVE_ADMIN",
                    "DEACTIVATE_USER",
                    "ACTIVATE_USER" -> true

                    else -> false
                }
            }

        adapter.updateActivities(
            filteredActivities
        )
    }

    private fun showReportActivities() {

        val filteredActivities =
            allActivities.filter {

                when (it.action) {

                    "DISMISS_REPORT",
                    "REMOVE_REPORTED_ITEM" -> true

                    else -> false
                }
            }

        adapter.updateActivities(
            filteredActivities
        )
    }

    private fun showItemActivities() {

        val filteredActivities =
            allActivities.filter {

                when (it.action) {

                    "REMOVE_REPORTED_ITEM" -> true

                    else -> false
                }
            }

        adapter.updateActivities(
            filteredActivities
        )
    }

    private fun showSecurityActivities() {

        val filteredActivities =
            allActivities.filter {

                when (it.action) {

                    "RESET_PASSWORD" -> true

                    else -> false
                }
            }

        adapter.updateActivities(
            filteredActivities
        )
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

            loadActivities()
        }
    }
}