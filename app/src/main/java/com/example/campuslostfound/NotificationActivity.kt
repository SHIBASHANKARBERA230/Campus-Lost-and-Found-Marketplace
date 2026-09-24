package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.adapter.NotificationAdapter
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.NotificationEntity
import com.example.campuslostfound.database.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var tvResult: TextView

    private lateinit var btnMarkAllRead: Button

    private val repository by lazy {

        NotificationRepository(
            AppDatabase
                .getDatabase(this)
                .notificationDao()
        )
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_notifications
        )

        recyclerView =
            findViewById(
                R.id.recyclerNotifications
            )

        tvResult =
            findViewById(
                R.id.tvNotificationResult
            )

        btnMarkAllRead =
            findViewById(
                R.id.btnMarkAllRead
            )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        btnMarkAllRead.setOnClickListener {

            markAllAsRead()
        }

        loadNotifications()
    }

    private fun loadNotifications() {

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

        if (userId == 0) {

            tvResult.text =
                "Please login first ❌"

            return
        }

        lifecycleScope.launch {

            val notifications:
                    List<NotificationEntity> =

                withContext(Dispatchers.IO) {

                    repository.getNotifications(
                        userId
                    )
                }

            displayNotifications(
                notifications
            )
        }
    }

    private fun displayNotifications(
        notifications: List<NotificationEntity>
    ) {

        if (notifications.isEmpty()) {

            tvResult.text =
                "No notifications 🔔"

            recyclerView.adapter =
                NotificationAdapter(
                    emptyList()
                ) {
                    // No notification
                }

            return
        }

        tvResult.text =
            "${notifications.size} notification(s)"

        recyclerView.adapter =
            NotificationAdapter(
                notifications
            ) { notification ->

                // Mark notification as read
                markNotificationAsRead(
                    notification.id
                )

                openNotification(
                    notification
                )
            }
    }

    private fun openNotification(
        notification: NotificationEntity
    ) {

        when (notification.notificationType) {

            // ---------------------------------
            // ITEM MATCH
            // ---------------------------------

            "ITEM_MATCH" -> {

                val intent = Intent(
                    this,
                    ItemDetailsActivity::class.java
                )

                intent.putExtra(
                    "itemId",
                    notification.itemId
                )

                startActivity(intent)
            }

            // ---------------------------------
            // CLAIM REQUEST
            // ---------------------------------

            "CLAIM_REQUEST" -> {

                val intent = Intent(
                    this,
                    ClaimRequestsActivity::class.java
                )

                startActivity(intent)
            }

            // ---------------------------------
            // CLAIM APPROVED
            // ---------------------------------

            "CLAIM_APPROVED" -> {

                val intent = Intent(
                    this,
                    ItemDetailsActivity::class.java
                )

                intent.putExtra(
                    "itemId",
                    notification.itemId
                )

                startActivity(intent)
            }

            // ---------------------------------
            // CLAIM REJECTED
            // ---------------------------------

            "CLAIM_REJECTED" -> {

                val intent = Intent(
                    this,
                    ItemDetailsActivity::class.java
                )

                intent.putExtra(
                    "itemId",
                    notification.itemId
                )

                startActivity(intent)
            }

            // ---------------------------------
            // UNKNOWN NOTIFICATION
            // ---------------------------------

            else -> {

                val intent = Intent(
                    this,
                    ItemDetailsActivity::class.java
                )

                intent.putExtra(
                    "itemId",
                    notification.itemId
                )

                startActivity(intent)
            }
        }
    }

    private fun markNotificationAsRead(
        notificationId: Int
    ) {

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {

                repository.markAsRead(
                    notificationId
                )
            }

            loadNotifications()
        }
    }

    private fun markAllAsRead() {

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

        if (userId == 0) {
            return
        }

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {

                repository.markAllAsRead(
                    userId
                )
            }

            loadNotifications()
        }
    }

    override fun onResume() {

        super.onResume()

        if (::recyclerView.isInitialized) {

            loadNotifications()
        }
    }
}