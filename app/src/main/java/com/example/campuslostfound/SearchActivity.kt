package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.adapter.ItemAdapter
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ItemRepository
import com.example.campuslostfound.model.Item
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var searchBox: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvResult: TextView

    private lateinit var btnAll: Button
    private lateinit var btnLost: Button
    private lateinit var btnFound: Button

    private val repository by lazy {
        ItemRepository(
            AppDatabase.getDatabase(this).itemDao()
        )
    }

    private var searchJob: Job? = null

    // Current filter
    private var currentFilter = "ALL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        searchBox = findViewById(R.id.etSearchItems)

        recyclerView =
            findViewById(R.id.recyclerSearchResults)

        tvResult =
            findViewById(R.id.tvSearchResult)

        btnAll =
            findViewById(R.id.btnAll)

        btnLost =
            findViewById(R.id.btnLost)

        btnFound =
            findViewById(R.id.btnFound)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        // =========================
        // ALL BUTTON
        // =========================

        btnAll.setOnClickListener {

            currentFilter = "ALL"

            searchItems(
                searchBox.text.toString().trim()
            )
        }

        // =========================
        // LOST BUTTON
        // =========================

        btnLost.setOnClickListener {

            currentFilter = "LOST"

            searchItems(
                searchBox.text.toString().trim()
            )
        }

        // =========================
        // FOUND BUTTON
        // =========================

        btnFound.setOnClickListener {

            currentFilter = "FOUND"

            searchItems(
                searchBox.text.toString().trim()
            )
        }

        // =========================
        // LIVE SEARCH
        // =========================

        searchBox.addTextChangedListener(
            object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    searchJob?.cancel()

                    searchJob =
                        lifecycleScope.launch {

                            delay(300)

                            searchItems(
                                s.toString().trim()
                            )
                        }
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {
                }
            }
        )

        // Show all items initially
        searchItems("")
    }

    // =========================
    // SEARCH ITEMS
    // =========================

    private fun searchItems(query: String) {

        searchJob?.cancel()

        searchJob =
            lifecycleScope.launch {

                val databaseItems =
                    if (query.isEmpty()) {

                        repository.getAllItems()

                    } else {

                        repository.searchItems(query)
                    }

                // Apply LOST / FOUND filter
                val filteredItems =
                    when (currentFilter) {

                        "LOST" ->
                            databaseItems.filter {
                                it.type.equals(
                                    "LOST",
                                    ignoreCase = true
                                )
                            }

                        "FOUND" ->
                            databaseItems.filter {
                                it.type.equals(
                                    "FOUND",
                                    ignoreCase = true
                                )
                            }

                        else ->
                            databaseItems
                    }

                // Convert Entity → Model
                val items =
                    filteredItems.map { entity ->

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

                // Result text
                tvResult.text =
                    when {

                        items.isEmpty() ->
                            "No items found ❌"

                        currentFilter == "ALL" ->
                            "${items.size} item(s) found"

                        else ->
                            "${items.size} $currentFilter item(s) found"
                    }

                // RecyclerView
                recyclerView.adapter =
                    ItemAdapter(items) { item ->

                        openItemDetails(item)
                    }
            }
    }

    // =========================
    // OPEN ITEM DETAILS
    // =========================

    private fun openItemDetails(item: Item) {

        val intent =
            Intent(
                this,
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

    // =========================
    // DESTROY
    // =========================

    override fun onDestroy() {

        searchJob?.cancel()

        super.onDestroy()
    }
}