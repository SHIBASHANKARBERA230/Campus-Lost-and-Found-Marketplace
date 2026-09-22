package com.example.campuslostfound

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ItemEntity
import com.example.campuslostfound.database.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var btnNotifications: Button

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    private val notificationRepository by lazy {
        NotificationRepository(
            database.notificationDao()
        )
    }

    private val itemDao by lazy {
        database.itemDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // =========================================
        // PROFILE
        // =========================================

        val btnProfile =
            findViewById<Button>(R.id.btnProfile)

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
            findViewById<Button>(R.id.btnLostItems)

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
            findViewById<Button>(R.id.btnFoundItems)

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
            findViewById<Button>(R.id.btnPostItem)

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
            findViewById<Button>(R.id.btnMyItems)

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
            findViewById(R.id.btnNotifications)

        btnNotifications.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    NotificationActivity::class.java
                )
            )
        }

        // =========================================
        // CLAIM REQUESTS
        // =========================================

        val btnClaimRequests =
            findViewById<Button>(R.id.btnClaimRequests)

        btnClaimRequests.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ClaimRequestsActivity::class.java
                )
            )
        }

        // =========================================
        // MY CLAIMS
        // =========================================

        val btnMyClaims =
            findViewById<Button>(R.id.btnMyClaims)

        btnMyClaims.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MyClaimsActivity::class.java
                )
            )
        }

        // =========================================
        // SEARCH
        // =========================================

        val etSearch =
            findViewById<EditText>(R.id.etSearch)

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

                startActivity(intent)

                etSearch.text.clear()
            }

            true
        }

        // =========================================
        // INITIAL DATA LOAD
        // =========================================

        loadUnreadNotificationCount()
        loadRecentItems()
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

        if (userId == 0) {

            btnNotifications.text =
                getString(
                    R.string.notifications
                )

            return
        }

        lifecycleScope.launch {

            val unreadCount =
                withContext(Dispatchers.IO) {

                    notificationRepository
                        .getUnreadCount(userId)
                }

            if (unreadCount > 0) {

                btnNotifications.text =
                    getString(
                        R.string.notifications_with_count,
                        unreadCount
                    )

            } else {

                btnNotifications.text =
                    getString(
                        R.string.notifications
                    )
            }
        }
    }

    // =============================================
    // LOAD RECENT ITEMS
    // =============================================

    private fun loadRecentItems() {

        lifecycleScope.launch {

            val recentItems =
                withContext(Dispatchers.IO) {

                    itemDao.getRecentItems()
                }

            displayRecentItems(
                recentItems
            )
        }
    }

    // =============================================
    // DISPLAY RECENT ITEMS
    // =============================================

    private fun displayRecentItems(
        items: List<ItemEntity>
    ) {

        val recentItemsContainer =
            findViewById<LinearLayout>(
                R.id.recentItemsContainer
            )

        val tvNoRecentItems =
            findViewById<TextView>(
                R.id.tvNoRecentItems
            )

        // Remove previous dynamically created cards.
        recentItemsContainer.removeAllViews()

        // =========================================
        // NO ITEMS
        // =========================================

        if (items.isEmpty()) {

            tvNoRecentItems.visibility =
                View.VISIBLE

            return
        }

        tvNoRecentItems.visibility =
            View.GONE

        // =========================================
        // CREATE RECENT ITEM CARDS
        // =========================================

        for (item in items) {

            val itemCard =
                LinearLayout(this)

            itemCard.orientation =
                LinearLayout.VERTICAL

            itemCard.setPadding(
                16,
                16,
                16,
                16
            )

            itemCard.setBackgroundColor(
                0xFFEEEEEE.toInt()
            )

            val cardParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            cardParams.setMargins(
                0,
                10,
                0,
                0
            )

            itemCard.layoutParams =
                cardParams

            // =====================================
            // ITEM NAME
            // =====================================

            val tvName =
                TextView(this)

            tvName.text =
                "📦 ${item.name}"

            tvName.textSize =
                18f

            tvName.setTypeface(
                null,
                Typeface.BOLD
            )

            itemCard.addView(
                tvName
            )

            // =====================================
            // CATEGORY
            // =====================================

            val tvCategory =
                TextView(this)

            tvCategory.text =
                "Category: ${item.category}"

            tvCategory.textSize =
                14f

            val categoryParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            categoryParams.setMargins(
                0,
                7,
                0,
                0
            )

            tvCategory.layoutParams =
                categoryParams

            itemCard.addView(
                tvCategory
            )

            // =====================================
            // LOCATION
            // =====================================

            val tvLocation =
                TextView(this)

            tvLocation.text =
                "📍 ${item.location}"

            tvLocation.textSize =
                14f

            val locationParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            locationParams.setMargins(
                0,
                5,
                0,
                0
            )

            tvLocation.layoutParams =
                locationParams

            itemCard.addView(
                tvLocation
            )

            // =====================================
            // TYPE
            // =====================================

            val tvType =
                TextView(this)

            tvType.text =
                "Type: ${item.type}"

            tvType.textSize =
                14f

            val typeParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            typeParams.setMargins(
                0,
                5,
                0,
                0
            )

            tvType.layoutParams =
                typeParams

            itemCard.addView(
                tvType
            )

            // =====================================
            // STATUS
            // =====================================

            val tvStatus =
                TextView(this)

            tvStatus.text =
                "Status: ${item.status}"

            tvStatus.textSize =
                14f

            tvStatus.setTypeface(
                null,
                Typeface.BOLD
            )

            val statusParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            statusParams.setMargins(
                0,
                5,
                0,
                0
            )

            tvStatus.layoutParams =
                statusParams

            itemCard.addView(
                tvStatus
            )

            // =====================================
            // OPEN ITEM DETAILS
            // =====================================

            itemCard.setOnClickListener {

                val intent =
                    Intent(
                        this,
                        ItemDetailsActivity::class.java
                    )

                intent.putExtra(
                    "itemId",
                    item.id
                )

                startActivity(
                    intent
                )
            }

            recentItemsContainer.addView(
                itemCard
            )
        }
    }

    // =============================================
    // REFRESH WHEN RETURNING TO MAIN SCREEN
    // =============================================

    override fun onResume() {

        super.onResume()

        if (::btnNotifications.isInitialized) {

            loadUnreadNotificationCount()

            loadRecentItems()
        }
    }
}