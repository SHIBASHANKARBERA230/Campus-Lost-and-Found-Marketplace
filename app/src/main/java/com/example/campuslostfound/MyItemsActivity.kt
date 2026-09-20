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

class MyItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvResult: TextView
    private lateinit var spinnerStatus: Spinner

    private val repository by lazy {
        ItemRepository(
            AppDatabase
                .getDatabase(this)
                .itemDao()
        )
    }

    private val statusFilters = arrayOf(
        "All Items",
        "LOST",
        "FOUND",
        "RESOLVED"
    )

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_my_items
        )

        recyclerView =
            findViewById(
                R.id.recyclerMyItems
            )

        tvResult =
            findViewById(
                R.id.tvMyItemsResult
            )

        spinnerStatus =
            findViewById(
                R.id.spinnerStatus
            )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        setupStatusSpinner()

        loadMyItems("All Items")
    }

    private fun setupStatusSpinner() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            statusFilters
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerStatus.adapter = adapter

        spinnerStatus.onItemSelectedListener =
            object :
                AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val selectedStatus =
                        statusFilters[position]

                    loadMyItems(
                        selectedStatus
                    )
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun loadMyItems(
        selectedStatus: String
    ) {

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

            recyclerView.adapter =
                ItemAdapter(
                    emptyList()
                ) {
                }

            return
        }

        lifecycleScope.launch {

            val databaseItems =
                withContext(Dispatchers.IO) {

                    repository.getItemsByUser(
                        userId
                    )
                }

            val filteredItems: List<ItemEntity> =
                if (selectedStatus == "All Items") {

                    databaseItems

                } else {

                    databaseItems.filter {
                        it.status == selectedStatus
                    }
                }

            if (filteredItems.isEmpty()) {

                tvResult.text =
                    when (selectedStatus) {

                        "All Items" ->
                            "You have not posted any items yet 📭"

                        else ->
                            "No $selectedStatus items found 📭"
                    }

                recyclerView.adapter =
                    ItemAdapter(
                        emptyList()
                    ) {
                    }

            } else {

                tvResult.text =
                    when (selectedStatus) {

                        "All Items" ->
                            "${filteredItems.size} item(s) posted by you"

                        else ->
                            "${filteredItems.size} $selectedStatus item(s)"
                    }

                recyclerView.adapter =
                    ItemAdapter(
                        filteredItems
                    ) { item ->

                        openItemDetails(
                            item
                        )
                    }
            }
        }
    }

    private fun openItemDetails(
        item: ItemEntity
    ) {

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

    override fun onResume() {

        super.onResume()

        if (::recyclerView.isInitialized) {

            val selectedPosition =
                spinnerStatus.selectedItemPosition

            val selectedStatus =
                statusFilters[
                    selectedPosition
                ]

            loadMyItems(
                selectedStatus
            )
        }
    }
}