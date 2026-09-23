package com.example.campuslostfound

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.FavoriteEntity
import com.example.campuslostfound.database.ItemEntity
import com.example.campuslostfound.database.ItemRepository
import com.example.campuslostfound.database.UserRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemDetailsActivity : AppCompatActivity() {

    // =============================================
    // VIEWS
    // =============================================

    private lateinit var ivItemImage: ImageView

    private lateinit var tvItemName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvType: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvStatus: TextView

    private lateinit var btnContactOwner: Button
    private lateinit var btnClaimItem: Button
    private lateinit var btnFavorite: Button
    private lateinit var btnReport: Button
    private lateinit var btnEdit: Button
    private lateinit var btnMarkResolved: Button
    private lateinit var btnDelete: Button


    // =============================================
    // DATABASE
    // =============================================

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }


    // =============================================
    // REPOSITORIES
    // =============================================

    private val itemRepository by lazy {
        ItemRepository(
            database.itemDao()
        )
    }

    private val userRepository by lazy {
        UserRepository(
            database.userDao()
        )
    }


    // =============================================
    // VARIABLES
    // =============================================

    private var itemId: Int = 0

    private var itemOwnerId: Int = 0

    private var currentUserId: Int = 0

    private var isFavorite = false


    // =============================================
    // ON CREATE
    // =============================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_item_details
        )


        // =========================================
        // IMAGE
        // =========================================

        ivItemImage =
            findViewById(R.id.ivItemImage)


        // =========================================
        // TEXT VIEWS
        // =========================================

        tvItemName =
            findViewById(R.id.tvItemName)

        tvDescription =
            findViewById(R.id.tvDescription)

        tvCategory =
            findViewById(R.id.tvCategory)

        tvType =
            findViewById(R.id.tvType)

        tvLocation =
            findViewById(R.id.tvLocation)

        tvDate =
            findViewById(R.id.tvDate)

        tvStatus =
            findViewById(R.id.tvStatus)


        // =========================================
        // BUTTONS
        // =========================================

        btnContactOwner =
            findViewById(R.id.btnContactOwner)

        btnClaimItem =
            findViewById(R.id.btnClaimItem)

        btnFavorite =
            findViewById(R.id.btnFavorite)

        btnReport =
            findViewById(R.id.btnReport)

        btnEdit =
            findViewById(R.id.btnEdit)

        btnMarkResolved =
            findViewById(R.id.btnMarkResolved)

        btnDelete =
            findViewById(R.id.btnDelete)


        // =========================================
        // GET ITEM ID
        // =========================================

        itemId =
            intent.getIntExtra(
                "itemId",
                0
            )


        if (itemId == 0) {

            Toast.makeText(
                this,
                "Invalid item ID ❌",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        // =========================================
        // CURRENT USER
        // =========================================

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


        // =========================================
        // FAVORITE BUTTON
        // =========================================

        btnFavorite.setOnClickListener {

            toggleFavorite()
        }


        // =========================================
        // REPORT BUTTON
        // =========================================

        btnReport.setOnClickListener {

            openReportScreen()
        }


        // =========================================
        // LOAD ITEM
        // =========================================

        loadItem()
    }


    // =============================================
    // LOAD ITEM
    // =============================================

    private fun loadItem() {

        lifecycleScope.launch {

            val item: ItemEntity? =
                withContext(Dispatchers.IO) {

                    itemRepository.getItemById(
                        itemId
                    )
                }


            // =====================================
            // ITEM NOT FOUND
            // =====================================

            if (item == null) {

                Toast.makeText(
                    this@ItemDetailsActivity,
                    "Item not found ❌",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

                return@launch
            }


            // =====================================
            // LOAD ITEM DATA
            // =====================================

            tvItemName.text =
                item.name

            tvDescription.text =
                "Description: ${item.description}"

            tvCategory.text =
                "Category: ${item.category}"

            tvType.text =
                "Type: ${item.type}"

            tvLocation.text =
                "Location: ${item.location}"

            tvDate.text =
                "Date: ${item.date}"

            tvStatus.text =
                "Status: ${item.status}"


            // =====================================
            // LOAD IMAGE
            // =====================================

            loadItemImage(item)


            // =====================================
            // OWNER
            // =====================================

            itemOwnerId =
                item.userId


            // =====================================
            // LOAD FAVORITE STATUS
            // =====================================

            loadFavoriteStatus()


            // =====================================
            // CHECK OWNERSHIP
            // =====================================

            if (itemOwnerId == currentUserId) {

                // =================================
                // USER'S OWN ITEM
                // =================================

                btnContactOwner.isEnabled =
                    false

                btnContactOwner.text =
                    "📞 THIS IS YOUR ITEM"

                btnClaimItem.visibility =
                    View.GONE

                btnReport.visibility =
                    View.GONE

                btnEdit.visibility =
                    View.VISIBLE

                btnDelete.visibility =
                    View.VISIBLE

                btnMarkResolved.visibility =
                    View.VISIBLE


                // ================================
                // ALREADY RESOLVED
                // ================================

                if (
                    item.status.equals(
                        "RESOLVED",
                        ignoreCase = true
                    )
                ) {

                    btnMarkResolved.isEnabled =
                        false

                    btnMarkResolved.text =
                        "✅ ITEM RESOLVED"
                }

            } else {

                // =================================
                // SOMEONE ELSE'S ITEM
                // =================================

                btnContactOwner.isEnabled =
                    true

                btnContactOwner.text =
                    "📞 CONTACT OWNER"

                btnReport.visibility =
                    View.VISIBLE

                btnEdit.visibility =
                    View.GONE

                btnDelete.visibility =
                    View.GONE

                btnMarkResolved.visibility =
                    View.GONE


                // =================================
                // CLAIM FEATURE
                // =================================

                /*
                 * Only FOUND items can be claimed.
                 *
                 * RESOLVED items cannot be claimed.
                 */

                if (
                    item.type.equals(
                        "FOUND",
                        ignoreCase = true
                    ) &&
                    !item.status.equals(
                        "RESOLVED",
                        ignoreCase = true
                    )
                ) {

                    btnClaimItem.visibility =
                        View.VISIBLE

                    btnClaimItem.isEnabled =
                        true

                    btnClaimItem.text =
                        "🔵 CLAIM THIS ITEM"

                    btnClaimItem.setOnClickListener {

                        val intent =
                            Intent(
                                this@ItemDetailsActivity,
                                ClaimItemActivity::class.java
                            )

                        intent.putExtra(
                            "itemId",
                            item.id
                        )

                        startActivity(intent)
                    }

                } else {

                    btnClaimItem.visibility =
                        View.GONE
                }
            }


            // =====================================
            // BUTTON LISTENERS
            // =====================================

            btnContactOwner.setOnClickListener {

                contactOwner()
            }


            btnEdit.setOnClickListener {

                showEditDialog()
            }


            btnMarkResolved.setOnClickListener {

                markItemAsResolved()
            }


            btnDelete.setOnClickListener {

                confirmDelete()
            }
        }
    }


    // =============================================
    // REPORT ITEM
    // =============================================

    private fun openReportScreen() {

        if (currentUserId == 0) {

            Toast.makeText(
                this,
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        if (itemOwnerId == currentUserId) {

            Toast.makeText(
                this,
                "You cannot report your own item",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val intent =
            Intent(
                this,
                ReportItemActivity::class.java
            )

        intent.putExtra(
            "itemId",
            itemId
        )

        intent.putExtra(
            "ownerUserId",
            itemOwnerId
        )

        startActivity(intent)
    }


    // =============================================
    // LOAD FAVORITE STATUS
    // =============================================

    private fun loadFavoriteStatus() {

        if (currentUserId == 0) {

            btnFavorite.isEnabled =
                false

            btnFavorite.text =
                "⭐ LOGIN TO SAVE"

            return
        }


        lifecycleScope.launch {

            val result =
                withContext(Dispatchers.IO) {

                    database
                        .favoriteDao()
                        .isFavorite(
                            currentUserId,
                            itemId
                        )
                }

            isFavorite =
                result

            updateFavoriteButton()
        }
    }


    // =============================================
    // TOGGLE FAVORITE
    // =============================================

    private fun toggleFavorite() {

        if (currentUserId == 0) {

            Toast.makeText(
                this,
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        lifecycleScope.launch {

            withContext(Dispatchers.IO) {

                if (isFavorite) {

                    database
                        .favoriteDao()
                        .removeFavorite(
                            currentUserId,
                            itemId
                        )

                } else {

                    database
                        .favoriteDao()
                        .addFavorite(
                            FavoriteEntity(
                                userId =
                                    currentUserId,

                                itemId =
                                    itemId
                            )
                        )
                }
            }


            isFavorite =
                !isFavorite

            updateFavoriteButton()


            if (isFavorite) {

                Toast.makeText(
                    this@ItemDetailsActivity,
                    "Added to favorites ⭐",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this@ItemDetailsActivity,
                    "Removed from favorites",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    // =============================================
    // UPDATE FAVORITE BUTTON
    // =============================================

    private fun updateFavoriteButton() {

        if (isFavorite) {

            btnFavorite.text =
                "★ REMOVE FROM FAVORITES"

        } else {

            btnFavorite.text =
                "☆ ADD TO FAVORITES"
        }
    }


    // =============================================
    // LOAD IMAGE
    // =============================================

    private fun loadItemImage(
        item: ItemEntity
    ) {

        if (!item.imageUri.isNullOrEmpty()) {

            try {

                ivItemImage.setImageURI(
                    Uri.parse(
                        item.imageUri
                    )
                )

            } catch (e: Exception) {

                ivItemImage.setImageResource(
                    android.R.drawable.ic_menu_gallery
                )
            }

        } else {

            ivItemImage.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }
    }


    // =============================================
    // CONTACT OWNER
    // =============================================

    private fun contactOwner() {

        if (itemOwnerId == currentUserId) {

            Toast.makeText(
                this,
                "This is your own item",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        if (itemOwnerId == 0) {

            Toast.makeText(
                this,
                "Owner information unavailable ❌",
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
                .setTitle(
                    "📞 Contact Owner"
                )
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

                    startActivity(
                        callIntent
                    )
                }
                .show()
        }
    }


    // =============================================
    // EDIT ITEM
    // =============================================

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


        // =========================================
        // LOAD CURRENT VALUES
        // =========================================

        lifecycleScope.launch {

            val item =
                withContext(Dispatchers.IO) {

                    itemRepository.getItemById(
                        itemId
                    )
                }


            if (item == null) {

                Toast.makeText(
                    this@ItemDetailsActivity,
                    "Item not found ❌",
                    Toast.LENGTH_SHORT
                ).show()

                return@launch
            }


            etName.setText(
                item.name
            )


            etDescription.setText(
                item.description
            )


            etCategory.setText(
                item.category
            )


            etLocation.setText(
                item.location
            )


            etDate.setText(
                item.date
            )


            AlertDialog.Builder(
                this@ItemDetailsActivity
            )
                .setTitle(
                    "✏️ Edit Item"
                )
                .setView(dialogView)
                .setNegativeButton(
                    "CANCEL",
                    null
                )
                .setPositiveButton(
                    "UPDATE"
                ) { _, _ ->

                    updateItem(

                        name =
                            etName.text
                                .toString()
                                .trim(),

                        description =
                            etDescription.text
                                .toString()
                                .trim(),

                        category =
                            etCategory.text
                                .toString()
                                .trim(),

                        location =
                            etLocation.text
                                .toString()
                                .trim(),

                        date =
                            etDate.text
                                .toString()
                                .trim()
                    )
                }
                .show()
        }
    }


    // =============================================
    // UPDATE ITEM
    // =============================================

    private fun updateItem(
        name: String,
        description: String,
        category: String,
        location: String,
        date: String
    ) {

        // =========================================
        // VALIDATION
        // =========================================

        if (name.isEmpty()) {

            Toast.makeText(
                this,
                "Item name cannot be empty ❌",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        if (description.isEmpty()) {

            Toast.makeText(
                this,
                "Description cannot be empty ❌",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        if (category.isEmpty()) {

            Toast.makeText(
                this,
                "Category cannot be empty ❌",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        if (location.isEmpty()) {

            Toast.makeText(
                this,
                "Location cannot be empty ❌",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        if (date.isEmpty()) {

            Toast.makeText(
                this,
                "Date cannot be empty ❌",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // =========================================
        // UPDATE DATABASE
        // =========================================

        lifecycleScope.launch {

            val result =
                withContext(Dispatchers.IO) {

                    itemRepository.updateItemByOwner(

                        itemId =
                            itemId,

                        userId =
                            currentUserId,

                        name =
                            name,

                        description =
                            description,

                        category =
                            category,

                        location =
                            location,

                        date =
                            date
                    )
                }


            // =====================================
            // UPDATE SUCCESS
            // =====================================

            if (result > 0) {

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


    // =============================================
    // MARK ITEM AS RESOLVED
    // =============================================

    private fun markItemAsResolved() {

        AlertDialog.Builder(this)
            .setTitle(
                "✅ Mark as Resolved"
            )
            .setMessage(
                "Are you sure you want to mark this item as resolved?"
            )
            .setNegativeButton(
                "CANCEL",
                null
            )
            .setPositiveButton(
                "RESOLVE"
            ) { _, _ ->

                lifecycleScope.launch {

                    val result =
                        withContext(Dispatchers.IO) {

                            itemRepository
                                .updateItemStatusByOwner(

                                    itemId =
                                        itemId,

                                    userId =
                                        currentUserId,

                                    status =
                                        "RESOLVED"
                                )
                        }


                    if (result > 0) {

                        tvStatus.text =
                            "Status: RESOLVED"

                        btnMarkResolved.isEnabled =
                            false

                        btnMarkResolved.text =
                            "✅ ITEM RESOLVED"

                        Toast.makeText(
                            this@ItemDetailsActivity,
                            "Item marked as resolved ✅",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Toast.makeText(
                            this@ItemDetailsActivity,
                            "Failed to update status ❌",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .show()
    }


    // =============================================
    // DELETE ITEM
    // =============================================

    private fun confirmDelete() {

        AlertDialog.Builder(this)
            .setTitle(
                "🗑️ Delete Item"
            )
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


    // =============================================
    // DELETE ITEM
    // =============================================

    private fun deleteItem() {

        lifecycleScope.launch {

            val result =
                withContext(Dispatchers.IO) {

                    itemRepository.deleteItemByOwner(

                        itemId =
                            itemId,

                        userId =
                            currentUserId
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