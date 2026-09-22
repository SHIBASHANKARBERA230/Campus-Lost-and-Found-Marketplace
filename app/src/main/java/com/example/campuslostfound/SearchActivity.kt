package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.campuslostfound.database.ItemEntity
import com.example.campuslostfound.database.ItemRepository

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
    private lateinit var spinnerLocation: Spinner

    private val repository by lazy {
        ItemRepository(
            AppDatabase
                .getDatabase(this)
                .itemDao()
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

    /*
     * These are filter options.
     * Add/change locations here according to your campus.
     */
    private val locations = arrayOf(
        "All Locations",
        "Library",
        "Canteen",
        "Hostel",
        "Classroom",
        "Laboratory",
        "Main Gate",
        "Parking",
        "Auditorium",
        "Sports Ground",
        "Other"
    )

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_search
        )

        searchBox =
            findViewById(R.id.etSearchItems)

        recyclerView =
            findViewById(R.id.recyclerSearchResults)

        tvResult =
            findViewById(R.id.tvSearchResult)

        spinnerType =
            findViewById(R.id.spinnerType)

        spinnerCategory =
            findViewById(R.id.spinnerCategory)

        spinnerLocation =
            findViewById(R.id.spinnerLocation)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        setupTypeSpinner()
        setupCategorySpinner()
        setupLocationSpinner()
        setupSearch()

        val initialQuery =
            intent.getStringExtra("searchQuery")

        if (!initialQuery.isNullOrBlank()) {

            searchBox.setText(initialQuery)

            searchBox.setSelection(
                searchBox.text.length
            )

            searchBox.post {
                performSearch()
            }

        } else {

            tvResult.text =
                "Enter an item name, category or location"
        }
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

    private fun setupLocationSpinner() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            locations
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerLocation.adapter = adapter

        spinnerLocation.onItemSelectedListener =
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

        searchBox.addTextChangedListener(
            object : TextWatcher {

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

                            performSearch()
                        }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    private fun performSearch() {

        val query =
            searchBox.text
                .toString()
                .trim()

        val selectedType =
            spinnerType.selectedItem
                ?.toString()
                ?: "All"

        val selectedCategory =
            spinnerCategory.selectedItem
                ?.toString()
                ?: "All Categories"

        val selectedLocation =
            spinnerLocation.selectedItem
                ?.toString()
                ?: "All Locations"

        /*
         * Don't require search text anymore.
         *
         * This allows users to:
         * - filter only LOST items
         * - filter only FOUND items
         * - filter by category
         * - filter by location
         */
        lifecycleScope.launch {

            val databaseItems:
                    List<ItemEntity> =
                withContext(Dispatchers.IO) {

                    if (query.isEmpty()) {
                        repository.getAllItems()
                    } else {
                        repository.searchItems(query)
                    }
                }

            var filteredItems =
                databaseItems

            /*
             * LOST / FOUND filter.
             */
            if (selectedType != "All") {

                filteredItems =
                    filteredItems.filter { item ->

                        item.type.equals(
                            selectedType,
                            ignoreCase = true
                        )
                    }
            }

            /*
             * Category filter.
             */
            if (selectedCategory != "All Categories") {

                filteredItems =
                    filteredItems.filter { item ->

                        item.category.equals(
                            selectedCategory,
                            ignoreCase = true
                        )
                    }
            }

            /*
             * Location filter.
             *
             * Contains is used instead of exact matching,
             * so "Library - Ground Floor" can match
             * the "Library" filter.
             */
            if (selectedLocation != "All Locations") {

                filteredItems =
                    filteredItems.filter { item ->

                        item.location.contains(
                            selectedLocation,
                            ignoreCase = true
                        )
                    }
            }

            if (filteredItems.isEmpty()) {

                recyclerView.adapter =
                    ItemAdapter(emptyList()) {
                        // No item
                    }

                tvResult.text =
                    "No matching items found 📭"

                return@launch
            }

            tvResult.text =
                "${filteredItems.size} item(s) found 🔎"

            recyclerView.adapter =
                ItemAdapter(
                    filteredItems
                ) { item ->

                    openItemDetails(item)
                }
        }
    }

    private fun openItemDetails(
        item: ItemEntity
    ) {

        val intent = Intent(
            this,
            ItemDetailsActivity::class.java
        )

        intent.putExtra(
            "itemId",
            item.id
        )

        startActivity(intent)
    }

    override fun onDestroy() {

        searchJob?.cancel()

        super.onDestroy()
    }
}