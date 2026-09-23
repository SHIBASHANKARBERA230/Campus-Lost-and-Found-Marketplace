package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.adapter.ItemAdapter
import com.example.campuslostfound.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_favorite_items)

        recyclerView =
            findViewById(R.id.recyclerFavoriteItems)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        loadFavorites()
    }

    private fun loadFavorites() {

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
            Toast.makeText(
                this,
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        lifecycleScope.launch {

            val favoriteIds =
                withContext(Dispatchers.IO) {
                    database
                        .favoriteDao()
                        .getFavoriteItemIds(userId)
                }

            val items =
                withContext(Dispatchers.IO) {

                    favoriteIds.mapNotNull { itemId ->
                        database
                            .itemDao()
                            .getItemById(itemId)
                    }
                }

            if (items.isEmpty()) {
                Toast.makeText(
                    this@FavoriteItemsActivity,
                    "No favorite items yet",
                    Toast.LENGTH_SHORT
                ).show()
            }

            recyclerView.adapter =
                ItemAdapter(
                    items
                ) { item ->

                    val intent =
                        Intent(
                            this@FavoriteItemsActivity,
                            ItemDetailsActivity::class.java
                        )

                    intent.putExtra(
                        "itemId",
                        item.id
                    )

                    startActivity(intent)
                }
        }
    }

    override fun onResume() {
        super.onResume()

        if (::recyclerView.isInitialized) {
            loadFavorites()
        }
    }
}