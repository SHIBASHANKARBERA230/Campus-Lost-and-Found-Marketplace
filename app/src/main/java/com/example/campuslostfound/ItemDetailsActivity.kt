package com.example.campuslostfound

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ItemRepository
import com.example.campuslostfound.database.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemDetailsActivity : AppCompatActivity() {

    private lateinit var tvItemName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvType: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvStatus: TextView

    private lateinit var btnContactOwner: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button

    // Item repository
    private val itemRepository by lazy {
        ItemRepository(
            AppDatabase.getDatabase(this).itemDao()
        )
    }

    // User repository
    private val userRepository by lazy {
        UserRepository(
            AppDatabase.getDatabase(this).userDao()
        )
    }

    private var itemId = 0
    private var itemOwnerId = 0
    private var currentUserId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_item_details)

        // -----------------------------
        // Find Views
        // -----------------------------

        tvItemName = findViewById(R.id.tvItemName)
        tvDescription = findViewById(R.id.tvDescription)
        tvCategory = findViewById(R.id.tvCategory)
        tvType = findViewById(R.id.tvType)
        tvLocation = findViewById(R.id.tvLocation)
        tvDate = findViewById(R.id.tvDate)
        tvStatus = findViewById(R.id.tvStatus)

        btnContactOwner = findViewById(R.id.btnContactOwner)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)

        // -----------------------------
        // Get Item ID
        // -----------------------------

        itemId = intent.getIntExtra(
            "itemId",
            0
        )

        // Owner ID stored in item
        itemOwnerId = intent.getIntExtra(
            "userId",
            0
        )

        // -----------------------------
        // Get Current Logged-in User
        // -----------------------------

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

        // -----------------------------
        // Display Item Details
        // -----------------------------

        tvItemName.text =
            intent.getStringExtra("itemName")
                ?: "Unknown Item"

        tvDescription.text =
            "Description: ${
                intent.getStringExtra("description")
                    ?: ""
            }"

        tvCategory.text =
            "Category: ${
                intent.getStringExtra("category")
                    ?: ""
            }"

        tvType.text =
            "Type: ${
                intent.getStringExtra("type")
                    ?: ""
            }"

        tvLocation.text =
            "Location: ${
                intent.getStringExtra("location")
                    ?: ""
            }"

        tvDate.text =
            "Date: ${
                intent.getStringExtra("date")
                    ?: ""
            }"

        tvStatus.text =
            "Status: ${
                intent.getStringExtra("status")
                    ?: ""
            }"

        // -----------------------------
        // Owner / Other User
        // -----------------------------

        if (itemOwnerId == currentUserId) {

            // This is user's own item

            btnContactOwner.isEnabled = false

            btnContactOwner.text =
                "📞 THIS IS YOUR ITEM"

            btnEdit.visibility =
                Button.VISIBLE

            btnDelete.visibility =
                Button.VISIBLE

        } else {

            // Someone else's item

            btnContactOwner.isEnabled = true

            btnContactOwner.text =
                "📞 CONTACT OWNER"

            btnEdit.visibility =
                Button.GONE

            btnDelete.visibility =
                Button.GONE
        }

        // -----------------------------
        // Contact Owner
        // -----------------------------

        btnContactOwner.setOnClickListener {

            contactOwner()
        }

        // -----------------------------
        // Edit
        // -----------------------------

        btnEdit.setOnClickListener {

            showEditDialog()
        }

        // -----------------------------
        // Delete
        // -----------------------------

        btnDelete.setOnClickListener {

            confirmDelete()
        }
    }

    // =========================================================
    // CONTACT OWNER
    // =========================================================

    private fun contactOwner() {

        if (itemOwnerId == 0) {

            Toast.makeText(
                this,
                "Owner information not available ❌",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        lifecycleScope.launch {

            val owner =
                withContext(Dispatchers.IO) {

                    userRepository.getUserById(
                        itemOwnerId
                    )
                }

            if (owner == null) {

                Toast.makeText(
                    this@ItemDetailsActivity,
                    "Owner not found ❌",
                    Toast.LENGTH_SHORT
                ).show()

                return@launch
            }

            val ownerName =
                owner.name

            val phoneNumber =
                owner.phone

            if (phoneNumber.isBlank()) {

                Toast.makeText(
                    this@ItemDetailsActivity,
                    "Owner phone number not available ❌",
                    Toast.LENGTH_SHORT
                ).show()

                return@launch
            }

            AlertDialog.Builder(
                this@ItemDetailsActivity
            )
                .setTitle("📞 Contact Owner")
                .setMessage(
                    "Owner: $ownerName\n\n" +
                            "Phone: $phoneNumber\n\n" +
                            "Do you want to call this owner?"
                )
                .setNegativeButton(
                    "CANCEL",
                    null
                )
                .setPositiveButton(
                    "CALL"
                ) { _, _ ->

                    val callIntent =
                        Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse(
                                "tel:$phoneNumber"
                            )
                        )

                    startActivity(callIntent)
                }
                .show()
        }
    }

    // =========================================================
    // EDIT ITEM
    // =========================================================

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

        // Fill current values

        etName.setText(
            intent.getStringExtra(
                "itemName"
            ) ?: ""
        )

        etDescription.setText(
            intent.getStringExtra(
                "description"
            ) ?: ""
        )

        etCategory.setText(
            intent.getStringExtra(
                "category"
            ) ?: ""
        )

        etLocation.setText(
            intent.getStringExtra(
                "location"
            ) ?: ""
        )

        etDate.setText(
            intent.getStringExtra(
                "date"
            ) ?: ""
        )

        AlertDialog.Builder(this)
            .setTitle("✏️ Edit Item")
            .setView(dialogView)
            .setNegativeButton(
                "CANCEL",
                null
            )
            .setPositiveButton(
                "UPDATE"
            ) { _, _ ->

                updateItem(
                    name = etName.text
                        .toString()
                        .trim(),

                    description = etDescription.text
                        .toString()
                        .trim(),

                    category = etCategory.text
                        .toString()
                        .trim(),

                    location = etLocation.text
                        .toString()
                        .trim(),

                    date = etDate.text
                        .toString()
                        .trim()
                )
            }
            .show()
    }

    // =========================================================
    // UPDATE ITEM
    // =========================================================

    private fun updateItem(
        name: String,
        description: String,
        category: String,
        location: String,
        date: String
    ) {

        if (name.isEmpty()) {

            Toast.makeText(
                this,
                "Item name cannot be empty ❌",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        lifecycleScope.launch {

            val result =
                withContext(Dispatchers.IO) {

                    itemRepository.updateItemByOwner(
                        itemId = itemId,
                        userId = currentUserId,
                        name = name,
                        description = description,
                        category = category,
                        location = location,
                        date = date
                    )
                }

            if (result > 0) {

                // Update screen

                tvItemName.text =
                    name

                tvDescription.text =
                    "Description: $description"

                tvCategory.text =
                    "Category: $category"

                tvLocation.text =
                    "Location: $location"

                tvDate.text =
                    "Date: $date"

                // Update Intent values

                intent.putExtra(
                    "itemName",
                    name
                )

                intent.putExtra(
                    "description",
                    description
                )

                intent.putExtra(
                    "category",
                    category
                )

                intent.putExtra(
                    "location",
                    location
                )

                intent.putExtra(
                    "date",
                    date
                )

                Toast.makeText(
                    this@ItemDetailsActivity,
                    "Item updated successfully ✅",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this@ItemDetailsActivity,
                    "Update failed ❌",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // =========================================================
    // DELETE CONFIRMATION
    // =========================================================

    private fun confirmDelete() {

        AlertDialog.Builder(this)
            .setTitle("🗑️ Delete Item")
            .setMessage(
                "Are you sure you want to delete this item?"
            )
            .setNegativeButton(
                "CANCEL",
                null
            )
            .setPositiveButton(
                "DELETE"
            ) { _, _ ->

                deleteItem()
            }
            .show()
    }

    // =========================================================
    // DELETE ITEM
    // =========================================================

    private fun deleteItem() {

        lifecycleScope.launch {

            val result =
                withContext(Dispatchers.IO) {

                    itemRepository.deleteItemByOwner(
                        itemId = itemId,
                        userId = currentUserId
                    )
                }

            if (result > 0) {

                Toast.makeText(
                    this@ItemDetailsActivity,
                    "Item deleted successfully ✅",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } else {

                Toast.makeText(
                    this@ItemDetailsActivity,
                    "Delete failed ❌",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}