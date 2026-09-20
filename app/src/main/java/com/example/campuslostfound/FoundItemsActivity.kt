package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.adapter.ItemAdapter
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ItemEntity
import com.example.campuslostfound.database.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoundItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvResult: TextView
    private lateinit var spinnerCategory: Spinner

    private val repository by lazy {
        ItemRepository(
            AppDatabase.getDatabase(this).itemDao()
        )
    }

    private val categories = arrayOf(
        "All Categories",
        "Electronics",
        "Books",
        "Bags",
        "Keys",
        "Clothing",
        "ID / Cards",
        "Documents",
        "Accessories",
        "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_found_items)

        recyclerView =
            findViewById(R.id.recyclerFoundItems)

        tvResult =
            findViewById(R.id.tvFoundItemsResult)

        spinnerCategory =
            findViewById(R.id.spinnerCategory)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        setupCategorySpinner()

        loadFoundItems("All Categories")
    }

    private fun setupCategorySpinner() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerCategory.adapter = adapter

        spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val selectedCategory =
                        categories[position]

                    loadFoundItems(selectedCategory)
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun loadFoundItems(category: String) {

        lifecycleScope.launch {

            val databaseItems: List<ItemEntity> =
                withContext(Dispatchers.IO) {

                    if (category == "All Categories") {

                        repository.getFoundItems()

                    } else {

                        repository.getItemsByTypeAndCategory(
                            "FOUND",
                            category
                        )
                    }
                }

            if (databaseItems.isEmpty()) {

                tvResult.text =
                    if (category == "All Categories") {
                        "No found items available 📭"
                    } else {
                        "No found items available in $category 📭"
                    }

                recyclerView.adapter =
                    ItemAdapter(emptyList()) {
                        // No item to open
                    }

            } else {

                tvResult.text =
                    "${databaseItems.size} found item(s) available"

                recyclerView.adapter =
                    ItemAdapter(databaseItems) { item ->

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

                        intent.putExtra(
                            "imageUri",
                            item.imageUri
                        )

                        startActivity(intent)
                    }
            }
        }
    }
}