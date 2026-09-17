package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.adapter.ItemAdapter
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ItemRepository
import com.example.campuslostfound.model.Item
import com.example.campuslostfound.viewmodel.ItemViewModel
import com.example.campuslostfound.viewmodel.ItemViewModelFactory
import kotlinx.coroutines.launch

class FoundItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private val viewModel: ItemViewModel by viewModels {
        ItemViewModelFactory(
            ItemRepository(
                AppDatabase.getDatabase(this).itemDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_found_items)

        recyclerView =
            findViewById(R.id.recyclerFoundItems)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        observeFoundItems()

        viewModel.loadFoundItems()
    }

    private fun observeFoundItems() {

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.foundItems.collect { databaseItems ->

                    val items =
                        databaseItems.map { entity ->

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

                    recyclerView.adapter =
                        ItemAdapter(items) { item ->

                            val intent =
                                Intent(
                                    this@FoundItemsActivity,
                                    ItemDetailsActivity::class.java
                                )

                            intent.putExtra(
                                "itemId",
                                item.id
                            )

                            intent.putExtra(
                                "userId",
                                item.userId
                            )

                            intent.putExtra(
                                "itemName",
                                item.name
                            )

                            intent.putExtra(
                                "description",
                                item.description
                            )

                            intent.putExtra(
                                "category",
                                item.category
                            )

                            intent.putExtra(
                                "type",
                                item.type
                            )

                            intent.putExtra(
                                "location",
                                item.location
                            )

                            intent.putExtra(
                                "date",
                                item.date
                            )

                            intent.putExtra(
                                "status",
                                item.status
                            )

                            startActivity(intent)
                        }
                }
            }
        }
    }
}