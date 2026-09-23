package com.example.campuslostfound

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ItemEntity
import com.example.campuslostfound.database.ItemRepository
import com.example.campuslostfound.database.NotificationEntity
import com.example.campuslostfound.database.NotificationRepository
import com.example.campuslostfound.viewmodel.ItemViewModel
import com.example.campuslostfound.viewmodel.ItemViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostItemActivity : AppCompatActivity() {

    private val itemRepository by lazy {
        ItemRepository(
            AppDatabase
                .getDatabase(this)
                .itemDao()
        )
    }

    private val notificationRepository by lazy {
        NotificationRepository(
            AppDatabase
                .getDatabase(this)
                .notificationDao()
        )
    }

    private val viewModel: ItemViewModel by viewModels {

        ItemViewModelFactory(
            itemRepository
        )
    }

    private var selectedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1001
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_post_item
        )

        val ivItemImage =
            findViewById<ImageView>(
                R.id.ivItemImage
            )

        val btnSelectImage =
            findViewById<Button>(
                R.id.btnSelectImage
            )

        val etItemName =
            findViewById<EditText>(
                R.id.etItemName
            )

        val etDescription =
            findViewById<EditText>(
                R.id.etDescription
            )

        val etCategory =
            findViewById<EditText>(
                R.id.etCategory
            )

        val etLocation =
            findViewById<EditText>(
                R.id.etLocation
            )

        val etDate =
            findViewById<EditText>(
                R.id.etDate
            )

        val rgType =
            findViewById<RadioGroup>(
                R.id.rgType
            )

        val btnSubmit =
            findViewById<Button>(
                R.id.btnSubmitItem
            )

        // SELECT IMAGE
        btnSelectImage.setOnClickListener {

            val intent =
                Intent(
                    Intent.ACTION_OPEN_DOCUMENT
                ).apply {

                    addCategory(
                        Intent.CATEGORY_OPENABLE
                    )

                    type = "image/*"
                }

            startActivityForResult(
                intent,
                PICK_IMAGE_REQUEST
            )
        }

        // POST ITEM
        btnSubmit.setOnClickListener {

            val name =
                etItemName.text
                    .toString()
                    .trim()

            val description =
                etDescription.text
                    .toString()
                    .trim()

            val category =
                etCategory.text
                    .toString()
                    .trim()

            val location =
                etLocation.text
                    .toString()
                    .trim()

            val date =
                etDate.text
                    .toString()
                    .trim()

            val type =
                when (
                    rgType.checkedRadioButtonId
                ) {

                    R.id.rbFound -> "FOUND"

                    else -> "LOST"
                }

            if (
                name.isEmpty() ||
                description.isEmpty() ||
                category.isEmpty() ||
                location.isEmpty() ||
                date.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

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

                Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            val item =
                ItemEntity(

                    userId = userId,

                    name = name,

                    description = description,

                    category = category,

                    type = type,

                    location = location,

                    date = date,

                    status = type,

                    imageUri =
                        selectedImageUri?.toString()
                )

            // Insert item and create matching notifications
            lifecycleScope.launch {

                try {

                    val newItemId =
                        withContext(Dispatchers.IO) {

                            itemRepository.insertItem(
                                item
                            )
                        }

                    val savedItem =
                        item.copy(
                            id = newItemId.toInt()
                        )

                    createMatchNotifications(
                        savedItem
                    )

                    Toast.makeText(
                        this@PostItemActivity,
                        "$type item posted successfully!",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()

                } catch (e: Exception) {

                    Toast.makeText(
                        this@PostItemActivity,
                        "Failed to post item",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private suspend fun createMatchNotifications(
        newItem: ItemEntity
    ) {

        // LOST searches for FOUND.
        // FOUND searches for LOST.
        val oppositeType =
            if (newItem.type == "LOST") {
                "FOUND"
            } else {
                "LOST"
            }

        val matchingItems =
            withContext(Dispatchers.IO) {

                itemRepository.findMatchingItems(

                    oppositeType =
                        oppositeType,

                    category =
                        newItem.category,

                    name =
                        newItem.name,

                    userId =
                        newItem.userId
                )
            }

        for (matchingItem in matchingItems) {

            val alreadyExists =
                withContext(Dispatchers.IO) {

                    notificationRepository
                        .notificationExists(
                            userId =
                                matchingItem.userId,

                            itemId =
                                newItem.id
                        )
                }

            if (alreadyExists) {
                continue
            }

            val notification =
                NotificationEntity(

                    userId =
                        matchingItem.userId,

                    title =
                        "Possible Item Match 🔔",

                    message =
                        "A ${newItem.type.lowercase()} item may " +
                                "match your ${matchingItem.type.lowercase()} " +
                                "item: ${matchingItem.name}. " +
                                "Category: ${newItem.category}.",

                    itemId =
                        newItem.id,

                    isRead =
                        false
                )

            withContext(Dispatchers.IO) {

                notificationRepository
                    .insertNotification(
                        notification
                    )
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode ==
            PICK_IMAGE_REQUEST &&
            resultCode ==
            RESULT_OK
        ) {

            val uri =
                data?.data

            if (uri != null) {

                selectedImageUri =
                    uri

                try {

                    contentResolver
                        .takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                } catch (
                    e: SecurityException
                ) {
                    // Provider does not support
                    // persistable permission.
                }

                findViewById<ImageView>(
                    R.id.ivItemImage
                ).setImageURI(uri)
            }
        }
    }
}