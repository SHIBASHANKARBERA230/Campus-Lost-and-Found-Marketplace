package com.example.campuslostfound

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.adapter.AdminActivityLogAdapter
import com.example.campuslostfound.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminActivityLogActivity : AppCompatActivity() {

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    private lateinit var adapter: AdminActivityLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        checkAdmin(currentUserId)
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

    private fun checkAdmin(
        userId: Int
    ) {

        lifecycleScope.launch {

            val admin =
                withContext(Dispatchers.IO) {
                    database.userDao()
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

            adapter.updateActivities(
                activities
            )
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
            loadActivities()
        }
    }
}