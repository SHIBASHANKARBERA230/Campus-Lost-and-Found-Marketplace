package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.adapter.ItemAdapter
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ItemRepository
import com.example.campuslostfound.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvResult: TextView

    private val repository by lazy {
        ItemRepository(
            AppDatabase.getDatabase(this).itemDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_items)

        recyclerView = findViewById(R.id.recyclerMyItems)
        tvResult = findViewById(R.id.tvMyItemsResult)

        recyclerView.layoutManager = LinearLayoutManager(this)

        loadMyItems()
    }

    private fun loadMyItems() {

        val preferences = getSharedPreferences(
            "user_session",
            MODE_PRIVATE
        )

        val userId = preferences.getInt("userId", 0)

        if (userId == 0) {
            tvResult.text = "Please login first ❌"
            return
        }

        lifecycleScope.launch {

            val databaseItems = withContext(Dispatchers.IO) {
                repository.getItemsByUser(userId)
            }

            val items = databaseItems.map { entity ->

                Item(
                    id = entity.id,
                    userId = entity.userId,
                    name = entity.name,
                    description = entity.description,
                    category = entity.category,
                    type = entity.type,
                    location = entity.location,
                    date = entity.date,
                    status = entity.status
                )
            }

            if (items.isEmpty()) {

                tvResult.text = "You have not posted any items yet 📭"

                recyclerView.adapter =
                    ItemAdapter(emptyList())

            } else {

                tvResult.text =
                    "${items.size} item(s) posted by you"

                recyclerView.adapter =
                    ItemAdapter(items) { item ->

                        val intent = Intent(
                            this@MyItemsActivity,
                            ItemDetailsActivity::class.java
                        )

                        intent.putExtra("itemId", item.id)
                        intent.putExtra("userId", item.userId)
                        intent.putExtra("itemName", item.name)
                        intent.putExtra("description", item.description)
                        intent.putExtra("category", item.category)
                        intent.putExtra("type", item.type)
                        intent.putExtra("location", item.location)
                        intent.putExtra("date", item.date)
                        intent.putExtra("status", item.status)

                        startActivity(intent)
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (::recyclerView.isInitialized) {
            loadMyItems()
        }
    }
}