package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var searchBox: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvResult: TextView
    private lateinit var spinnerType: Spinner
    private lateinit var spinnerCategory: Spinner

    private val repository by lazy {
        ItemRepository(
            AppDatabase.getDatabase(this).itemDao()
        )
    }

    private var searchJob: Job? = null

    private val types = arrayOf(
        "All",
        "LOST",
        "FOUND"
    )

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

        setContentView(R.layout.activity_search)

        searchBox = findViewById(R.id.etSearchItems)
        recyclerView = findViewById(R.id.recyclerSearchResults)
        tvResult = findViewById(R.id.tvSearchResult)
        spinnerType = findViewById(R.id.spinnerType)
        spinnerCategory = findViewById(R.id.spinnerCategory)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        setupTypeSpinner()
        setupCategorySpinner()
        setupSearch()

        tvResult.text =
            "Enter an item name, category or location"
    }

    private fun setupTypeSpinner() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            types
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerType.adapter = adapter

        spinnerType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    performSearch()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
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
                    performSearch()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun setupSearch() {

        searchBox.requestFocus()

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

                    searchJob = lifecycleScope.launch {

                        delay(300)

                        performSearch()
                    }
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {
                }
            }
        )
    }

    private fun performSearch() {

        val query =
            searchBox.text.toString().trim()

        val selectedType =
            spinnerType.selectedItem?.toString() ?: "All"

        val selectedCategory =
            spinnerCategory.selectedItem?.toString()
                ?: "All Categories"

        lifecycleScope.launch {

            val databaseItems =
                withContext(Dispatchers.IO) {

                    repository.searchItems(query)
                }

            var filteredItems = databaseItems

            // Filter by type
            if (selectedType != "All") {

                filteredItems =
                    filteredItems.filter {
                        it.type == selectedType
                    }
            }

            // Filter by category
            if (selectedCategory != "All Categories") {

                filteredItems =
                    filteredItems.filter {
                        it.category == selectedCategory
                    }
            }

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

            if (query.isEmpty()) {

                recyclerView.adapter =
                    ItemAdapter(emptyList())

                tvResult.text =
                    "Enter an item name, category or location"

                return@launch
            }

            if (items.isEmpty()) {

                recyclerView.adapter =
                    ItemAdapter(emptyList())

                tvResult.text =
                    "No matching items found 📭"

            } else {

                tvResult.text =
                    "${items.size} item(s) found 🔎"

                recyclerView.adapter =
                    ItemAdapter(items) { item ->

                        val intent =
                            Intent(
                                this@SearchActivity,
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

    override fun onDestroy() {

        searchJob?.cancel()

        super.onDestroy()
    }
}