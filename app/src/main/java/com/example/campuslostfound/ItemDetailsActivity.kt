package com.example.campuslostfound

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ItemEntity
import com.example.campuslostfound.database.ItemRepository
import kotlinx.coroutines.launch

class ItemDetailsActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvType: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvStatus: TextView

    private lateinit var btnContactOwner: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button

    private val repository by lazy {
        ItemRepository(
            AppDatabase.getDatabase(this).itemDao()
        )
    }

    private var itemId = 0
    private var itemOwnerId = 0
    private var currentUserId = 0

    private lateinit var itemName: String
    private lateinit var description: String
    private lateinit var category: String
    private lateinit var type: String
    private lateinit var location: String
    private lateinit var date: String
    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_item_details)

        tvName = findViewById(R.id.tvDetailName)
        tvDescription = findViewById(R.id.tvDetailDescription)
        tvCategory = findViewById(R.id.tvDetailCategory)
        tvType = findViewById(R.id.tvDetailType)
        tvLocation = findViewById(R.id.tvDetailLocation)
        tvDate = findViewById(R.id.tvDetailDate)
        tvStatus = findViewById(R.id.tvDetailStatus)

        btnContactOwner = findViewById(R.id.btnContactOwner)
        btnEdit = findViewById(R.id.btnEditItem)
        btnDelete = findViewById(R.id.btnDeleteItem)

        itemId = intent.getIntExtra("itemId", 0)

        itemOwnerId = intent.getIntExtra("userId", 0)

        val preferences =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        currentUserId =
            preferences.getInt(
                "userId",
                0
            )

        itemName =
            intent.getStringExtra("itemName") ?: ""

        description =
            intent.getStringExtra("description") ?: ""

        category =
            intent.getStringExtra("category") ?: ""

        type =
            intent.getStringExtra("type") ?: ""

        location =
            intent.getStringExtra("location") ?: ""

        date =
            intent.getStringExtra("date") ?: ""

        status =
            intent.getStringExtra("status") ?: ""

        if (itemId == 0) {

            Toast.makeText(
                this,
                "Invalid item ID",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        showItemDetails()

        // Check ownership
        if (itemOwnerId != currentUserId) {

            btnEdit.isEnabled = false
            btnDelete.isEnabled = false

            btnEdit.alpha = 0.5f
            btnDelete.alpha = 0.5f
        }

        btnContactOwner.setOnClickListener {

            Toast.makeText(
                this,
                "Contact owner feature coming soon 📞",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnEdit.setOnClickListener {

            if (itemOwnerId != currentUserId) {

                Toast.makeText(
                    this,
                    "You can only edit your own items 🔒",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            showEditDialog()
        }

        btnDelete.setOnClickListener {

            if (itemOwnerId != currentUserId) {

                Toast.makeText(
                    this,
                    "You can only delete your own items 🔒",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            showDeleteDialog()
        }
    }

    private fun showItemDetails() {

        tvName.text = itemName

        tvDescription.text = description

        tvCategory.text = "Category: $category"

        tvType.text = "Type: $type"

        tvLocation.text = "Location: $location"

        tvDate.text = "Date: $date"

        tvStatus.text = "Status: $status"
    }

    private fun showEditDialog() {

        val dialogView =
            layoutInflater.inflate(
                R.layout.dialog_edit_item,
                null
            )

        val etName =
            dialogView.findViewById<EditText>(
                R.id.etEditName
            )

        val etDescription =
            dialogView.findViewById<EditText>(
                R.id.etEditDescription
            )

        val etCategory =
            dialogView.findViewById<EditText>(
                R.id.etEditCategory
            )

        val etLocation =
            dialogView.findViewById<EditText>(
                R.id.etEditLocation
            )

        val etDate =
            dialogView.findViewById<EditText>(
                R.id.etEditDate
            )

        etName.setText(itemName)
        etDescription.setText(description)
        etCategory.setText(category)
        etLocation.setText(location)
        etDate.setText(date)

        AlertDialog.Builder(this)
            .setTitle("✏️ Edit Item")
            .setView(dialogView)
            .setPositiveButton("UPDATE") { _, _ ->

                val updatedName =
                    etName.text.toString().trim()

                val updatedDescription =
                    etDescription.text.toString().trim()

                val updatedCategory =
                    etCategory.text.toString().trim()

                val updatedLocation =
                    etLocation.text.toString().trim()

                val updatedDate =
                    etDate.text.toString().trim()

                if (
                    updatedName.isEmpty() ||
                    updatedDescription.isEmpty() ||
                    updatedCategory.isEmpty() ||
                    updatedLocation.isEmpty() ||
                    updatedDate.isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                lifecycleScope.launch {

                    val rowsUpdated =
                        repository.updateItemByOwner(
                            itemId = itemId,
                            userId = currentUserId,
                            name = updatedName,
                            description = updatedDescription,
                            category = updatedCategory,
                            location = updatedLocation,
                            date = updatedDate
                        )

                    if (rowsUpdated > 0) {

                        itemName = updatedName
                        description = updatedDescription
                        category = updatedCategory
                        location = updatedLocation
                        date = updatedDate

                        showItemDetails()

                        Toast.makeText(
                            this@ItemDetailsActivity,
                            "Item updated successfully ✅",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Toast.makeText(
                            this@ItemDetailsActivity,
                            "Update failed 🔒",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun showDeleteDialog() {

        AlertDialog.Builder(this)
            .setTitle("🗑️ Delete Item")
            .setMessage(
                "Are you sure you want to delete this item?"
            )
            .setPositiveButton("DELETE") { _, _ ->

                lifecycleScope.launch {

                    val rowsDeleted =
                        repository.deleteItemByOwner(
                            itemId = itemId,
                            userId = currentUserId
                        )

                    if (rowsDeleted > 0) {

                        Toast.makeText(
                            this@ItemDetailsActivity,
                            "Item deleted successfully 🗑️",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this@ItemDetailsActivity,
                            "Delete failed 🔒",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }
}