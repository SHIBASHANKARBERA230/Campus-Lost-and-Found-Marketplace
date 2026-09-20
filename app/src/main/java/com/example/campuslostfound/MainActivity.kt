package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var btnNotifications: Button

    private val notificationRepository by lazy {
        NotificationRepository(
            AppDatabase
                .getDatabase(this)
                .notificationDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_main
        )


        // =========================================
        // PROFILE
        // =========================================

        val btnProfile =
            findViewById<Button>(
                R.id.btnProfile
            )

        btnProfile.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }


        // =========================================
        // LOST ITEMS
        // =========================================

        val btnLostItems =
            findViewById<Button>(
                R.id.btnLostItems
            )

        btnLostItems.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    LostItemsActivity::class.java
                )
            )
        }


        // =========================================
        // FOUND ITEMS
        // =========================================

        val btnFoundItems =
            findViewById<Button>(
                R.id.btnFoundItems
            )

        btnFoundItems.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    FoundItemsActivity::class.java
                )
            )
        }


        // =========================================
        // POST ITEM
        // =========================================

        val btnPostItem =
            findViewById<Button>(
                R.id.btnPostItem
            )

        btnPostItem.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    PostItemActivity::class.java
                )
            )
        }


        // =========================================
        // MY ITEMS
        // =========================================

        val btnMyItems =
            findViewById<Button>(
                R.id.btnMyItems
            )

        btnMyItems.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MyItemsActivity::class.java
                )
            )
        }


        // =========================================
        // NOTIFICATIONS
        // =========================================

        btnNotifications =
            findViewById(
                R.id.btnNotifications
            )

        btnNotifications.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    NotificationActivity::class.java
                )
            )
        }


        // =========================================
        // SEARCH
        // =========================================

        val etSearch =
            findViewById<EditText>(
                R.id.etSearch
            )

        etSearch.setOnEditorActionListener { _, _, _ ->

            val query =
                etSearch.text
                    .toString()
                    .trim()

            if (query.isNotEmpty()) {

                val intent =
                    Intent(
                        this,
                        SearchActivity::class.java
                    )

                intent.putExtra(
                    "searchQuery",
                    query
                )

                startActivity(
                    intent
                )
            }

            true
        }


        // =========================================
        // LOAD UNREAD NOTIFICATION COUNT
        // =========================================

        loadUnreadNotificationCount()
    }


    // =============================================
    // LOAD UNREAD NOTIFICATION COUNT
    // =============================================

    private fun loadUnreadNotificationCount() {

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


        // User not logged in

        if (userId == 0) {

            btnNotifications.text =
                "🔔 NOTIFICATIONS"

            return
        }


        lifecycleScope.launch {

            val unreadCount =
                withContext(Dispatchers.IO) {

                    notificationRepository
                        .getUnreadCount(
                            userId
                        )
                }


            if (unreadCount > 0) {

                btnNotifications.text =
                    "🔔 NOTIFICATIONS ($unreadCount)"

            } else {

                btnNotifications.text =
                    "🔔 NOTIFICATIONS"
            }
        }
    }


    // =============================================
    // REFRESH WHEN RETURNING TO MAIN SCREEN
    // =============================================

    override fun onResume() {

        super.onResume()

        if (::btnNotifications.isInitialized) {

            loadUnreadNotificationCount()
        }
    }
}